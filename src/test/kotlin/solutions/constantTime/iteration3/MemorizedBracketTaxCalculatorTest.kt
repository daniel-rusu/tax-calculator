package solutions.constantTime.iteration3

import org.junit.jupiter.api.Test
import sampleData.SampleTaxBrackets
import solutions.TaxCalculatorValidator

class MemorizedBracketTaxCalculatorTest {
    @Test
    fun `produces the same tax amounts as the well-tested LinearTaxCalculator`() {
        TaxCalculatorValidator.ensureProducesSameResultsAsLinearTaxCalculator(
            MemorizedBracketTaxCalculator(SampleTaxBrackets.bracketsWithTinyRange),
            SampleTaxBrackets.bracketsWithTinyRange,
        )
    }
}
