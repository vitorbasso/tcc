package utils

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Asset
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.enum.TransactionType
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.time.LocalDateTime

infix fun BigDecimal.shouldBeEqual(other: BigDecimal) = this.compareTo(other) shouldBe 0
fun now(): LocalDateTime = LocalDateTime.now()
fun transaction(
    type: TransactionType = TransactionType.BUY,
    quantity: Long = (1..100L).random(),
    value: BigDecimal = BigDecimal.TEN,
    transactionDate: LocalDateTime = EasyRandomWrapper.random(),
    checkingValue: BigDecimal = BigDecimal.ZERO,
    checkingQuantity: Long = 0,
    daytradeQuantity: Long = 0,
    asset: Asset = EasyRandomWrapper.random()
) = Transaction(
    id = EasyRandomWrapper.random(),
    type = type,
    quantity = quantity,
    value = value,
    transactionDate = transactionDate,
    checkingValue = checkingValue,
    checkingQuantity = checkingQuantity,
    daytradeQuantity = daytradeQuantity,
    asset = asset
)