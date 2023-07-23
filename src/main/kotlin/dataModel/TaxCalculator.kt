package dataModel

interface TaxCalculator {
    fun computeTax(taxableIncome: Money): Money
}
