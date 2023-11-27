package benchmarks.memory

import benchmarks.tools.MemoryAnalyzer
import benchmarks.tools.MemoryUsage
import benchmarks.tools.ResultPrinter
import dataModel.base.TaxBracket
import sampleData.SampleTaxBrackets
import solutions.constantTime.iteration4.GCDTaxCalculator
import solutions.constantTime.iteration5.MinBracketTaxCalculator
import solutions.constantTime.iteration6.RegionTaxCalculator
import kotlin.reflect.full.memberProperties

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

    ResultPrinter.printSectionHeading("Sample datasets:")
    for (property in SampleTaxBrackets::class.memberProperties) {
        @Suppress("UNCHECKED_CAST")
        printDatasetMemoryUsageOfDataset(
            datasetName = property.name,
            taxBrackets = property.get(SampleTaxBrackets) as List<TaxBracket>,
            memoryAnalyzer = memoryAnalyzer,
        )
    }
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
