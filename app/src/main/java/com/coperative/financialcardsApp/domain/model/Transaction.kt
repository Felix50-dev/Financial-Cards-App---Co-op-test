package com.coperative.financialcardsApp.domain.model

data class Transaction(
    val id: String,
    val cardId: String,
    val amount: Double,
    val currency: String = "KES",
    val date: String,
    val merchant: String,
    val type: String // DEBIT / CREDIT
)
