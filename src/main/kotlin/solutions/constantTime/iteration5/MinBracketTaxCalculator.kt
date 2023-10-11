package solutions.constantTime.iteration5

import dataModel.AccumulatedTaxBracketV2
import dataModel.AccumulatedTaxBracketV2.Companion.toAccumulatedBracketsV2
import dataModel.Money
import dataModel.Money.Companion.cents
import dataModel.TaxBracket
import dataModel.TaxCalculator
import solutions.constantTime.iteration4.GCDTaxCalculator

/**
 * Similar to [GCDTaxCalculator] but uses the smallest bracket instead of the GCD along with a constant-time
 * correction.  This is much more efficient as the GCD can be 1 whereas the smallest bracket is usually much larger
 * resulting in significantly fewer chunks.
 */
class MinBracketTaxCalculator(taxBrackets: List<TaxBracket>) : TaxCalculator {
    private val chunkSize = getNarrowestBracketSize(taxBrackets)
    private val highestBracket: AccumulatedTaxBracketV2
    private val roundedDownIncomeToBracket: Map<Money, AccumulatedTaxBracketV2>

    init {
        val accumulatedBrackets = taxBrackets.toAccumulatedBracketsV2()
        highestBracket = accumulatedBrackets.last()
        roundedDownIncomeToBracket = associateChunkMultiplesToTaxBrackets(accumulatedBrackets)
    }

    override fun computeTax(income: Money): Money {
        return getTaxBracket(income).computeTotalTax(income)
    }

    private fun getTaxBracket(income: Money): AccumulatedTaxBracketV2 {
        if (income >= highestBracket.from) return highestBracket

        val roundedDownIncome = income / chunkSize * chunkSize
        val approximateBracket = roundedDownIncomeToBracket[roundedDownIncome]!!
        return when {
            approximateBracket.to == null || income < approximateBracket.to!! -> approximateBracket
            else -> approximateBracket.next!! // boundary scenario
        }
    }

    /** Create a map associating each multiple of the chunk size with its corresponding tax bracket */
    private fun associateChunkMultiplesToTaxBrackets(
        accumulatedBrackets: List<AccumulatedTaxBracketV2>,
    ): Map<Money, AccumulatedTaxBracketV2> {
        val chunkAmount = Money.ofCents(chunkSize)
        var bracketIndex = 0
        return generateSequence(0.cents) { it + chunkAmount }
            .takeWhile { it < accumulatedBrackets.last().from }
            // create a hashMap as "associateWith" creates a LinkedHashMap by default which uses more memory
            .associateWithTo(HashMap()) { income ->
                if (income >= accumulatedBrackets[bracketIndex].to!!) {
                    bracketIndex++
                }
                accumulatedBrackets[bracketIndex]
            }
    }
}

private fun getNarrowestBracketSize(brackets: List<TaxBracket>): Long {
    return brackets.asSequence()
        .filter { it.to != null }
        .minOfOrNull { (it.to!! - it.from).cents }
        ?: 1L
}
