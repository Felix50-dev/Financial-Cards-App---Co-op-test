package com.coperative.financialcardsApp.domain.repositories.cardRepositories

import android.util.Log
import com.coperative.financialcardsApp.common.Resource
import com.coperative.financialcardsApp.common.toEntity
import com.coperative.financialcardsApp.data.local.dao.CardDao
import com.coperative.financialcardsApp.data.mappers.toDomain
import com.coperative.financialcardsApp.data.mappers.toEntity
import com.coperative.financialcardsApp.data.remote.MockApi
import com.coperative.financialcardsApp.data.remote.dto.CardDto
import com.coperative.financialcardsApp.data.remote.dto.TransactionDto
import com.coperative.financialcardsApp.data.remote.dto.UserDto
import com.coperative.financialcardsApp.data.remote.dto.fromJson
import com.coperative.financialcardsApp.data.remote.dto.toDomain
import com.coperative.financialcardsApp.domain.model.Card
import com.coperative.financialcardsApp.domain.model.Transaction
import com.coperative.financialcardsApp.domain.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import javax.inject.Inject

private const val TAG = "CardRepositoryImpl"


data class CardResponseWrapper(
    val cards: Any?
)
class CardRepositoryImpl @Inject constructor(
    private val api: MockApi,
    private val dao: CardDao
) : CardRepository {
    override fun getCards(): Flow<Resource<List<Card>>> = flow {
        emit(Resource.Loading())

        try {
            val local = dao.getAllCards().firstOrNull()?.map { it.toDomain() }.orEmpty()
            if (local.isNotEmpty()) emit(Resource.Success(local))

            // ==== REMOTE ====
            val response = api.getCardsRaw() // returns ResponseBody
            if (!response.isSuccessful || response.body() == null) {
                emit(Resource.Error("Network error ${response.code()}"))
                return@flow
            }

            val bodyString = response.body()!!.string()
            Log.d(TAG, "Raw API response: $bodyString")

            // Helper: convert a JSONArray to domain list
            fun JSONArray.toDomainList(): List<Card> =
                (0 until this.length()).map { i ->
                    CardDto.fromJson(this.getJSONObject(i)).toDomain()
                }

            val domainCards: List<Card> = try {
                // Try a smart parse using JSONTokener (detects JSONArray, JSONObject, or String)
                when (val parsed = JSONTokener(bodyString).nextValue()) {
                    is JSONArray -> parsed.toDomainList()

                    is JSONObject -> {
                        // If it's an object with "cards", use it
                        if (parsed.has("cards")) {
                            parsed.getJSONArray("cards").toDomainList()
                        } else {
                            // maybe the object itself is the array like {"0": {...}, ...} - try to find first array
                            val possibleArray = parsed.keys().asSequence()
                                .mapNotNull { key ->
                                    try {
                                        parsed.get(key).let { v ->
                                            if (v is JSONArray) v else null
                                        }
                                    } catch (_: Exception) { null }
                                }
                                .firstOrNull()
                            if (possibleArray != null) possibleArray.toDomainList()
                            else throw JSONException("No cards array present in JSON object")
                        }
                    }

                    is String -> {
                        // String-wrapped JSON: unescape and re-parse
                        val inner = (parsed as String)
                            .removePrefix("\"")
                            .removeSuffix("\"")
                            .replace("\\\"", "\"")
                            .replace("\\n", "")
                            .trim()

                        // Try parse inner content again
                        when (val innerParsed = try {
                            JSONTokener(inner).nextValue()
                        } catch (e: JSONException) { null }) {
                            is JSONArray -> innerParsed.toDomainList()
                            is JSONObject -> {
                                if (innerParsed.has("cards")) innerParsed.getJSONArray("cards").toDomainList()
                                else {
                                    // fallback: try extract first [...] occurrence
                                    val firstArray = inner.substringAfterFirstOrNull('[')
                                    if (firstArray != null) {
                                        val arrString = "[" + firstArray.substringBeforeLastOrNull(']') + "]"
                                        JSONArray(arrString).toDomainList()
                                    } else throw JSONException("Couldn't parse inner JSON string")
                                }
                            }
                            else -> {
                                // last-resort: extract the first bracketed array substring from the raw bodyString
                                val start = bodyString.indexOf('[')
                                val end = bodyString.lastIndexOf(']')
                                if (start >= 0 && end > start) {
                                    JSONArray(bodyString.substring(start, end + 1)).toDomainList()
                                } else {
                                    throw JSONException("Unable to locate JSON array in response")
                                }
                            }
                        }
                    }

                    else -> throw JSONException("Unknown JSON root type")
                }
            } catch (e: JSONException) {
                Log.e(TAG, "JSON parsing error: ${e.message}")
                throw e
            }
            dao.insertCards(domainCards.map { it.toEntity() })
            Log.d(TAG, "getCards: $domainCards")

            emit(Resource.Success(domainCards))

        } catch (e: Exception) {
            Log.e(TAG, "Error fetching cards", e)
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }.flowOn(Dispatchers.IO)

    // --- small helper extension used above ---
    private fun String.substringAfterFirstOrNull(ch: Char): String? {
        val idx = this.indexOf(ch)
        return if (idx >= 0 && idx + 1 < this.length) this.substring(idx + 1) else null
    }
    private fun String.substringBeforeLastOrNull(ch: Char): String? {
        val idx = this.lastIndexOf(ch)
        return if (idx >= 0) this.substring(0, idx) else null
    }

    override fun getAllTransactions(): Flow<Resource<List<Transaction>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getTransactionsRaw() // no cardId
            if (response.isSuccessful && response.body() != null) {
                val rawJson = response.body()!!.string()
                val jsonArray = JSONArray(rawJson) // or JSONObject(rawJson).getJSONArray("transactions")
                val dtos = TransactionDto.listFromJson(jsonArray)
                val domainTransactions = dtos.map { it.toDomain() }

                // Insert all into local DB
                dao.insertTransactions(dtos.map { it.toEntity() })
                emit(Resource.Success(domainTransactions))
            } else {
                emit(Resource.Error("Failed to fetch transactions"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error fetching transactions"))
        }
    }


    override suspend fun toggleBlock(cardId: String, isBlocked: Boolean) {
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Toggling block status for $cardId -> $isBlocked")
                api.toggleBlock(cardId, isBlocked)
            } catch (e: Exception) {
                Log.e(TAG, "Error toggling block status remotely", e)
            } finally {
                dao.updateBlockStatus(cardId, isBlocked)
                Log.d(TAG, "Updated local DB block status for $cardId")
            }
        }
    }

    override fun getUser(): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        Log.d(TAG, "Fetching user info")

        try {
            val response = api.getUserRaw() // suspend fun getUserRaw(): Response<ResponseBody>
            val bodyString = response.body()?.string().orEmpty()
            Log.d(TAG, "Raw user response: $bodyString")

            if (bodyString.isBlank()) {
                emit(Resource.Error("Empty user response"))
                return@flow
            }

            val fixedBody = if (!bodyString.trim().startsWith("{")) "{ $bodyString }" else bodyString
            val jsonObject = JSONObject(fixedBody)
            val userJson = jsonObject.getJSONObject("user")
            val userDto = UserDto.fromJson(userJson)
            emit(Resource.Success(userDto.toDomain()))

        } catch (e: Exception) {
            Log.e(TAG, "Error fetching or parsing user", e)
            emit(Resource.Error(e.message ?: "Error fetching user"))
        }
    }.flowOn(Dispatchers.IO)

}


