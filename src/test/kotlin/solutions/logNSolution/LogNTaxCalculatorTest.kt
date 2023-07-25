package solutions.logNSolution

import org.junit.jupiter.api.Test
import sampleData.SampleTaxBrackets
import solutions.TaxCalculatorValidator

class LogNTaxCalculatorTest {
    @Test
    fun `produces the same tax as the well-tested LinearTaxCalculator`() {
        TaxCalculatorValidator.ensureProducesSameResultAsLinearTaxCalculator(
            LogNTaxCalculator(SampleTaxBrackets.texasTaxBrackets),
            SampleTaxBrackets.texasTaxBrackets,
        )
    }
}