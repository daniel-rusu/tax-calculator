package solutions.constantTime.iteration2

import org.junit.jupiter.api.Test
import sampleData.SampleTaxBrackets
import solutions.TaxCalculatorValidator

class BoundedMemorizedTaxCalculatorTest {
    @Test
    fun `produces the same tax amounts as the well-tested LinearTaxCalculator`() {
        TaxCalculatorValidator.ensureProducesSameResultsAsLinearTaxCalculator(
            BoundedMemorizedTaxCalculator(SampleTaxBrackets.bracketsWithTinyRange),
            SampleTaxBrackets.bracketsWithTinyRange,
        )
    }
}
