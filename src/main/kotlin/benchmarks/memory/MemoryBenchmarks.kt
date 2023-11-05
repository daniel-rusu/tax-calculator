package benchmarks.memory

import benchmarks.ResultPrinter
import dataModel.TaxBracket
import dataModel.TaxCalculator
import sampleData.SampleTaxBrackets
import solutions.constantTime.iteration4.GCDTaxCalculator
import solutions.constantTime.iteration5.MinBracketTaxCalculator
import solutions.constantTime.iteration6.RegionTaxCalculator
import java.text.NumberFormat
import kotlin.random.Random
import kotlin.reflect.full.memberProperties
import kotlin.time.TimeSource

private val currencyFormatter = NumberFormat.getCurrencyInstance()
private val numberFormatter = NumberFormat.getNumberInstance()

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
    val memoryAnalyzer = MemoryAnalyzer(cacheShallowSizes = true)
    val timeSource = TimeSource.Monotonic
    val startTime = timeSource.markNow()

    ResultPrinter.printSectionHeading("Sample datasets:")
    for (property in SampleTaxBrackets::class.memberProperties) {
        @Suppress("UNCHECKED_CAST")
        printDatasetMemoryUsageOfDataset(
            datasetName = property.name,
            taxBrackets = property.get(SampleTaxBrackets) as List<TaxBracket>,
            memoryAnalyzer = memoryAnalyzer,
        )
    }
    for (numBrackets in listOf(10)) {//}, 20, 50)) {
        val lowerBoundDollarsOfHighestBracket = 350_000

        val upperBound = currencyFormatter.format(lowerBoundDollarsOfHighestBracket)
        ResultPrinter.printSectionHeading("$numBrackets random tax brackets up to $upperBound:")

        val seed = System.currentTimeMillis() + System.nanoTime()
        // Start low since it takes a long time for the less efficient algorithms and gradually increase this value
        // to improve the accuracy of the results
        var numSamples = 100
        for (taxCalculatorGenerator in taxCalculatorGenerators) {
            println("Running simulation with ${numberFormatter.format(numSamples)} samples...")
            simulateScenarios(
                seed = seed,
                numSamples = numSamples,
                percentiles = listOf(50, 90, 95),
                lowerBoundDollarsOfHighestBracket = lowerBoundDollarsOfHighestBracket,
                numBrackets = numBrackets,
                memoryAnalyzer = memoryAnalyzer,
                createTaxCalculator = taxCalculatorGenerator,
            )
            numSamples *= 10
        }
    }
    val time = timeSource.markNow() - startTime
    println("Analysis completed in $time")
}

private fun printDatasetMemoryUsageOfDataset(
    datasetName: String,
    taxBrackets: List<TaxBracket>,
    memoryAnalyzer: MemoryAnalyzer,
) {
    val usages = taxCalculatorGenerators.map { generateTaxCalculatorFor ->
        val taxCalculator = generateTaxCalculatorFor(taxBrackets)

        MemoryUsage(
            scenario = taxCalculator::class.java.simpleName,
            memoryUsageBytes = memoryAnalyzer.sizeOf(taxCalculator)
        )
    }
    ResultPrinter.printMemoryUsage(title = "[$datasetName]", scenarioTitle = "Algorithm", results = usages)
}

private fun simulateScenarios(
    seed: Long,
    numSamples: Int,
    percentiles: List<Int>,
    lowerBoundDollarsOfHighestBracket: Int,
    numBrackets: Int,
    memoryAnalyzer: MemoryAnalyzer,
    createTaxCalculator: (List<TaxBracket>) -> TaxCalculator,
) {
    val random = Random(seed)
    val percentileResults = memoryAnalyzer.runMonteCarloSimulationAndMeasureMemoryUsage(
        random,
        numSamples,
        percentiles,
        lowerBoundDollarsOfHighestBracket,
        numBrackets,
        createTaxCalculator,
    )

    val results = percentiles.mapIndexed { index, percentile ->
        MemoryUsage(scenario = "P$percentile", percentileResults[index])
    }

    // Create a temporary tax calculator instance to get the class name
    val algorithmName = createTaxCalculator(SampleTaxBrackets.bracketsWithTinyRange)::class.simpleName
    ResultPrinter.printMemoryUsage(title = "[$algorithmName]", scenarioTitle = "Percentile", results = results)
}
