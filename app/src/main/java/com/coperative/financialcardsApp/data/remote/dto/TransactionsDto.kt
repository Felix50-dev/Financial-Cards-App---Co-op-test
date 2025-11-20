package com.coperative.financialcardsApp.data.remote.dto

import com.coperative.financialcardsApp.domain.model.Transaction
import com.squareup.moshi.Json

data class TransactionsResponseDto(
    @Json(name = "transactions") val transactions: List<TransactionDto>
)

data class TransactionDto(
    val id: String,
    val cardId: String,
    val amount: Double,
    val currency: String,
    val date: String,
    val merchant: String,
    val type: String
)

fun TransactionDto.toDomain(): Transaction {
    return Transaction(
        id = id,
        amount = amount,
        date = date,
        description = merchant,
        currency = currency
    )
}

