package dataModel.base

data class TaxBracket(
    val taxRate: Percent,
    val from: Money,
    val to: Money? = null, // null when the bracket has no upper bound
) {
    init {
        require(to == null || to > from) {
            "The upperbound $to must be greater than the lower bound $from when an upper bound exists"
        }
    }
}
