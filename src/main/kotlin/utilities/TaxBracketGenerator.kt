package utilities

import dataModel.Money.Companion.dollars
import dataModel.Percent
import dataModel.Percent.Companion.percent
import dataModel.TaxBracket
import kotlin.random.Random

private val maxPercentBps = 100.percent.amountBps

object TaxBracketGenerator {
    /**
     * The current data model can support a maximum of 10,000 brackets due to the following reasons:
     *
     * 1. In a progressive income tax system, the tax rate must be strictly increasing as you advance to the next
     * bracket.  This results in an ordered list of brackets starting with the lowest tax rate and ending in the
     * highest tax rate.
     *
     * 2. Consecutive brackets cannot have the same tax rate otherwise they would be part of a single larger tax
     * bracket.
     *
     * 3. All brackets need a different tax rate in order to fulfill the previous 2 requirements so the maximum number
     * of brackets is limited by the maximum number of unique tax rates.
     *
     * 4. All regions in the world limit tax bracket percentages to at most 2 decimal places so using basis points is
     * the granularity limit.
     *
     * 5. Each percent is split into 100bps so there are up to 100% X 100bps = 10,000 unique tax rates possible.
     */
    val maxNumTaxBrackets = maxPercentBps.toInt()

    fun generateTaxBrackets(
        numBrackets: Int,
        lowerBoundDollarsOfHighestBracket: Int = numBrackets * 100,
        random: Random = Random,
    ): List<TaxBracket> {
        require(numBrackets > 0) { "The number of tax brackets must be positive" }
        require(numBrackets <= maxNumTaxBrackets) { "Cannot create more than $maxNumTaxBrackets tax brackets" }
        if (numBrackets == 1) {
            require(lowerBoundDollarsOfHighestBracket == 0) {
                "The lower bound of the highest bracket must be 0 when creating a single bracket"
            }
        }
        require(lowerBoundDollarsOfHighestBracket + 1 >= numBrackets) {
            "Cannot create $numBrackets brackets if the highest bracket starts at $lowerBoundDollarsOfHighestBracket"
        }

        // Divide the range into numBrackets distinct lower-bound values including 0
        val lowerBounds = hashSetOf(0.dollars)
        lowerBounds += lowerBoundDollarsOfHighestBracket.dollars
        while (lowerBounds.size < numBrackets) {
            lowerBounds += random.nextInt(until = lowerBoundDollarsOfHighestBracket).dollars
        }

        // generate a unique percentage for each bracket up to 100.00% (exclusive)
        val percentages = hashSetOf<Percent>()
        while (percentages.size < numBrackets) {
            percentages += Percent.ofBasisPoints(random.nextLong(until = maxPercentBps))
        }
        val sortedPercentages = percentages.sortedBy { it.amountBps }
        val sortedLowerBounds = lowerBounds.sortedBy { it.cents }

        val result = ArrayList<TaxBracket>(numBrackets)
        for (i in 0..<numBrackets) {
            result += TaxBracket(
                taxRate = sortedPercentages[i],
                from = sortedLowerBounds[i],
                to = sortedLowerBounds.getOrNull(i + 1)
            )
        }
        return result
    }
}
