package com.coperative.financialcardsApp.ui.screens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coperative.financialcardsApp.common.Resource
import com.coperative.financialcardsApp.domain.model.Card
import com.coperative.financialcardsApp.domain.model.Transaction
import com.coperative.financialcardsApp.domain.model.User
import com.coperative.financialcardsApp.domain.repositories.cardRepositories.CardRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "CardViewModel"

@HiltViewModel
class CardViewModel @Inject constructor(
    private val repository: CardRepositoryImpl
) : ViewModel() {

    private val _cards = MutableStateFlow<Resource<List<Card>>>(Resource.Loading())
    val cards: StateFlow<Resource<List<Card>>> = _cards.asStateFlow()

    // Keep all transactions
    private val _allTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    private val _transactions = MutableStateFlow<Resource<List<Transaction>>>(Resource.Loading())
    val transactions: StateFlow<Resource<List<Transaction>>> = _transactions.asStateFlow()

    init {
        fetchCards()
        fetchAllTransactions()
    }

    fun fetchCards() {
        viewModelScope.launch {
            repository.getCards().collect { _cards.value = it }
        }
    }

    fun getCardById(cardId: String): Card? {
        val current = _cards.value
        return if (current is Resource.Success) {
            current.data.firstOrNull { it.id == cardId }
        } else null
    }
    fun fetchAllTransactions() {
        viewModelScope.launch {
            repository.getAllTransactions()// Returns Flow<Resource<List<Transaction>>>
                .collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _allTransactions.value = resource.data
                        }
                        else -> Unit
                    }
                }
        }
    }

    // Filter transactions for a specific card
    fun fetchTransactions(cardId: String) {
        val filtered = _allTransactions.value.filter { it.cardId == cardId }
        _transactions.value = Resource.Success(filtered)
    }

    private val _user = MutableStateFlow<Resource<User>>(Resource.Loading())
    val user: StateFlow<Resource<User>> = _user.asStateFlow()

    fun fetchUser() {
        viewModelScope.launch {
            repository.getUser().collect { _user.value = it }
        }
    }

    fun toggleCardBlock(cardId: String, isBlocked: Boolean) {
        viewModelScope.launch {
            repository.toggleBlock(cardId, isBlocked)
            fetchCards()
        }
    }
}
