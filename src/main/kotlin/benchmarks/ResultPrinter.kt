package benchmarks

import benchmarks.memory.MemoryUsage
import java.text.NumberFormat

private const val COLUMN_SPACING = 2
private const val MEMORY_USAGE_TITLE = "Data Structure Size (bytes)"

private val numberFormatter = NumberFormat.getNumberInstance()

object ResultPrinter {
    fun printMemoryUsage(title: String, scenarioTitle: String, results: List<MemoryUsage>) {
        val algorithmColumnSize = getScenarioColumnSize(scenarioTitle, results)
        val headerColumn = scenarioTitle.padEnd(algorithmColumnSize) + MEMORY_USAGE_TITLE
        val separator = "-".repeat(headerColumn.length)

        println(title)
        println(headerColumn)
        println(separator)

        for (result in results) {
            print(result.scenario.padEnd(length = algorithmColumnSize))
            println(numberFormatter.format(result.memoryUsageBytes))
        }
        println()
    }
}

private fun getScenarioColumnSize(scenarioTitle: String, results: List<MemoryUsage>): Int {
    val maxSize = results.maxOf { it.scenario.length }.coerceAtLeast(scenarioTitle.length)
    return maxSize + COLUMN_SPACING
}
