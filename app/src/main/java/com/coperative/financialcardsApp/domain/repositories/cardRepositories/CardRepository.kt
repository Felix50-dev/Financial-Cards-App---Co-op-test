package com.coperative.financialcardsApp.domain.repositories.cardRepositories

import com.coperative.financialcardsApp.common.Resource
import com.coperative.financialcardsApp.domain.model.Card
import com.coperative.financialcardsApp.domain.model.Transaction
import com.coperative.financialcardsApp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface CardRepository {
    fun getCards(): Flow<Resource<List<Card>>>
    fun getAllTransactions(): Flow<Resource<List<Transaction>>>
    suspend fun toggleBlock(cardId: String, isBlocked: Boolean)
    fun getUser(): Flow<Resource<User>>
}

