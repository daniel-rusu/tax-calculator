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
    evaluateBracketScalability(
        seed = seed,
        numSamplesPerIteration = 100,
    ) { taxBrackets ->
        GCDTaxCalculator(taxBrackets)
    }

    evaluateBracketScalability(
        seed = seed,
        numSamplesPerIteration = 1_000,
    ) { taxBrackets ->
        MinBracketTaxCalculator(taxBrackets)
    }

    evaluateBracketScalability(
        seed = seed,
        numSamplesPerIteration = 10_000,
    ) { taxBrackets ->
        RegionTaxCalculator(taxBrackets)
    }
}

private fun evaluateBracketScalability(
    seed: Long,
    numSamplesPerIteration: Int,
    percentile: Int = 50,
    lowerBoundDollarsOfHighestBracket: Int = 350_000,
    numBracketsStart: Int = 5,
    numBracketsEnd: Int = 250,
    bracketStepSize: Int = 5,
    createTaxCalculator: (List<TaxBracket>) -> TaxCalculator,
) {
    val algorithm = createTaxCalculator(SampleTaxBrackets.bracketsWithTinyRange)::class.simpleName!!

    val upperBound = currencyFormatter.format(lowerBoundDollarsOfHighestBracket)
    ResultPrinter.printSectionHeading("$algorithm bracket scaling with brackets up to $upperBound")

    val column1Title = "# Brackets" + " ".repeat(COLUMN_SPACING)
    val column2Title = "Memory (bytes)"
    val header = column1Title + column2Title
    println(header)
    println("-".repeat(header.length))

    val random = Random(seed)
    val memoryAnalyzer = MemoryAnalyzer(cacheShallowSizes = true)

    for (numBrackets in numBracketsStart..numBracketsEnd step bracketStepSize) {
        val memoryBytes = memoryAnalyzer.runMonteCarloSimulationAndMeasureMemoryUsage(
            random,
            numSamplesPerIteration,
            percentile,
            lowerBoundDollarsOfHighestBracket,
            numBrackets,
            createTaxCalculator,
        )

        print(numberFormatter.format(numBrackets).padEnd(length = column1Title.length))
        println(numberFormatter.format(memoryBytes))
    }
    println()
}
