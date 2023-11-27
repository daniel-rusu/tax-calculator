package dataModel.base

private const val CENTS_PER_DOLLAR = 100L

@JvmInline
value class Money private constructor(val cents: Long) {
    init {
        require(cents >= 0) { "Negative cents are not allowed: $cents" }
    }

    override fun toString(): String {
        val dollars = cents / CENTS_PER_DOLLAR

        val remainder = cents - dollars * CENTS_PER_DOLLAR
        val centsText = when {
            remainder == 0L -> ""
            remainder <= 9 -> ".0$remainder"
            else -> ".$remainder"
        }
        return "$$dollars$centsText"
    }

    operator fun plus(amount: Money): Money = Money(cents = this.cents + amount.cents)

    operator fun minus(amount: Money): Money = Money(cents = this.cents - amount.cents)

    operator fun times(amount: Long): Money = Money(cents = this.cents * amount)

    operator fun div(amount: Long): Money = Money(cents = this.cents / amount)

    operator fun compareTo(other: Money): Int = cents.compareTo(other.cents)

    companion object {
        fun ofCents(amount: Long): Money = Money(cents = amount)

        fun ofDollars(amount: Long): Money = Money(cents = amount * CENTS_PER_DOLLAR)

        fun of(dollars: Long, cents: Int): Money {
            require(dollars >= 0) { "Negative dollars are not allowed: $dollars" }
            require(cents in 0L..99L) { "Cents must be between 0 and 99 inclusive" }

            return Money(cents = dollars * CENTS_PER_DOLLAR + cents)
        }

        fun of(dollars: Int, cents: Int): Money = of(dollars.toLong(), cents)

        val Int.dollars: Money
            get() = ofDollars(this.toLong())

        val Int.cents: Money
            get() = ofCents(this.toLong())
    }
}
