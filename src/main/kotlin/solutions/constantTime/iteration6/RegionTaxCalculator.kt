package solutions.constantTime.iteration6

import dataModel.AccumulatedTaxBracketV2
import dataModel.AccumulatedTaxBracketV2.Companion.toAccumulatedBracketsV2
import dataModel.Money
import dataModel.TaxBracket
import dataModel.TaxCalculator
import solutions.constantTime.iteration5.MinBracketTaxCalculator
import kotlin.math.sqrt

/**
 * A tax calculator that splits the tax range into uniformly-sized regions where each region has its own chunk size.
 * This is much more efficient than [MinBracketTaxCalculator] as the negative effect of a tiny bracket is confined to
 * a single region instead of affecting the entire tax range resulting in significantly fewer chunks.
 */
class RegionTaxCalculator(taxBrackets: List<TaxBracket>) : TaxCalculator {
    private val highestBracket: AccumulatedTaxBracketV2
    private val regionSize = computeRegionSize(taxBrackets)
    private val regions: List<Region>
    private val bracketChunks: List<AccumulatedTaxBracketV2>

    init {
        val accumulatedBrackets = taxBrackets.toAccumulatedBracketsV2()
        highestBracket = accumulatedBrackets.last()

        val (regionList, chunkList) =
            createRegionsAndChunks(regionSize, accumulatedBrackets)

        regions = regionList
        bracketChunks = chunkList
    }

    override fun computeTax(income: Money): Money {
        return getTaxBracket(income).computeTotalTax(income)
    }

    private fun getTaxBracket(income: Money): AccumulatedTaxBracketV2 {
        if (income >= highestBracket.from) return highestBracket

        val regionIndex = (income.cents / regionSize).toInt()
        val region = regions[regionIndex]
        val regionBoundary = regionSize * regionIndex
        val remainder = income.cents - regionBoundary

        val chunkIndex = region.getChunkIndex(remainder)
        return getCorrectBracket(income, bracketChunks[chunkIndex])
    }
}

private fun createRegionsAndChunks(
    regionSize: Long,
    taxBrackets: List<AccumulatedTaxBracketV2>,
): Pair<List<Region>, List<AccumulatedTaxBracketV2>> {
    val highestBracket = taxBrackets.last()
    val regionList = mutableListOf<Region>()
    val bracketChunkList = mutableListOf<AccumulatedTaxBracketV2>()

    var regionStart = 0L
    var currentBracket = taxBrackets.first()
    while (regionStart < highestBracket.from.cents) {
        val regionEnd = regionStart + regionSize
        val chunkSize = computeChunkSizeForRegion(regionStart, regionEnd, currentBracket)
        regionList += Region(bracketChunkList.size, chunkSize)

        var chunkBoundary = regionStart
        while (chunkBoundary < regionEnd) {
            bracketChunkList += currentBracket
            chunkBoundary = (chunkBoundary + chunkSize).coerceAtMost(regionEnd)
            // prepare the bracket for the next chunk (which might be part of the next region)
            while (currentBracket.to != null && currentBracket.to!!.cents <= chunkBoundary) {
                currentBracket = currentBracket.next!!
            }
        }
        regionStart += regionSize
    }
    return Pair(regionList, bracketChunkList)
}

private fun getCorrectBracket(
    income: Money,
    approximateBracket: AccumulatedTaxBracketV2,
): AccumulatedTaxBracketV2 {
    val upperBound = approximateBracket.to
    return when {
        upperBound == null || income < upperBound -> approximateBracket
        else -> approximateBracket.next!! // boundary scenario
    }
}

private fun computeRegionSize(taxBrackets: List<TaxBracket>): Long {
    if (taxBrackets.size == 1) return 1L

    val narrowestBracketSize = taxBrackets.asSequence()
        .filter { it.to != null }
        .minOf { it.to!!.cents - it.from.cents }

    val range = taxBrackets.last().from.cents
    val multiplier = 5 // close to optimal based on a Monte Carlo simulation across a large distribution of datasets
    return multiplier * sqrt(range * narrowestBracketSize / sqrt(taxBrackets.size.toDouble())).toLong()
}

private fun computeChunkSizeForRegion(
    regionStart: Long,
    regionEnd: Long,
    currentBracket: AccumulatedTaxBracketV2,
): Long {
    return generateSequence(currentBracket) { it.next }
        .dropWhile { it.from.cents <= regionStart }
        .filter { it.to != null }
        .takeWhile { it.to!!.cents < regionEnd }
        .minOfOrNull { it.to!!.cents - it.from.cents }
        ?: (regionEnd - regionStart)
}
