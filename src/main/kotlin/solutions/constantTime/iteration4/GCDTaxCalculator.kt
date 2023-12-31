package solutions.constantTime.iteration4

import dataModel.base.Money
import dataModel.base.Money.Companion.cents
import dataModel.base.TaxBracket
import dataModel.base.TaxCalculator
import solutions.constantTime.iteration3.MemorizedBracketTaxCalculator
import dataModel.v2.AccumulatedTaxBracket
import dataModel.v2.toAccumulatedBrackets

/**
 * This is expected to be thousands of times more efficient than the [MemorizedBracketTaxCalculator].
 *
 * A detailed writeup is available here:
 * https://itnext.io/impossible-algorithm-computing-income-tax-in-constant-time-716b3c36c012
 */
class GCDTaxCalculator(taxBrackets: List<TaxBracket>) : TaxCalculator {
    private val greatestCommonDivisor = computeBracketSizeGCD(taxBrackets)
    private val highestBracket: AccumulatedTaxBracket
    private val roundedDownIncomeToBracket: Map<Money, AccumulatedTaxBracket>

    init {
        val accumulatedBrackets = taxBrackets.toAccumulatedBrackets()
        highestBracket = accumulatedBrackets.last()
        roundedDownIncomeToBracket = associateGCDMultiplesToTaxBrackets(accumulatedBrackets)
    }

    override fun computeTax(income: Money): Money {
        val roundedDownIncome = income / greatestCommonDivisor * greatestCommonDivisor

        return roundedDownIncomeToBracket[roundedDownIncome]?.computeTotalTax(income)
            ?: highestBracket.computeTotalTax(income)
    }

    /** Create a map associating each multiple of the GCD with its corresponding tax bracket */
    private fun associateGCDMultiplesToTaxBrackets(
        accumulatedBrackets: List<AccumulatedTaxBracket>,
    ): Map<Money, AccumulatedTaxBracket> {
        val gcdAmount = Money.ofCents(greatestCommonDivisor)
        var bracketIndex = 0
        return generateSequence(0.cents) { it + gcdAmount }
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

/** Computes the [gcd] of all the bracket sizes (in pennies) */
private fun computeBracketSizeGCD(taxBrackets: List<TaxBracket>): Long {
    return taxBrackets.asSequence()
        .filter { it.to != null } // ignore the last unbounded bracket
        .map { it.to!!.cents - it.from.cents }
        .reduceOrNull { result, size -> gcd(result, size) }
        ?: 1
}

/** Computes the greatest common divisor of [a] and [b] */
private fun gcd(a: Long, b: Long): Long = when (a) {
    0L -> b
    else -> gcd(b % a, a)
}
