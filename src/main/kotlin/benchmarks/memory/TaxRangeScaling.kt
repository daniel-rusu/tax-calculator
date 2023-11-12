package benchmarks.memory

import benchmarks.tools.MemoryAnalyzer
import benchmarks.tools.ResultPrinter
import dataModel.TaxBracket
import dataModel.TaxCalculator
import sampleData.SampleTaxBrackets
import solutions.constantTime.iteration4.GCDTaxCalculator
import solutions.constantTime.iteration5.MinBracketTaxCalculator
import solutions.constantTime.iteration6.RegionTaxCalculator
import java.text.NumberFormat
import kotlin.random.Random

private const val COLUMN_SPACING = 2

private val currencyFormatter = NumberFormat.getCurrencyInstance().apply {
    minimumFractionDigits = 0
}

private val numberFormatter = NumberFormat.getNumberInstance()

fun main() {
    val seed = System.currentTimeMillis() + System.nanoTime()

    // Since these take so long, starting with fewer samples and increasing as we measure more efficient algorithms
    evaluateTaxRangeScalability(
        seed = seed,
        numSamplesPerIteration = 100,
    ) { taxBrackets ->
        GCDTaxCalculator(taxBrackets)
    }

    evaluateTaxRangeScalability(
        seed = seed,
        numSamplesPerIteration = 1_000,
    ) { taxBrackets ->
        MinBracketTaxCalculator(taxBrackets)
    }

    evaluateTaxRangeScalability(
        seed = seed,
        numSamplesPerIteration = 10_000,
    ) { taxBrackets ->
        RegionTaxCalculator(taxBrackets)
    }
}

private fun evaluateTaxRangeScalability(
    seed: Long,
    numSamplesPerIteration: Int,
    percentile: Int = 50,
    numBrackets: Int = 10,
    lowerBoundDollarsOfHighestBracketStart: Int = 200_000,
    lowerBoundDollarsOfHighestBracketEnd: Int = 1_000_000,
    stepSize: Int = 50_000,
    createTaxCalculator: (List<TaxBracket>) -> TaxCalculator,
) {
    val algorithm = createTaxCalculator(SampleTaxBrackets.bracketsWithTinyRange)::class.simpleName!!
    ResultPrinter.printSectionHeading("$algorithm tax range scaling with $numBrackets brackets")

    val column1Title = "Highest bracket" + " ".repeat(COLUMN_SPACING)
    val column2Title = "Memory (bytes) - ${percentile}th percentile"
    val header = column1Title + column2Title
    println(header)
    println("-".repeat(header.length))

    val random = Random(seed)
    val memoryAnalyzer = MemoryAnalyzer(cacheShallowSizes = true)

    val highestBracketStartingRange = lowerBoundDollarsOfHighestBracketStart..lowerBoundDollarsOfHighestBracketEnd
    for (lowerBoundDollarsOfHighestBracket in highestBracketStartingRange step stepSize) {
        val memoryBytes = memoryAnalyzer.runMonteCarloSimulationAndMeasureMemoryUsage(
            random,
            numSamplesPerIteration,
            percentile,
            lowerBoundDollarsOfHighestBracket,
            numBrackets,
            createTaxCalculator,
        )

        val lowerBound = currencyFormatter.format(lowerBoundDollarsOfHighestBracket)
        print(lowerBound.padEnd(length = column1Title.length))
        println(numberFormatter.format(memoryBytes))
    }
    println()
}
