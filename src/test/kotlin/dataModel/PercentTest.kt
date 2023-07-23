package dataModel

import dataModel.Money.Companion.dollars
import dataModel.Percent.Companion.percent
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class PercentTest {
    @Test
    fun `multiply percent times money`() {
        expectThat(50.percent * 6.dollars)
            .isEqualTo(3.dollars)
    }
}
