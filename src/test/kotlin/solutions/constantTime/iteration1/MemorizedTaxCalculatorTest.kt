package solutions.constantTime.iteration1

import org.junit.jupiter.api.Test
import sampleData.SampleTaxBrackets
import solutions.TaxCalculatorValidator

class MemorizedTaxCalculatorTest {
    @Test
    fun `produces the same tax amounts as the well-tested LinearTaxCalculator`() {
        TaxCalculatorValidator.ensureProducesSameResultsAsLinearTaxCalculator(
            MemorizedTaxCalculator(SampleTaxBrackets.bracketsWithTinyRange),
            SampleTaxBrackets.bracketsWithTinyRange,
        )
    }
}
