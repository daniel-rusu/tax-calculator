package solutions

import dataModel.Money
import dataModel.Money.Companion.dollars
import dataModel.TaxBracket
import dataModel.TaxCalculator
import solutions.linear.LinearTaxCalculator
import strikt.api.Assertion
import strikt.api.expectThat
import kotlin.random.Random

private const val NUM_SAMPLES_PER_BRACKET = 10

object TaxCalculatorValidator {
    fun ensureProducesSameResultsAsLinearTaxCalculator(
        taxCalculator: TaxCalculator,
        taxBrackets: List<TaxBracket>,
        random: Random,
    ) {
        val linearTaxCalculator = LinearTaxCalculator(taxBrackets)

        for (taxBracket in taxBrackets) {
            // Test the start of the bracket
            expectThat(taxCalculator)
                .producesSameTax(asCalculator = linearTaxCalculator, forIncome = taxBracket.from)

            validateRandomIncomesInBracket(taxBracket, random, taxCalculator, linearTaxCalculator)

            if (taxBracket.to == null) continue

            // Test the highest income that lies within this bracket
            val highestIncomeInBracket = taxBracket.to!! - Money.ofCents(1)
            expectThat(taxCalculator)
                .producesSameTax(asCalculator = linearTaxCalculator, forIncome = highestIncomeInBracket)
        }
    }
}

private fun validateRandomIncomesInBracket(
    taxBracket: TaxBracket,
    random: Random,
    taxCalculator: TaxCalculator,
    linearTaxCalculator: LinearTaxCalculator
) {
    val numValidations = when (taxBracket.to) {
        null -> NUM_SAMPLES_PER_BRACKET
        else -> NUM_SAMPLES_PER_BRACKET.coerceAtMost((taxBracket.to!! - taxBracket.from).cents.toInt())
    }

    val upperBound = when {
        taxBracket.to != null -> taxBracket.to!!
        taxBracket.from.cents == 0L -> 100.dollars
        else -> taxBracket.from * 2
    }

    repeat(numValidations) {
        // Test a random value that lies within this bracket
        val amount = random.nextLong(from = taxBracket.from.cents, until = upperBound.cents)

        expectThat(taxCalculator)
            .producesSameTax(asCalculator = linearTaxCalculator, forIncome = Money.ofCents(amount))
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
