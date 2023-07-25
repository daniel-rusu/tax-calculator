package solutions.logN

import dataModel.Money
import dataModel.TaxBracket
import dataModel.TaxCalculator

class LogNTaxCalculator(taxBrackets: List<TaxBracket>) : TaxCalculator {
    private val taxBrackets = taxBrackets.toAccumulatedBrackets()

    override fun computeTax(income: Money): Money {
        val bracketIndex = taxBrackets.binarySearch { bracket ->
            when {
                bracket.from > income -> 1
                bracket.to != null && bracket.to!! <= income -> -1
                else -> 0
            }
        }
        return taxBrackets[bracketIndex].computeTotalTax(income)
    }
}
