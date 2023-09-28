package solutions.logN

import org.junit.jupiter.api.Test
import sampleData.SampleTaxBrackets
import solutions.TaxCalculatorValidator
import kotlin.random.Random

class LogNTaxCalculatorTest {
    @Test
    fun `produces the same tax amounts as the well-tested LinearTaxCalculator`() {
        TaxCalculatorValidator.ensureProducesSameResultsAsLinearTaxCalculator(
            LogNTaxCalculator(SampleTaxBrackets.texasTaxBrackets),
            SampleTaxBrackets.texasTaxBrackets,
            Random,
        )
    }
}
