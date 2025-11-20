package com.coperative.financialcardsApp.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TransactionResponseDto(
    val transactions: List<TransactionDto>
)