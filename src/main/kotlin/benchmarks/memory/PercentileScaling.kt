package benchmarks.memory

import benchmarks.tools.MemoryAnalyzer
import benchmarks.tools.MemoryUsage
import benchmarks.tools.ResultPrinter
import dataModel.TaxBracket
import dataModel.TaxCalculator
import sampleData.SampleTaxBrackets
import solutions.constantTime.iteration5.MinBracketTaxCalculator
import solutions.constantTime.iteration6.RegionTaxCalculator
import java.text.NumberFormat
import kotlin.random.Random

private val currencyFormatter = NumberFormat.getCurrencyInstance().apply {
    minimumFractionDigits = 0
}

fun main() {
    val seed = System.currentTimeMillis() + System.nanoTime()

    evaluatePercentileScalability(
        seed = seed,
    ) { taxBrackets ->
        MinBracketTaxCalculator(taxBrackets)
    }

    evaluatePercentileScalability(
        seed = seed,
    ) { taxBrackets ->
        RegionTaxCalculator(taxBrackets)
    }
}

private fun evaluatePercentileScalability(
    seed: Long,
    numSamples: Int = 100_000,
    numBrackets: Int = 10,
    lowerBoundDollarsOfHighestBracket: Int = 350_000,
    createTaxCalculator: (List<TaxBracket>) -> TaxCalculator,
) {
    val algorithm = createTaxCalculator(SampleTaxBrackets.bracketsWithTinyRange)::class.simpleName!!
    val upperBound = currencyFormatter.format(lowerBoundDollarsOfHighestBracket)

    ResultPrinter.printSectionHeading("$algorithm percentile scaling with $numBrackets brackets up to $upperBound")

    val random = Random(seed)
    val memoryAnalyzer = MemoryAnalyzer(cacheShallowSizes = true)

    val percentiles = (1..99).toList()
    val results = memoryAnalyzer.runMonteCarloSimulationAndMeasureMemoryUsage(
        random,
        numSamples,
        percentiles,
        lowerBoundDollarsOfHighestBracket,
        numBrackets,
        createTaxCalculator,
    ).mapIndexed { index, bytes ->
        MemoryUsage(percentiles[index].toString(), bytes)
    }

    ResultPrinter.printMemoryUsage(
        title = "$algorithm percentile scaling with $numBrackets brackets up to $upperBound",
        scenarioTitle = "Percentile",
        results = results,
    )
}
