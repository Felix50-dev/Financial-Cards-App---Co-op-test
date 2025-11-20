package com.coperative.financialcardsApp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey val id: String,
    val number: String,
    val holderName: String,
    val isBlocked: Boolean,
    val type: String, // PREPAID | CREDIT | MULTI | DEBIT
    val extraJson: String? = null // variant fields serialized as JSON
)
