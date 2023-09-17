package dataModel

import dataModel.Money.Companion.dollars
import solutions.linear.computeBracketTax

/** Similar to [TaxBracket] except that it also stores the accumulated tax up to the start of this bracket */
data class AccumulatedTaxBracket(
    private val regularBracket: TaxBracket,
    private val accumulatedTax: Money,
) {
    val from: Money
        get() = regularBracket.from

    val to: Money?
        get() = regularBracket.to

    fun computeTotalTax(income: Money): Money {
        require(income >= from)
        require(to == null || income < to!!)

        return accumulatedTax + regularBracket.computeBracketTax(income)
    }
}

fun List<TaxBracket>.toAccumulatedBrackets(): List<AccumulatedTaxBracket> {
    var accumulatedTax = 0.dollars

    return this.map { regularBracket ->
        val accumulatedTaxUpToStartOfBracket = accumulatedTax
        if (regularBracket.to != null) {
            accumulatedTax += regularBracket.computeBracketTax(regularBracket.to)
        }
        AccumulatedTaxBracket(regularBracket, accumulatedTaxUpToStartOfBracket)
    }
}