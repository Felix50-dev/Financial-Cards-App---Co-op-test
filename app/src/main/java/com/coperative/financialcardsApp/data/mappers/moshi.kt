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

fun CardEntity.toDomain(): Card {
    return when (type) {
        "PREPAID" -> {
            val balance = extraJson?.let { moshi.adapter(Double::class.java).fromJson(it) } ?: 0.0
            Card.Prepaid(id, number, holderName, isBlocked, balance)
        }
        "CREDIT" -> {
            val map = extraJson?.let { moshi.adapter(Map::class.java).fromJson(it) as? Map<String, Any> }.orEmpty()
            val creditLimit = map["creditLimit"] as? Double ?: 0.0
            val dueDate = map["dueDate"] as? String ?: ""
            Card.Credit(id, number, holderName, isBlocked, creditLimit, dueDate)
        }
        "MULTI" -> {
            val map = extraJson?.let { moshi.adapter(Map::class.java).fromJson(it) as? Map<String, Double> }.orEmpty()
            Card.MultiCurrency(id, number, holderName, isBlocked, map)
        }
        "DEBIT" -> {
            val map = extraJson?.let { moshi.adapter(Map::class.java).fromJson(it) as? Map<String, Any> }.orEmpty()
            val balance = map["balance"] as? Double ?: 0.0
            val linkedAccountName = map["linkedAccountName"] as? String ?: "Unknown"
            Card.Debit(id, number, holderName, isBlocked, linkedAccountName, balance)
        }
        else -> throw IllegalArgumentException("Unknown card type: $type")
    }
}

fun TransactionEntity.toDomain() = Transaction(id, amount, date, description, currency)

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
