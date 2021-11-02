package utils

import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.time.LocalDateTime

infix fun BigDecimal.shouldBeEqual(other: BigDecimal) = this.compareTo(other) shouldBe 0
fun now(): LocalDateTime = LocalDateTime.now()