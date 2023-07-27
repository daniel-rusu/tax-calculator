package solutions.constantTime.iteration2

import dataModel.Money
import dataModel.Money.Companion.cents
import dataModel.TaxBracket
import dataModel.TaxCalculator
import solutions.constantTime.iteration1.MemorizedTaxCalculator
import solutions.logN.LogNTaxCalculator
import solutions.logN.toAccumulatedBrackets

/**
 * An improved version of [MemorizedTaxCalculator] as it's bounded by the lower bound of the highest tax bracket.  All
 * incomes that are greater than that bound can be computed in constant time without having to memorize them.
 *
 * The boundary of the highest tax bracket is expected to be thousands of times less than the max reported income.
 * However, this is still horribly inefficient.  See the iteration 2 writeup for details:
 *
 * https://itnext.io/impossible-algorithm-computing-income-tax-in-constant-time-716b3c36c012
 */
class BoundedMemorizedTaxCalculator(taxBrackets: List<TaxBracket>) : TaxCalculator {
    private val highestBracket = taxBrackets.toAccumulatedBrackets().last()
    private val memorizedIncomeToTaxAmount: Map<Money, Money>

    init {
        val taxCalculator = LogNTaxCalculator(taxBrackets)

        // manually create a hashMap as "associateWith" creates a LinkedHashMap by default which uses more memory
        memorizedIncomeToTaxAmount = generateSequence(0.cents) { it + 1.cents }
            .takeWhile { it < highestBracket.from }
            // create a hashMap as "associateWith" creates a LinkedHashMap by default which uses more memory
            .associateWithTo(hashMapOf()) { income -> taxCalculator.computeTax(income) }
    }

    override fun computeTax(income: Money): Money {
        return memorizedIncomeToTaxAmount[income] ?: highestBracket.computeTotalTax(income)
    }
}
