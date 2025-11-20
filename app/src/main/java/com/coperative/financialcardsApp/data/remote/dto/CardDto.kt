package com.coperative.financialcardsApp.data.remote.dto

import com.coperative.financialcardsApp.domain.model.Card
import com.squareup.moshi.Json

data class CardResponseDto(
    @Json(name = "cards") val cards: List<CardDto>
)

data class CardDto(
    val id: String,
    val userId: String,
    val type: String,        // PREPAID, CREDIT, MULTI_CURRENCY, DEBIT
    val name: String,
    @Json(name = "cardNumber") val cardNumber: String,
    val holderName: String,
    val expiryDate: String,
    val status: String,      // ACTIVE/BLOCKED
    val balance: Double?,    // prepaid + debit
    val currency: String?,

    // credit-only fields
    val currentSpend: Double? = null,
    val creditLimit: Double? = null,
    val dueDate: String? = null,

    // debit-only fields
    val linkedAccountName: String? = null,

    // multi-currency wallets
    val wallets: List<WalletDto>? = null
)

data class WalletDto(
    val currency: String,
    val flag: String,
    val balance: Double
)

fun CardDto.toDomain(): Card {
    return when (type) {
        "PREPAID" -> Card.Prepaid(id, cardNumber, holderName, status == "BLOCKED", balance ?: 0.0)
        "CREDIT" -> Card.Credit(id, cardNumber, holderName, status == "BLOCKED", creditLimit ?: 0.0, dueDate ?: "")
        "MULTI_CURRENCY" -> Card.MultiCurrency(id, cardNumber, holderName, status == "BLOCKED",
            wallets?.associate { it.currency to it.balance } ?: emptyMap())
        "DEBIT" -> Card.Debit(id, cardNumber, holderName, status == "BLOCKED", linkedAccountName ?: "Unknown", balance ?: 0.0)
        else -> throw IllegalArgumentException("Unknown card type: $type")
    }
}

