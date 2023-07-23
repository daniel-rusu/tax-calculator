package dataModel

data class TaxBracket(
    val lowerBound: Money,
    val upperBound: Money?, // null when the bracket has no upper bound
    val taxRate: Percent,
) {
    init {
        require(upperBound == null || upperBound > lowerBound) {
            "The upperbound $upperBound must be greater than the lower bound $lowerBound when an upper bound exists"
        }
    }
}
