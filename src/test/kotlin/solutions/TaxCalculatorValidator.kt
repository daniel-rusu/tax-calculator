package solutions

import dataModel.Money
import dataModel.TaxBracket
import dataModel.TaxCalculator
import solutions.linear.LinearTaxCalculator
import strikt.api.Assertion
import strikt.api.expectThat
import kotlin.random.Random

object TaxCalculatorValidator {
    fun ensureProducesSameResultsAsLinearTaxCalculator(
        taxCalculator: TaxCalculator,
        taxBrackets: List<TaxBracket>,
    ) {
        val linearTaxCalculator = LinearTaxCalculator(taxBrackets)

        for (taxBracket in taxBrackets) {
            // Test the start of the bracket
            expectThat(taxCalculator)
                .producesSameTax(asCalculator = linearTaxCalculator, forIncome = taxBracket.from)

            // Test a random value that lies within this bracket
            val upperBound = taxBracket.to ?: (taxBracket.from * 2)
            val amount = Random.nextLong(from = taxBracket.from.cents, until = upperBound.cents)

            expectThat(taxCalculator)
                .producesSameTax(asCalculator = linearTaxCalculator, forIncome = Money.ofCents(amount))
        }
    }
}

private fun Assertion.Builder<TaxCalculator>.producesSameTax(
    asCalculator: TaxCalculator,
    forIncome: Money
): Assertion.Builder<TaxCalculator> {
    return assert(description = "produces same tax for income $forIncome") {
        when (val tax = subject.computeTax(forIncome)) {
            asCalculator.computeTax(forIncome) -> pass()
            else -> fail(actual = tax)
        }
    }
}
