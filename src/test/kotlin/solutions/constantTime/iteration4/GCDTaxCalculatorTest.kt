package solutions.constantTime.iteration4

import org.junit.jupiter.api.Test
import sampleData.SampleTaxBrackets
import solutions.TaxCalculatorValidator

class GCDTaxCalculatorTest {
    @Test
    fun `produces the same tax amounts as the well-tested LinearTaxCalculator`() {
        TaxCalculatorValidator.ensureProducesSameResultsAsLinearTaxCalculator(
            GCDTaxCalculator(SampleTaxBrackets.texasTaxBrackets),
            SampleTaxBrackets.texasTaxBrackets,
        )
    }
}
