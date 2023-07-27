package solutions.constantTime.iteration2

import org.junit.jupiter.api.Test
import sampleData.SampleTaxBrackets
import solutions.TaxCalculatorValidator

class BoundedMemorizedTaxCalculatorTest {
    @Test
    fun `produces the same tax as the well-tested LinearTaxCalculator`() {
        TaxCalculatorValidator.ensureProducesSameResultAsLinearTaxCalculator(
            BoundedMemorizedTaxCalculator(SampleTaxBrackets.bracketsWithTinyRange),
            SampleTaxBrackets.bracketsWithTinyRange,
        )
    }
}
