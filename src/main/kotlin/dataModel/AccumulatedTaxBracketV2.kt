package dataModel

import dataModel.AccumulatedTaxBracketV2.Companion.toAccumulatedBracketsV2
import dataModel.Money.Companion.dollars
import solutions.linear.computeBracketTax

/**
 * Similar to [TaxBracket] except that it also stores the accumulated tax up to the start of this bracket.
 *
 * Private constructor so that the only way to create accumulated brackets is from a list of regular brackets via the
 * [toAccumulatedBracketsV2] function so that the [next] references are set correctly.
 */
class AccumulatedTaxBracketV2 private constructor(
    val from: Money,
    val taxRate: Percent,
    val accumulatedTax: Money,
) {
    var next: AccumulatedTaxBracketV2? = null // null when this is the last bracket
        private set

    val to: Money?
        get() = next?.from

    override fun toString(): String {
        return "AccumulatedTaxBracket[from = $from, to = $to, taxRate = $taxRate]"
    }

    fun computeTotalTax(income: Money): Money {
        require(income >= from) { "$income is below the lower bound of $from" }
        to?.let { require(income < it) }

        return accumulatedTax + taxRate * (income - from)
    }

    companion object {
        fun List<TaxBracket>.toAccumulatedBracketsV2(): List<AccumulatedTaxBracketV2> {
            val accumulatedBrackets = ArrayList<AccumulatedTaxBracketV2>(this.size)
            var nextAccumulatedTax = 0.dollars
            this.mapTo(accumulatedBrackets) { bracket ->
                val accumulatedTax = nextAccumulatedTax
                if (bracket.to != null) {
                    nextAccumulatedTax += bracket.computeBracketTax(bracket.to)
                }
                AccumulatedTaxBracketV2(bracket.from, bracket.taxRate, accumulatedTax)
            }

            // Link each bracket to the next one
            accumulatedBrackets.forEachIndexed { index, bracket ->
                bracket.next = accumulatedBrackets.getOrNull(index + 1)
            }
            return accumulatedBrackets
        }
    }
}
