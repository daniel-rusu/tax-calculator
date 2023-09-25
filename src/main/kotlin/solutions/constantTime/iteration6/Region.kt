package solutions.constantTime.iteration6

class Region(
    /** The index of the chunk where this region starts */
    private val chunkOffset: Int,
    /** The chunk size for this region */
    private val chunkSize: Long,
) {
    /** [remainder] is the remaining income that falls in this region */
    fun getChunkIndex(remainder: Long): Int {
        return chunkOffset + (remainder / chunkSize).toInt()
    }
}
