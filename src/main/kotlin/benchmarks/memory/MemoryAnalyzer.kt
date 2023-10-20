package benchmarks.memory

import org.github.jamm.Filters
import org.github.jamm.MemoryMeter
import org.github.jamm.MemoryMeterStrategy
import org.github.jamm.listeners.NoopMemoryMeterListener
import org.github.jamm.strategies.MemoryMeterStrategies
import java.util.concurrent.ConcurrentHashMap

class MemoryAnalyzer(cacheShallowSizes: Boolean) {
    private val memoryMeter = createMemoryMeter(cacheShallowSizes = cacheShallowSizes)

    /** Measures the total memory consumption of the [entity] including the size of nested entities */
    fun sizeOf(entity: Any): Long = memoryMeter.measureDeep(entity)
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
