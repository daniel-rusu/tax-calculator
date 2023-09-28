package solutions.constantTime.iteration4

import dataModel.TaxBracket
import org.junit.jupiter.api.Test
import sampleData.SampleTaxBrackets
import solutions.TaxCalculatorValidator
import taxBracketGenerator.TaxBracketGenerator
import kotlin.random.Random

class GCDTaxCalculatorTest {
    @Test
    fun `produces the same tax amounts as the well-tested LinearTaxCalculator`() {
        ensureMatchesResultsOfLinearTaxCalculator(forBrackets = SampleTaxBrackets.texasTaxBrackets, random = Random)
    }

    @Test
    fun `produces the same tax amounts as the well-tested LinearTaxCalculator with random brackets`() {
        val seed = Random.nextLong()
        val random = Random(seed)
        println("Seed: $seed") // to be able to reproduce when encountering a failure

        repeat(100) {
            val numBrackets = random.nextInt(1, 100)
            val lowerBoundDollarsOfHighestBracket = when (numBrackets) {
                1 -> 0
                else -> random.nextInt(numBrackets - 1, numBrackets * 100)
            }
            val taxBrackets = TaxBracketGenerator.generateTaxBrackets(
                numBrackets,
                lowerBoundDollarsOfHighestBracket,
                random,
            )
            ensureMatchesResultsOfLinearTaxCalculator(forBrackets = taxBrackets, random = random)
        }
    }

    private fun ensureMatchesResultsOfLinearTaxCalculator(forBrackets: List<TaxBracket>, random: Random) {
        TaxCalculatorValidator.ensureProducesSameResultsAsLinearTaxCalculator(
            GCDTaxCalculator(forBrackets),
            forBrackets,
            random,
        )
    }
}
