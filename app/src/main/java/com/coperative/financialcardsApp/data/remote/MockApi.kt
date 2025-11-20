package com.coperative.financialcardsApp.data.remote

import com.coperative.financialcardsApp.data.remote.dto.CardResponseDto
import com.coperative.financialcardsApp.data.remote.dto.TransactionResponseDto
import com.coperative.financialcardsApp.data.remote.dto.UserResponseDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface MockApi {

    // GET CARDS
    @GET("getCards")
    suspend fun getCards(): CardResponseDto

    // GET TRANSACTIONS FOR A CARD
    @GET("cardTransactions")
    suspend fun getTransactions(
        @Query("cardId") cardId: String
    ): TransactionResponseDto

    // GET USER
    @GET("getUser")
    suspend fun getUser(): UserResponseDto

    suspend fun toggleBlock(cardId: String, isBlocked: Boolean) {
        kotlinx.coroutines.delay(500)
    }
}

object ApiClient {

    private const val BASE_URL = "https://card-services.free.beeceptor.com/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val api: MockApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(MockApi::class.java)
}

