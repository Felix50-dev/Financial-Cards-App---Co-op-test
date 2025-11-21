package com.coperative.financialcardsApp.data.remote

import com.coperative.financialcardsApp.data.remote.dto.CardDto
import com.coperative.financialcardsApp.data.remote.dto.TransactionResponseDto
import com.google.gson.annotations.SerializedName
//import com.coperative.financialcardsApp.data.remote.dto.UserResponseDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MockApi {

    data class CardsResponse(
        @SerializedName("cards") val cards: List<CardDto>
    )

    @GET("getCards")
    suspend fun getCardsRaw(): Response<ResponseBody>

    // GET TRANSACTIONS FOR A CARD
    @GET("cardTransactions")
    suspend fun getTransactions(
        @Query("cardId") cardId: String
    ): TransactionResponseDto

    // GET USER
//    @GET("getUser")
//    suspend fun getUser(): UserResponseDto

    suspend fun toggleBlock(cardId: String, isBlocked: Boolean) {
        kotlinx.coroutines.delay(500)
    }

    @GET("cardTransactions")
    suspend fun getTransactionsRaw(): Response<ResponseBody>

    @GET("getUser")
    suspend fun getUserRaw(): Response<ResponseBody>

    @GET("getCards")
    suspend fun getCards(): Response<CardsResponse>
}

