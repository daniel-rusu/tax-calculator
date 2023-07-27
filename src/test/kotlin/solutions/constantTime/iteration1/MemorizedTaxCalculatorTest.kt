package solutions.constantTime.iteration1

import org.junit.jupiter.api.Test
import sampleData.SampleTaxBrackets
import solutions.TaxCalculatorValidator

class MemorizedTaxCalculatorTest {
    @Test
    fun `produces the same tax as the well-tested LinearTaxCalculator`() {
        TaxCalculatorValidator.ensureProducesSameResultAsLinearTaxCalculator(
            MemorizedTaxCalculator(SampleTaxBrackets.bracketsWithTinyRange),
            SampleTaxBrackets.bracketsWithTinyRange,
        )
    }
}
