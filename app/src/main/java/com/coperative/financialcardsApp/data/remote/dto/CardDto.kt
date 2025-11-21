package com.coperative.financialcardsApp.data.remote.dto

import com.coperative.financialcardsApp.domain.model.Card
import org.json.JSONObject

data class CardDto(
    val id: String,
    val userId: String,
    val type: String,        // "PREPAID", "CREDIT", "MULTI_CURRENCY", "DEBIT"
    val name: String,
    val cardNumber: String,
    val holderName: String,
    val expiryDate: String,
    val status: String,
    val balance: Double? = null,         // only for PREPAID / DEBIT
    val currency: String? = null,
    val creditLimit: Double? = null,     // only for CREDIT
    val currentSpend: Double? = null,    // only for CREDIT
    val dueDate: String? = null,         // only for CREDIT
    val wallets: List<WalletDto>? = null,// only for MULTI_CURRENCY
    val linkedAccountName: String? = null // only for DEBIT
) {
    companion object {
        fun fromJson(json: JSONObject): CardDto {
            val walletsList = mutableListOf<WalletDto>()
            json.optJSONArray("wallets")?.let { arr ->
                for (i in 0 until arr.length()) {
                    val w = arr.getJSONObject(i)
                    walletsList.add(
                        WalletDto(
                            currency = w.optString("currency"),
                            flag = w.optString("flag"),
                            balance = w.optDouble("balance", 0.0)
                        )
                    )
                }
            }

            return CardDto(
                id = json.optString("id"),
                userId = json.optString("userId"),
                type = json.optString("type"),
                name = json.optString("name"),
                cardNumber = json.optString("cardNumber"),
                holderName = json.optString("holderName"),
                expiryDate = json.optString("expiryDate"),
                status = json.optString("status"),
                balance = if (json.has("balance")) json.optDouble("balance") else null,
                currency = json.optString("currency", null),
                creditLimit = if (json.has("creditLimit")) json.optDouble("creditLimit") else null,
                currentSpend = if (json.has("currentSpend")) json.optDouble("currentSpend") else null,
                dueDate = json.optString("dueDate", null),
                wallets = if (walletsList.isNotEmpty()) walletsList else null,
                linkedAccountName = json.optString("linkedAccountName", null)
            )
        }
    }
}

data class WalletDto(
    val currency: String,
    val flag: String,
    val balance: Double
)

fun CardDto.toDomain(): Card {
    return when (type.uppercase()) {
        "PREPAID" -> Card.Prepaid(
            id = id,
            number = cardNumber,
            holderName = holderName,
            isBlocked = status.uppercase() == "BLOCKED",
            loadBalance = balance ?: 0.0
        )
        "CREDIT" -> Card.Credit(
            id = id,
            number = cardNumber,
            holderName = holderName,
            isBlocked = status.uppercase() == "BLOCKED",
            creditLimit = creditLimit ?: 0.0,
            dueDate = dueDate ?: ""
        )
        "MULTI_CURRENCY" -> Card.MultiCurrency(
            id = id,
            number = cardNumber,
            holderName = holderName,
            isBlocked = status.uppercase() == "BLOCKED",
            balances = wallets?.associate { it.currency to it.balance } ?: emptyMap()
        )
        "DEBIT" -> Card.Debit(
            id = id,
            number = cardNumber,
            holderName = holderName,
            isBlocked = status.uppercase() == "BLOCKED",
            linkedAccountName = linkedAccountName ?: "",
            balance = balance ?: 0.0
        )
        else -> throw IllegalArgumentException("Unknown card type: $type")
    }
}
