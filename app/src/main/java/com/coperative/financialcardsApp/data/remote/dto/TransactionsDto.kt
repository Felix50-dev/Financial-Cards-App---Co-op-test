package com.coperative.financialcardsApp.data.remote.dto

import com.coperative.financialcardsApp.domain.model.Transaction
import org.json.JSONArray
import org.json.JSONObject

data class TransactionDto(
    val id: String,
    val cardId: String,
    val amount: Double,
    val currency: String,
    val merchant: String,      // fixed from 'description'
    val date: String,
    val type: String // "CREDIT" or "DEBIT"
) {
    companion object {
        fun fromJson(json: JSONObject): TransactionDto {
            return TransactionDto(
                id = json.optString("id"),
                cardId = json.optString("cardId"), // use the actual value from JSON
                amount = json.optDouble("amount"),
                currency = json.optString("currency"),
                merchant = json.optString("merchant"), // updated field
                date = json.optString("date"),
                type = json.optString("type")
            )
        }

        fun listFromJson(jsonArray: JSONArray): List<TransactionDto> {
            val list = mutableListOf<TransactionDto>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(fromJson(obj)) // don't override cardId
            }
            return list
        }
    }
}

// Convert to domain model, including cardId
fun TransactionDto.toDomain(): Transaction {
    return Transaction(
        id = id,
        cardId = cardId,           // store cardId for filtering later
        amount = amount,
        currency = currency,
        date = date,
        description = merchant,
    )
}
