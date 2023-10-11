package solutions.constantTime.iteration3

import dataModel.Money
import dataModel.Money.Companion.dollars
import dataModel.TaxBracket
import dataModel.TaxCalculator
import solutions.constantTime.iteration2.BoundedMemorizedTaxCalculator
import dataModel.AccumulatedTaxBracket
import dataModel.toAccumulatedBrackets

/**
 * An improved version of [BoundedMemorizedTaxCalculator] as it stores a reference to an existing bracket for each
 * income instead of storing a new Money instance each time.  This reduces memory consumption by about 40%.
 * Additionally, the initial setup completes in O(B) time instead of O(B * log(N)) time, where N is the number of
 * brackets and B is the lower bound of the highest tax bracket in pennies.
 */
class MemorizedBracketTaxCalculator(taxBrackets: List<TaxBracket>) : TaxCalculator {
    private val highestBracket: AccumulatedTaxBracket
    private val memorizedIncomeToBracket: Map<Money, AccumulatedTaxBracket>

    init {
        val accumulatedBrackets = taxBrackets.toAccumulatedBrackets()
        highestBracket = accumulatedBrackets.last()

        var bracketIndex = 0
        memorizedIncomeToBracket = generateSequence(0.dollars) { it + Money.ofCents(amount = 1) }
            .takeWhile { it < taxBrackets.last().from }
            // create a hashMap as "associateWith" creates a LinkedHashMap by default which uses more memory
            .associateWithTo(HashMap()) { income ->
                if (income >= taxBrackets[bracketIndex].to!!) {
                    bracketIndex++
                }
                accumulatedBrackets[bracketIndex]
            }
    }

    override fun computeTax(income: Money): Money {
        val bracket = memorizedIncomeToBracket[income] ?: highestBracket
        return bracket.computeTotalTax(income)
    }
}
