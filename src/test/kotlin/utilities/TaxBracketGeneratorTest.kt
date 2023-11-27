package utilities

import dataModel.base.Money.Companion.dollars
import dataModel.base.Percent.Companion.percent
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.*
import kotlin.random.Random

class TaxBracketGeneratorTest {
    @Test
    fun `the number of tax brackets must be positive`() {
        expectThrows<IllegalArgumentException> {
            TaxBracketGenerator.generateTaxBrackets(numBrackets = -1)
        }.message.isEqualTo("The number of tax brackets must be positive")

        expectThrows<IllegalArgumentException> {
            TaxBracketGenerator.generateTaxBrackets(numBrackets = 0)
        }.message.isEqualTo("The number of tax brackets must be positive")

        // positive # of brackets completes without failure
        expectThat(TaxBracketGenerator.generateTaxBrackets(numBrackets = 3))
            .hasSize(3)
    }

    @Test
    fun `cannot create more than 10,000 brackets`() {
        expectThat(TaxBracketGenerator.maxNumTaxBrackets)
            .isEqualTo(10_000)

        expectThrows<IllegalArgumentException> {
            TaxBracketGenerator.generateTaxBrackets(numBrackets = 10_001)
        }.message.isEqualTo("Cannot create more than 10000 tax brackets")

        // Creating exactly the maximum completes without failure
        expectThat(TaxBracketGenerator.generateTaxBrackets(numBrackets = 10_000))
            .hasSize(10_000)
    }

    @Test
    fun `the highest bracket must start at 0 when creating a single bracket`() {
        expectThrows<IllegalArgumentException> {
            TaxBracketGenerator.generateTaxBrackets(numBrackets = 1, lowerBoundDollarsOfHighestBracket = 1)
        }.message.isEqualTo("The lower bound of the highest bracket must be 0 when creating a single bracket")

        val brackets = TaxBracketGenerator.generateTaxBrackets(numBrackets = 1, lowerBoundDollarsOfHighestBracket = 0)
        expectThat(brackets)
            .hasSize(1)
    }

    @Test
    fun `lower bound must be large enough to support the number of brackets`() {
        expectThrows<IllegalArgumentException> {
            TaxBracketGenerator.generateTaxBrackets(numBrackets = 2, lowerBoundDollarsOfHighestBracket = 0)
        }.message.isEqualTo("Cannot create 2 brackets if the highest bracket starts at 0")

        expectThrows<IllegalArgumentException> {
            TaxBracketGenerator.generateTaxBrackets(numBrackets = 3, lowerBoundDollarsOfHighestBracket = 1)
        }.message.isEqualTo("Cannot create 3 brackets if the highest bracket starts at 1")

        // Allowing just enough range completes without failure
        val brackets = TaxBracketGenerator.generateTaxBrackets(numBrackets = 4, lowerBoundDollarsOfHighestBracket = 3)
        expectThat(brackets)
            .hasSize(4)

        for (index in brackets.indices) {
            val bracket = brackets[index]
            expectThat(bracket.from)
                .isEqualTo(index.dollars)
        }
    }

    @Test
    fun `brackets have proper structure`() {
        val seed = Random.nextLong()
        val random = Random(seed)
        println("Seed: $seed") // to be able to reproduce when encountering a failure

        repeat(100) {
            generateRandomBracketsAndValidateStructure(random)
        }
    }

    private fun generateRandomBracketsAndValidateStructure(random: Random) {
        val numBrackets = random.nextInt(1, 100)
        val lowerBoundDollarsOfHighestBracket = when (numBrackets) {
            1 -> 0
            else -> random.nextInt(numBrackets - 1, numBrackets * 100)
        }

        val brackets = TaxBracketGenerator.generateTaxBrackets(
            numBrackets = numBrackets,
            lowerBoundDollarsOfHighestBracket = lowerBoundDollarsOfHighestBracket,
            random = random
        )
        expectThat(brackets)
            .hasSize(numBrackets)

        with(brackets.first()) {
            expectThat(from)
                .isEqualTo(0.dollars)

            expectThat(taxRate.amountBps)
                .isGreaterThanOrEqualTo(0)
        }

        for (i in brackets.indices) {
            val bracket = brackets[i]
            val nextBracket = brackets.getOrNull(i + 1)

            if (nextBracket != null) {
                // lower bounds are strictly increasing
                expectThat(bracket.from.cents)
                    .isLessThan(nextBracket.from.cents)

                // upper bound matches the lower bound of the next bracket
                expectThat(bracket.to)
                    .isEqualTo(nextBracket.from)

                // tax rates are strictly increasing
                expectThat(bracket.taxRate.amountBps)
                    .isLessThan(nextBracket.taxRate.amountBps)
            }
        }

        with(brackets.last()) {
            expectThat(from)
                .isEqualTo(lowerBoundDollarsOfHighestBracket.dollars)

            expectThat(to)
                .isNull()

            expectThat(taxRate.amountBps)
                .isLessThan(100.percent.amountBps)
        }
    }
}
