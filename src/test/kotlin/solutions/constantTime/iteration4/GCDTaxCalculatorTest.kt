package solutions.constantTime.iteration4

import org.junit.jupiter.api.Test
import sampleData.SampleTaxBrackets
import solutions.TaxCalculatorValidator

class GCDTaxCalculatorTest {
    @Test
    fun `produces the same tax as the well-tested LinearTaxCalculator`() {
        TaxCalculatorValidator.ensureProducesSameResultAsLinearTaxCalculator(
            GCDTaxCalculator(SampleTaxBrackets.texasTaxBrackets),
            SampleTaxBrackets.texasTaxBrackets,
        )
    }
}
