package benchmarks.memory

import dataModel.TaxBracket
import dataModel.TaxCalculator
import org.github.jamm.Filters
import org.github.jamm.MemoryMeter
import org.github.jamm.MemoryMeterStrategy
import org.github.jamm.listeners.NoopMemoryMeterListener
import org.github.jamm.strategies.MemoryMeterStrategies
import utilities.TaxBracketGenerator
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.roundToLong
import kotlin.random.Random

class MemoryAnalyzer(cacheShallowSizes: Boolean) {
    private val memoryMeter = createMemoryMeter(cacheShallowSizes = cacheShallowSizes)

    /**
     * Performs a Monte Carlo simulation testing [numSamples] random tax systems each containing [numBrackets]
     * brackets up to [lowerBoundDollarsOfHighestBracket] and measures the [percentile] percentile memory consumption
     * in bytes.
     *
     * @param random The random generator for creating random tax systems
     * @param numSamples The number of tax systems to simulate
     * @param percentile The percentile of the memory consumption distribution to be measured
     * @param lowerBoundDollarsOfHighestBracket The highest bracket will start at this value in dollars
     * @param numBrackets The number of brackets that each tax system should contain
     * @param createTaxCalculator A higher-order function for creating the [TaxCalculator] given the tax system
     *
     * @return The [percentile] percentile memory consumption of all the simulated tax systems
     */
    fun runMonteCarloSimulationAndMeasureMemoryUsage(
        random: Random,
        numSamples: Int,
        percentile: Int,
        lowerBoundDollarsOfHighestBracket: Int,
        numBrackets: Int,
        createTaxCalculator: (List<TaxBracket>) -> TaxCalculator,
    ): Long {
        val memoryUsages = runSimulationAndMeasureMemoryUsages(
            random,
            numSamples,
            lowerBoundDollarsOfHighestBracket,
            numBrackets,
            createTaxCalculator,
        )
        return computePercentile(percentile, memoryUsages)
    }

    /**
     * Performs a Monte Carlo simulation testing [numSamples] random tax systems each containing [numBrackets]
     * brackets up to [lowerBoundDollarsOfHighestBracket] and measures each of the [percentiles] memory consumption
     * in bytes.
     *
     * @param random The random generator for creating random tax systems
     * @param numSamples The number of tax systems to simulate
     * @param percentiles The list of percentile of the memory consumption distribution to be measured
     * @param lowerBoundDollarsOfHighestBracket The highest bracket will start at this value in dollars
     * @param numBrackets The number of brackets that each tax system should contain
     * @param createTaxCalculator A higher-order function for creating the [TaxCalculator] given the tax system
     *
     * @return The [percentiles] memory consumption of all the simulated tax systems
     */
    fun runMonteCarloSimulationAndMeasureMemoryUsage(
        random: Random,
        numSamples: Int,
        percentiles: List<Int>,
        lowerBoundDollarsOfHighestBracket: Int,
        numBrackets: Int,
        createTaxCalculator: (List<TaxBracket>) -> TaxCalculator,
    ): List<Long> {
        val memoryUsages = runSimulationAndMeasureMemoryUsages(
            random,
            numSamples,
            lowerBoundDollarsOfHighestBracket,
            numBrackets,
            createTaxCalculator,
        )
        return percentiles.map { computePercentile(it, memoryUsages) }
    }

    fun sizeOf(entity: Any): Long = memoryMeter.measureDeep(entity)

    /**
     * Creates [numSamples] random tax systems each containing [numBrackets] brackets up to
     * [lowerBoundDollarsOfHighestBracket] measuring the memory consumption of each system.
     *
     * @param random The random generator for creating random tax systems
     * @param numSamples The number of tax systems to simulate
     * @param lowerBoundDollarsOfHighestBracket The highest bracket will start at this value in dollars
     * @param numBrackets The number of brackets that each tax system should contain
     * @param createTaxCalculator A higher-order function for creating the [TaxCalculator] given the tax system
     *
     * @return The list of memory usages of each simulation in bytes sorted ascending
     */
    private fun runSimulationAndMeasureMemoryUsages(
        random: Random,
        numSamples: Int,
        lowerBoundDollarsOfHighestBracket: Int,
        numBrackets: Int,
        createTaxCalculator: (List<TaxBracket>) -> TaxCalculator,
    ): List<Long> {
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
        return memoryUsages
    }
}

private fun computePercentile(percentile: Int, values: List<Long>): Long {
    val floatingIndex = percentile / 100.0 * (values.size - 1)
    val index = floatingIndex.toInt()
    val remainder = floatingIndex - index

    if (remainder == 0.0) return values[index]

    // Compute the weighted average based on how close it is to each index
    return (values[index] * (1.0 - remainder) + values[index + 1] * remainder).roundToLong()
}

private fun createMemoryMeter(cacheShallowSizes: Boolean): MemoryMeter {
    val backingStrategy = MemoryMeterStrategies.getInstance().getStrategy(MemoryMeter.BEST)
    val strategy = when (cacheShallowSizes) {
        true -> CachedMemoryMeterStrategy(backingStrategy)
        else -> backingStrategy
    }
    return MemoryMeter(
        strategy,
        Filters.getClassFilters(/* ignoreKnownSingletons = */ true),
        Filters.getFieldFilters(
            /* ignoreKnownSingletons = */ true,
            /* ignoreOuterClassReference = */ false,
            /* ignoreNonStrongReferences = */ true,
        ),
        NoopMemoryMeterListener.FACTORY,
    )
}

private class CachedMemoryMeterStrategy(
    val backingStrategy: MemoryMeterStrategy
) : MemoryMeterStrategy by backingStrategy {
    private val cache = ConcurrentHashMap<Class<Any>, Long>()

    override fun measure(entity: Any): Long {
        return cache.getOrPut(entity.javaClass) {
            backingStrategy.measure(entity)
        }
    }
}
