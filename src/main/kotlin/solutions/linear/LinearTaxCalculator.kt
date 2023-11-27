package solutions.linear

import dataModel.base.Money
import dataModel.base.TaxBracket
import dataModel.base.TaxCalculator

/** A tax calculator that computes income tax in O(N) time, where N is the number of tax brackets */
class LinearTaxCalculator(private val taxBrackets: List<TaxBracket>) : TaxCalculator {
    override fun computeTax(income: Money): Money {
        var accumulatedTax = Money.ofCents(amount = 0L)
        for (taxBracket in taxBrackets) {
            if (income <= taxBracket.from) break

            accumulatedTax += taxBracket.computeBracketTax(income)
        }
        return accumulatedTax
    }
}

fun TaxBracket.computeBracketTax(income: Money): Money {
    require(income >= from)

    val highestIncomeInBracket = when {
        to == null -> income
        income <= to -> income
        else -> to
    }
    return taxRate * (highestIncomeInBracket - from)
}
