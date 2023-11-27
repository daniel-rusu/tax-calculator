package solutions.constantTime.iteration1

import dataModel.base.Money
import dataModel.base.Money.Companion.cents
import dataModel.base.TaxBracket
import dataModel.base.TaxCalculator
import sampleData.SampleTaxBrackets
import solutions.logN.LogNTaxCalculator

/**
 * Temporary assumption on the max supported income to get things rolling without blowing up memory consumption.  The
 * next iteration will eliminate this assumption.
 */
private val maxReportedIncome = SampleTaxBrackets.bracketsWithTinyRange.last().from * 2

/**
 * A very inefficient idea as a starting point to iterate towards a constant-time solution.  This simply computes and
 * memorizes the taxes for every possible income up to [maxReportedIncome].
 *
 * Note that this solution isn't actually constant amortized time even though we're just looking up the tax in a map.
 * An explanation is included here:
 *
 * https://itnext.io/impossible-algorithm-computing-income-tax-in-constant-time-716b3c36c012
 */
class MemorizedTaxCalculator(taxBrackets: List<TaxBracket>) : TaxCalculator {
    private val memorizedIncomeToTaxAmount: Map<Money, Money>

    init {
        val taxCalculator = LogNTaxCalculator(taxBrackets)

        memorizedIncomeToTaxAmount = generateSequence(0.cents) { it + 1.cents }
            .takeWhile { it <= maxReportedIncome }
            // create a hashMap as "associateWith" creates a LinkedHashMap by default which uses more memory
            .associateWithTo(HashMap()) { income -> taxCalculator.computeTax(income) }
    }

    override fun computeTax(income: Money): Money {
        return memorizedIncomeToTaxAmount[income] ?: throw UnsupportedOperationException("Unsupported income: $income")
    }
}
