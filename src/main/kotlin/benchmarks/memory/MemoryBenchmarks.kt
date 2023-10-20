package benchmarks.memory

import benchmarks.ResultPrinter
import dataModel.Money.Companion.dollars
import dataModel.Percent.Companion.percent
import dataModel.TaxBracket
import dataModel.TaxCalculator
import org.github.jamm.MemoryMeter
import sampleData.SampleTaxBrackets
import solutions.constantTime.iteration4.GCDTaxCalculator
import solutions.constantTime.iteration5.MinBracketTaxCalculator
import solutions.constantTime.iteration6.RegionTaxCalculator
import utilities.TaxBracketGenerator
import java.text.NumberFormat
import kotlin.math.roundToLong
import kotlin.random.Random
import kotlin.reflect.full.memberProperties
import kotlin.time.TimeSource

private val currencyFormatter = NumberFormat.getCurrencyInstance()

/**
 * List of generators that create tax calculators.
 *
 * Note:
 * Skipping iterations 1, 2, and 3 as they're too inefficient.  Iteration 3 uses about 3.5 gigs of heap for the Texas
 * tax brackets and requires a 16 gig heap size to be analyzed since the memory analyzer uses lots of temporary memory.
 */
private val taxCalculatorGenerators = listOf(
    // iteration 1
    //{ taxBrackets: List<TaxBracket> -> MemorizedTaxCalculator(taxBrackets) },

    // iteration 2
    //{ taxBrackets: List<TaxBracket> -> BoundedMemorizedTaxCalculator(taxBrackets) },

    // iteration 3
    //{ taxBrackets: List<TaxBracket> -> MemorizedBracketTaxCalculator(taxBrackets) },

    // iteration 4
    { taxBrackets: List<TaxBracket> -> GCDTaxCalculator(taxBrackets) },

    // iteration 5
    { taxBrackets: List<TaxBracket> -> MinBracketTaxCalculator(taxBrackets) },

    // iteration 6
    { taxBrackets: List<TaxBracket> -> RegionTaxCalculator(taxBrackets) },
)

fun main() {
    val timeSource = TimeSource.Monotonic
    val startTime = timeSource.markNow()
    val memoryMeter = MemoryMeter.builder().build()

    printHeading("Sample datasets:")
    for (property in SampleTaxBrackets::class.memberProperties) {
        @Suppress("UNCHECKED_CAST")
        printDatasetMemoryUsageOfDataset(
            datasetName = property.name,
            taxBrackets = property.get(SampleTaxBrackets) as List<TaxBracket>,
            memoryMeter = memoryMeter,
        )
    }
    for (numBrackets in listOf(10, 20, 50)) {
        val lowerBoundDollarsOfHighestBracket = 175_000
        val numSamples = 100

        val upperBound = currencyFormatter.format(lowerBoundDollarsOfHighestBracket)
        printHeading("$numBrackets random tax brackets up to $upperBound ($numSamples samples):")

        val seed = System.currentTimeMillis() + System.nanoTime()
        for (taxCalculatorGenerator in taxCalculatorGenerators) {
            simulateScenarios(
                seed = seed,
                numSamples = numSamples,
                lowerBoundDollarsOfHighestBracket = lowerBoundDollarsOfHighestBracket,
                numBrackets = numBrackets,
                memoryMeter = memoryMeter,
                createTaxCalculator = taxCalculatorGenerator,
            )
        }
    }
    val time = timeSource.markNow() - startTime
    println("Analysis completed in $time")
}

private fun printHeading(heading: String) {
    val separator = "=".repeat(heading.length)
    println(separator)
    println(heading)
    println(separator)
}

private fun printDatasetMemoryUsageOfDataset(
    datasetName: String,
    taxBrackets: List<TaxBracket>,
    memoryMeter: MemoryMeter,
) {
    val usages = taxCalculatorGenerators.map { generateTaxCalculatorFor ->
        val taxCalculator = generateTaxCalculatorFor(taxBrackets)

        MemoryUsage(
            scenario = taxCalculator::class.java.simpleName,
            memoryUsageBytes = memoryMeter.measureDeep(taxCalculator)
        )
    }

    ResultPrinter.printMemoryUsage(title = "[$datasetName]", scenarioTitle = "Algorithm", results = usages)
}

private fun simulateScenarios(
    seed: Long,
    numSamples: Int,
    lowerBoundDollarsOfHighestBracket: Int,
    numBrackets: Int,
    memoryMeter: MemoryMeter,
    createTaxCalculator: (List<TaxBracket>) -> TaxCalculator,
) {
    val random = Random(seed)
    val memoryUsages = mutableListOf<Long>()
    repeat(numSamples) {
        val taxBrackets = TaxBracketGenerator.generateTaxBrackets(
            numBrackets,
            lowerBoundDollarsOfHighestBracket,
            random,
        )
        val taxCalculator = createTaxCalculator(taxBrackets)
        memoryUsages += memoryMeter.measureDeep(taxCalculator)
    }
    memoryUsages.sort()

    val results = listOf(50, 90, 95).map { percentile ->
        MemoryUsage(scenario = "P$percentile", computePercentile(percentile, memoryUsages))
    }

    // Create a temporary tax calculator instance to get the class name
    val algorithmName = createTaxCalculator(
        listOf(TaxBracket(taxRate = 10.percent, from = 0.dollars))
    )::class.java.simpleName

    ResultPrinter.printMemoryUsage(title = "[$algorithmName]", scenarioTitle = "Percentile", results = results)
}

private fun computePercentile(percentile: Int, values: List<Long>): Long {
    val floatingIndex = percentile / 100.0 * (values.size - 1)
    val index = floatingIndex.toInt()
    val remainder = floatingIndex - index

    if (remainder == 0.0) return values[index]

    // Compute the weighted average based on how close it is to each index
    return (values[index] * (1.0 - remainder) + values[index + 1] * remainder).roundToLong()
}
