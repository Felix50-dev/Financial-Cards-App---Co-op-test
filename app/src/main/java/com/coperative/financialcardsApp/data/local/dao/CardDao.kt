package com.coperative.financialcardsApp.data.local.dao

import androidx.room.*
import com.coperative.financialcardsApp.data.local.entities.CardEntity
import com.coperative.financialcardsApp.data.local.entities.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {

    @Query("SELECT * FROM cards ORDER BY holderName ASC")
    fun getAllCards(): Flow<List<CardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<CardEntity>)

    @Query("UPDATE cards SET isBlocked = :isBlocked WHERE id = :cardId")
    suspend fun updateBlockStatus(cardId: String, isBlocked: Boolean)

    @Query("SELECT * FROM transactions WHERE cardId = :cardId ORDER BY date DESC LIMIT :limit")
    fun getTransactionsForCard(cardId: String, limit: Int = 10): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)
}
