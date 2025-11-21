package com.coperative.financialcardsApp.data.mappers

import com.coperative.financialcardsApp.data.local.entities.CardEntity
import com.coperative.financialcardsApp.data.local.entities.TransactionEntity
import com.coperative.financialcardsApp.data.remote.dto.CardDto
import com.coperative.financialcardsApp.data.remote.dto.TransactionDto
import com.coperative.financialcardsApp.domain.model.Card
import com.coperative.financialcardsApp.domain.model.Transaction
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

// For Prepaid
data class PrepaidExtra(
    val balance: Double
)

// For Credit
data class CreditExtra(
    val creditLimit: Double,
    val dueDate: String
)

// For MultiCurrency
data class MultiExtra(
    val balances: Map<String, Double>
)

// For Debit
data class DebitExtra(
    val linkedAccountName: String,
    val balance: Double
)

fun CardEntity.toDomain(): Card {
    return when (type) {
        "PREPAID" -> {
            val balance = extraJson?.let {
                val adapter = moshi.adapter(PrepaidExtra::class.java)
                adapter.fromJson(it)?.balance ?: 0.0
            } ?: 0.0
            Card.Prepaid(id, number, holderName, isBlocked, balance)
        }

        "CREDIT" -> {
            val creditExtra = extraJson?.let {
                val adapter = moshi.adapter(CreditExtra::class.java)
                adapter.fromJson(it)
            }
            Card.Credit(
                id = id,
                number = number,
                holderName = holderName,
                isBlocked = isBlocked,
                creditLimit = creditExtra?.creditLimit ?: 0.0,
                dueDate = creditExtra?.dueDate ?: ""
            )
        }

        "MULTI_CURRENCY" -> {
            val multiExtra = extraJson?.let {
                val adapter = moshi.adapter(MultiExtra::class.java)
                adapter.fromJson(it)
            }
            Card.MultiCurrency(
                id = id,
                number = number,
                holderName = holderName,
                isBlocked = isBlocked,
                balances = multiExtra?.balances.orEmpty()
            )
        }

        "DEBIT" -> {
            val debitExtra = extraJson?.let {
                val adapter = moshi.adapter(DebitExtra::class.java)
                adapter.fromJson(it)
            }
            Card.Debit(
                id = id,
                number = number,
                holderName = holderName,
                isBlocked = isBlocked,
                linkedAccountName = debitExtra?.linkedAccountName ?: "Unknown",
                balance = debitExtra?.balance ?: 0.0
            )
        }

        else -> throw IllegalArgumentException("Unknown card type: $type")
    }
}


fun TransactionEntity.toDomain() = Transaction(id, cardId,amount, date, description, currency)

fun CardDto.toEntity(): CardEntity {
    val extra = when(type) {
        "PREPAID", "DEBIT" -> moshi.adapter(Map::class.java).toJson(mapOf("balance" to (balance ?: 0.0)))
        "CREDIT" -> moshi.adapter(Map::class.java).toJson(mapOf("creditLimit" to (creditLimit ?: 0.0), "dueDate" to (dueDate ?: "")))
        "MULTI_CURRENCY" -> moshi.adapter(Map::class.java).toJson(wallets?.associate { it.currency to it.balance } ?: emptyMap<String, Double>())
        else -> null
    }
    return CardEntity(id, cardNumber, holderName, status == "BLOCKED", type, extra)
}

fun TransactionDto.toEntity(cardId: String) = TransactionEntity(id, cardId, amount, currency, date, merchant)
