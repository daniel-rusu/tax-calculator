package dataModel

private const val BASIS_POINTS_PER_PERCENT = 100L
private const val BASIS_POINTS_PER_100_PERCENT = 100 * BASIS_POINTS_PER_PERCENT

@JvmInline
value class Percent private constructor(val amountBps: Long) {
    operator fun times(money: Money): Money = money * amountBps / BASIS_POINTS_PER_100_PERCENT

    companion object {
        fun ofPercent(amount: Long): Percent = Percent(amountBps = amount * BASIS_POINTS_PER_PERCENT)

        fun ofBasisPoints(amount: Long): Percent = Percent(amountBps = amount)
    }
}
