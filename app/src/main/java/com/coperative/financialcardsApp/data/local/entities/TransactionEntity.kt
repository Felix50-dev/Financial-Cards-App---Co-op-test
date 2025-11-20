package com.coperative.financialcardsApp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val cardId: String,
    val amount: Double,
    val date: String,
    val description: String,
    val currency: String?
)
