package dataModel.base

import dataModel.base.Money.Companion.cents
import dataModel.base.Money.Companion.dollars
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue
import strikt.assertions.message

class MoneyTest {
    @Test
    fun `toString validation`() {
        expectThat(0.dollars.toString())
            .isEqualTo("$0")

        expectThat(9.cents.toString())
            .isEqualTo("$0.09")

        expectThat(25.cents.toString())
            .isEqualTo("$0.25")

        expectThat(3.dollars.toString())
            .isEqualTo("$3")

        expectThat(Money.of(dollars = 7, cents = 8).toString())
            .isEqualTo("$7.08")

        expectThat(Money.of(dollars = 7, cents = 11).toString())
            .isEqualTo("$7.11")
    }

    @Test
    fun `cannot create negative money amounts`() {
        expectThrows<IllegalArgumentException> {
            Money.ofCents(-10L)
        }.message.isEqualTo("Negative cents are not allowed: -10")

        expectThrows<IllegalArgumentException> {
            (-10).cents
        }.message.isEqualTo("Negative cents are not allowed: -10")

        expectThrows<IllegalArgumentException> {
            Money.ofDollars(-5L)
        }.message.isEqualTo("Negative cents are not allowed: -500")

        expectThrows<IllegalArgumentException> {
            (-5).dollars
        }.message.isEqualTo("Negative cents are not allowed: -500")

        expectThrows<IllegalArgumentException> {
            Money.of(dollars = -3, cents = 25)
        }.message.isEqualTo("Negative dollars are not allowed: -3")

        expectThrows<IllegalArgumentException> {
            Money.of(dollars = 10, cents = -1)
        }.message.isEqualTo("Cents must be between 0 and 99 inclusive")

        expectThrows<IllegalArgumentException> {
            Money.of(dollars = 10, cents = 100)
        }.message.isEqualTo("Cents must be between 0 and 99 inclusive")
    }

    @Test
    fun `adding money`() {
        expectThat(7.dollars + 0.dollars)
            .isEqualTo(7.dollars)

        expectThat(7.dollars + 0.cents)
            .isEqualTo(7.dollars)

        expectThat(3.dollars + 5.dollars)
            .isEqualTo(8.dollars)

        expectThat(50.cents + 50.cents)
            .isEqualTo(1.dollars)

        expectThat(3.dollars + 25.cents)
            .isEqualTo(Money.of(dollars = 3, cents = 25))
    }

    @Test
    fun `subtracting money`() {
        expectThat(7.dollars - 0.dollars)
            .isEqualTo(7.dollars)

        expectThat(7.dollars - 0.cents)
            .isEqualTo(7.dollars)

        expectThat(5.dollars - 3.dollars)
            .isEqualTo(2.dollars)

        expectThat(2.dollars - 2.dollars)
            .isEqualTo(0.dollars)

        expectThat(3.dollars - 50.cents)
            .isEqualTo(Money.of(dollars = 2, cents = 50))
    }

    @Test
    fun `subtraction cannot result in negative money`() {
        expectThrows<IllegalArgumentException> {
            2.dollars - 5.dollars
        }.message.isEqualTo("Negative cents are not allowed: -300")
    }

    @Test
    fun `multiplying money`() {
        expectThat(3.dollars * 5)
            .isEqualTo(15.dollars)

        expectThat(50.cents * 0)
            .isEqualTo(0.cents)
    }

    @Test
    fun `dividing money`() {
        expectThat(6.dollars / 2)
            .isEqualTo(3.dollars)

        expectThat(5.dollars / 4)
            .isEqualTo(Money.of(dollars = 1, cents = 25))
    }

    @Test
    fun `cents are rounded down during division`() {
        expectThat(29.cents / 10)
            .isEqualTo(2.cents)
    }

    @Test
    fun `comparing money`() {
        expectThat(4.dollars == 4.dollars)
            .isTrue()

        expectThat(4.dollars != 4.dollars)
            .isFalse()

        expectThat(3.dollars == 7.dollars)
            .isFalse()

        expectThat(5.dollars == 5.cents)
            .isFalse()

        expectThat(3.dollars > 2.dollars)
            .isTrue()

        expectThat(Money.of(dollars = 5, cents = 1) < 5.dollars)
            .isFalse()
    }
}
