package dataModel

interface TaxCalculator {
    /** Computes the income tax given the specified taxable [income] */
    fun computeTax(income: Money): Money
}
