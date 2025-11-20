package com.coperative.financialcardsApp.data.mappers

import com.coperative.financialcardsApp.data.local.entities.CardEntity
import com.coperative.financialcardsApp.data.local.entities.TransactionEntity
import com.coperative.financialcardsApp.data.remote.dto.CardDto
import com.coperative.financialcardsApp.data.remote.dto.TransactionDto
import com.coperative.financialcardsApp.domain.model.Card
import com.coperative.financialcardsApp.domain.model.Transaction
import com.coperative.financialcardsApp.domain.model.User
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
private val mapAdapter = moshi.adapter(Map::class.java)

fun CardDto.toEntity(): CardEntity {
    // We'll store variant fields in extraJson to keep entity flexible
    val type = when (this.type) {
        "PREPAID" -> "PREPAID"
        "CREDIT" -> "CREDIT"
        "MULTI" -> "MULTI"
        "DEBIT" -> "DEBIT"
        else -> "PREPAID"
    }

    val extra = when (type) {
        "PREPAID" -> """{"loadBalance":${this.loadBalance}}"""
        "CREDIT" -> """{"creditLimit":${this.creditLimit},"dueDate":"${this.dueDate}"}"""
        "MULTI" -> moshi.adapter(Map::class.java).toJson(this.balances)
        "DEBIT" -> """{"linkedAccountName":"${this.linkedAccountName}","balance":${this.balance}}"""
        else -> null
    }

    return CardEntity(
        id = this.id,
        number = this.number,
        holderName = this.holderName,
        isBlocked = this.isBlocked,
        type = type,
        extraJson = extra
    )
}

fun CardEntity.toDomain(): Card {
    val type = this.type
    val extraJson = this.extraJson

    return when (type) {
        "PREPAID" -> {
            val loadBalance = extraJson?.let {
                moshi.adapter(Map::class.java).fromJson(it)?.get("loadBalance") as? Double
            } ?: 0.0
            Card.Prepaid(id, number, holderName, isBlocked, loadBalance)
        }
        "CREDIT" -> {
            val parsed = extraJson?.let { moshi.adapter(Map::class.java).fromJson(it) } ?: emptyMap<String, Any>()
            val creditLimit = (parsed["creditLimit"] as? Double) ?: 0.0
            val dueDate = (parsed["dueDate"] as? String) ?: ""
            Card.Credit(id, number, holderName, isBlocked, creditLimit, dueDate)
        }
        "MULTI" -> {
            val balances = extraJson?.let {
                moshi.adapter(Map::class.java).fromJson(it) as? Map<String, Double>
            } ?: emptyMap()
            Card.MultiCurrency(id, number, holderName, isBlocked, balances)
        }
        "DEBIT" -> {
            val parsed = extraJson?.let { moshi.adapter(Map::class.java).fromJson(it) } ?: emptyMap<String, Any>()
            val linked = (parsed["linkedAccountName"] as? String) ?: ""
            val balance = (parsed["balance"] as? Double) ?: 0.0
            Card.Debit(id, number, holderName, isBlocked, linked, balance)
        }
        else -> Card.Prepaid(id, number, holderName, isBlocked, 0.0)
    }
}

fun TransactionEntity.toDomain(): Transaction =
    Transaction(id = id, amount = amount, date = date, description = description, currency = currency)

fun TransactionDto.toEntity(cardId: String): TransactionEntity =
    TransactionEntity(id = id, cardId = cardId, amount = amount, date = date, description = description, currency = currency)

fun TransactionDto.toDomain(): Transaction =
    Transaction(id = id, amount = amount, date = date, description = description, currency = currency)

fun UserDto.toDomain(): User = User(name = name, avatarUrl = avatarUrl, email = email, phone = phone, address = address)

// DTO declarations are left to you (CardDto, TransactionDto, UserDto). Make sure fields (type, balances, etc.) match.
