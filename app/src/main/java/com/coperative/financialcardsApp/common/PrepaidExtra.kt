// file: CardMappers.kt
package com.coperative.financialcardsApp.common

import com.coperative.financialcardsApp.domain.model.Card
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.JsonAdapter
import android.util.Log
import com.coperative.financialcardsApp.data.local.entities.CardEntity
import com.coperative.financialcardsApp.data.remote.dto.CardDto
import com.squareup.moshi.adapter


// --- Extra data classes used for serializing variant fields ---
data class PrepaidExtra(val balance: Double)
data class CreditExtra(val creditLimit: Double, val dueDate: String)
data class MultiExtra(val balances: Map<String, Double>)
data class DebitExtra(val linkedAccountName: String, val balance: Double)

private const val TAG = "CardMappers"

// single Moshi instance
private val moshi: Moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

// --- Card -> CardEntity ---
fun Card.toEntity(): CardEntity {
    val typeString = when (this) {
        is Card.Prepaid -> "PREPAID"
        is Card.Credit -> "CREDIT"
        is Card.MultiCurrency -> "MULTI_CURRENCY"
        is Card.Debit -> "DEBIT"
    }

    val extraJson: String? = try {
        when (this) {
            is Card.Prepaid -> {
                val adapter: JsonAdapter<PrepaidExtra> = moshi.adapter(PrepaidExtra::class.java)
                adapter.toJson(PrepaidExtra(balance = this.loadBalance))
            }
            is Card.Credit -> {
                val adapter: JsonAdapter<CreditExtra> = moshi.adapter(CreditExtra::class.java)
                adapter.toJson(CreditExtra(creditLimit = this.creditLimit, dueDate = this.dueDate))
            }
            is Card.MultiCurrency -> {
                val adapter: JsonAdapter<MultiExtra> = moshi.adapter(MultiExtra::class.java)
                adapter.toJson(MultiExtra(balances = this.balances))
            }
            is Card.Debit -> {
                val adapter: JsonAdapter<DebitExtra> = moshi.adapter(DebitExtra::class.java)
                adapter.toJson(DebitExtra(linkedAccountName = this.linkedAccountName, balance = this.balance))
            }
            else -> null
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error serializing extraJson for card ${this.id}", e)
        null
    }

    return CardEntity(
        id = this.id,
        number = this.number,
        holderName = this.holderName,
        isBlocked = this.isBlocked,
        type = typeString,
        extraJson = extraJson
    )
}

@OptIn(ExperimentalStdlibApi::class)
fun CardEntity.toDomain(moshi: Moshi): Card {
    return when (type) {

        "PREPAID" -> {
            val obj = moshi.adapter<Map<String, Any>>().fromJson(extraJson!!)
            Card.Prepaid(
                id = id,
                number = number,
                holderName = holderName,
                isBlocked = isBlocked,
                loadBalance = (obj?.get("balance") as Double)
            )
        }

        "CREDIT" -> {
            val obj = moshi.adapter<Map<String, Any>>().fromJson(extraJson!!)
            Card.Credit(
                id = id,
                number = number,
                holderName = holderName,
                isBlocked = isBlocked,
                creditLimit = (obj?.get("creditLimit") as Double),
                dueDate = obj["dueDate"] as String
            )
        }

        "MULTI_CURRENCY" -> {
            val obj = moshi.adapter<Map<String, Any>>().fromJson(extraJson!!)
            @Suppress("UNCHECKED_CAST")
            val balances = obj?.get("balances") as Map<String, Double>
            Card.MultiCurrency(
                id = id,
                number = number,
                holderName = holderName,
                isBlocked = isBlocked,
                balances = balances
            )
        }

        "DEBIT" -> {
            val obj = moshi.adapter<Map<String, Any>>().fromJson(extraJson!!)
            Card.Debit(
                id = id,
                number = number,
                holderName = holderName,
                isBlocked = isBlocked,
                linkedAccountName = obj?.get("linkedAccountName") as String,
                balance = obj["balance"] as Double
            )
        }

        else -> error("Unknown type $type")
    }
}

