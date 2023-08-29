package solutions.logN

import dataModel.Money
import dataModel.TaxBracket
import dataModel.TaxCalculator
import dataModel.toAccumulatedBrackets

/** A tax calculator that computes income tax in Log(N) time, where N is the number of tax brackets */
class LogNTaxCalculator(taxBrackets: List<TaxBracket>) : TaxCalculator {
    private val taxBrackets = taxBrackets.toAccumulatedBrackets()

    override fun computeTax(income: Money): Money {
        // Log(N) time to find the correct bracket
        val bracketIndex = taxBrackets.binarySearch { bracket ->
            when {
                bracket.from > income -> 1
                bracket.to != null && bracket.to!! <= income -> -1
                else -> 0
            }
        }
        // + constant time to compute the total tax
        return taxBrackets[bracketIndex].computeTotalTax(income)
    }
}
