package solutions.constantTime.iteration6

import org.junit.jupiter.api.Test
import sampleData.SampleTaxBrackets
import solutions.TaxCalculatorValidator

class RegionTaxCalculatorTest {
    @Test
    fun `produces the same tax amounts as the well-tested LinearTaxCalculator`() {
        TaxCalculatorValidator.ensureProducesSameResultsAsLinearTaxCalculator(
            RegionTaxCalculator(SampleTaxBrackets.texasTaxBrackets),
            SampleTaxBrackets.texasTaxBrackets,
        )
    }
}
