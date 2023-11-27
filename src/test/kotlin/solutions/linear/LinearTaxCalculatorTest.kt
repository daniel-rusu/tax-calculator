package solutions.linear

import dataModel.base.Money
import dataModel.base.Money.Companion.dollars
import org.junit.jupiter.api.Test
import sampleData.SampleTaxBrackets
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class LinearTaxCalculatorTest {
    private val taxCalculator = LinearTaxCalculator(SampleTaxBrackets.texasTaxBrackets)

    @Test
    fun `zero income results in zero tax`() {
        expectThat(taxCalculator.computeTax(income = 0.dollars))
            .isEqualTo(0.dollars)
    }

    @Test
    fun `income at tax bracket boundaries`() {
        expectThat(taxCalculator.computeTax(10_275.dollars))
            // = $10,275 * 10%
            .isEqualTo(Money.of(dollars = 1_027, cents = 50))

        expectThat(taxCalculator.computeTax(41_775.dollars))
            // = tax up to $10,275 + 12% of the remainder
            // = $1,027.50 + ($41,775 - $10,275) * 12% = $1,027.50 + $3,780 = $4,807.50
            .isEqualTo(Money.of(dollars = 4_807, cents = 50))

        expectThat(taxCalculator.computeTax(89_075.dollars))
            // = tax up to $41,775 + 22% of the remainder
            // = $4,807.50 + ($89_075 - $41,775) * 22% = $4,807.50 + $10,406 = $15,213.50
            .isEqualTo(Money.of(dollars = 15_213, cents = 50))

        expectThat(taxCalculator.computeTax(170_050.dollars))
            // = tax up to $89_075 + 24% of the remainder
            // = $15,213.50 + ($170,050 - $89_075) * 24% = $15,213.50 + $19,434 = $34,647.50
            .isEqualTo(Money.of(dollars = 34_647, cents = 50))

        expectThat(taxCalculator.computeTax(215_950.dollars))
            // = tax up to $170,050 + 32% of the remainder
            // = $34,647.50 + ($215,950 - $170,050) * 32% = $34,647.50 + $14,688 = $49,335.50
            .isEqualTo(Money.of(dollars = 49_335, cents = 50))

        expectThat(taxCalculator.computeTax(539_900.dollars))
            // = tax up to $215,950 + 35% of the remainder
            // = $49,335.50 + ($539,900 - $215,950) * 35% = $49,335.50 + $113,382.50 = $162,718
            .isEqualTo(162_718.dollars)
    }

    @Test
    fun `income within a bracket`() {
        // Within first tax bracket
        expectThat(taxCalculator.computeTax(500.dollars))
            // = $500 * 10%
            .isEqualTo(50.dollars)

        // Within 3rd bracket
        expectThat(taxCalculator.computeTax(60_000.dollars))
            // = tax up to $41,775 + 22% of the remainder
            // = $4,807.50 + ($60,000 - $41,775) * 22% = $4,807.50 + $4,009.50 = $8,817
            .isEqualTo(8_817.dollars)
    }

    @Test
    fun `income above highest bracket`() {
        expectThat(taxCalculator.computeTax(1_000_000.dollars))
            // = tax up to $539,900 + 37% of the remainder
            // = $162,718 + ($1,000,000 - $539,900) * 37% = $162,718 + $170,237 = $332,955
            .isEqualTo(332_955.dollars)
    }

    @Test
    fun `tax is rounded down to the nearest penny`() {
        expectThat(taxCalculator.computeTax(Money.of(dollars = 20, cents = 39)))
            // = $20.39 * 10% = $2.039 = $2.03
            .isEqualTo(Money.of(dollars = 2, cents = 3))
    }
}
