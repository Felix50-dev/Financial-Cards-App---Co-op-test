package com.coperative.financialcardsApp.domain.repositories.cardRepositories

import com.coperative.financialcardsApp.common.Resource
import com.coperative.financialcardsApp.data.local.dao.CardDao
import com.coperative.financialcardsApp.data.mappers.toDomain
import com.coperative.financialcardsApp.data.mappers.toEntity
import com.coperative.financialcardsApp.data.remote.MockApi
import com.coperative.financialcardsApp.domain.model.Card
import com.coperative.financialcardsApp.domain.model.Transaction
import com.coperative.financialcardsApp.domain.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class CardRepositoryImpl(
    private val api: MockApi,
    private val dao: CardDao
) : CardRepository {

    override fun getCards(): Flow<Resource<List<Card>>> = flow {
        emit(Resource.Loading())

        try {
            // --- LOCAL FIRST ---
            val local = dao.getAllCards()
                .firstOrNull()
                ?.map { it.toDomain() }
                .orEmpty()

            if (local.isNotEmpty()) {
                emit(Resource.Success(local))
            }

            // --- REMOTE FETCH ---
            val remoteDtos = api.getCards().cards  // CardResponseDto.cards
            val remoteEntities = remoteDtos.map { it.toEntity() }
            dao.insertCards(remoteEntities)

            emit(Resource.Success(remoteDtos.map { it.toDomain() }))

        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }.flowOn(Dispatchers.IO)

    override fun getTransactions(cardId: String): Flow<Resource<List<Transaction>>> = flow<Resource<List<Transaction>>> {
        emit(Resource.Loading())

        try {
            val local = dao.getTransactionsForCard(cardId, 10)
                .firstOrNull()
                .orEmpty()                // fix here
                .map { it.toDomain() }

            if (local.isNotEmpty()) {
                emit(Resource.Success(local))
            }

            val remoteDtos = api.getTransactions(cardId).transactions
            dao.insertTransactions(remoteDtos.map { it.toEntity(cardId) })

            emit(Resource.Success(remoteDtos.map { it.toDomain() }))

        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error fetching transactions"))
        }
    }.flowOn(Dispatchers.IO)



    override suspend fun toggleBlock(cardId: String, isBlocked: Boolean) {
        withContext(Dispatchers.IO) {
            try {
                api.toggleBlock(cardId, isBlocked)
            } catch (_: Exception) {
                // ignore remote failure
            } finally {
                dao.updateBlockStatus(cardId, isBlocked)
            }
        }
    }

    override fun getUser(): Flow<Resource<User>> = flow {
        emit(Resource.Loading())

        try {
            val dto = api.getUser().user
            emit(Resource.Success(dto.toDomain()))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error fetching user"))
        }
    }.flowOn(Dispatchers.IO)
}
