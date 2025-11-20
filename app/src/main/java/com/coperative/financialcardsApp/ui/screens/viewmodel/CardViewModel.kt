package com.coperative.financialcardsApp.ui.screens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coperative.financialcardsApp.common.Resource
import com.coperative.financialcardsApp.domain.model.Card
import com.coperative.financialcardsApp.domain.model.Transaction
import com.coperative.financialcardsApp.domain.model.User
import com.coperative.financialcardsApp.domain.repositories.cardRepositories.CardRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CardViewModel(
    private val repository: CardRepository
) : ViewModel() {

    private val _cards = MutableStateFlow<Resource<List<Card>>>(Resource.Loading())
    val cards: StateFlow<Resource<List<Card>>> = _cards.asStateFlow()

    fun fetchCards() {
        viewModelScope.launch {
            repository.getCards()
                .collect { _cards.value = it }
        }
    }

    private val _transactions = MutableStateFlow<Resource<List<Transaction>>>(Resource.Loading())
    val transactions: StateFlow<Resource<List<Transaction>>> = _transactions.asStateFlow()

    fun fetchTransactions(cardId: String) {
        viewModelScope.launch {
            repository.getTransactions(cardId)
                .collect { _transactions.value = it }
        }
    }
    private val _user = MutableStateFlow<Resource<User>>(Resource.Loading())
    val user: StateFlow<Resource<User>> = _user.asStateFlow()

    fun fetchUser() {
        viewModelScope.launch {
            repository.getUser()
                .collect { _user.value = it }
        }
    }

    fun toggleCardBlock(cardId: String, isBlocked: Boolean) {
        viewModelScope.launch {
            repository.toggleBlock(cardId, isBlocked)
            // Optional: refresh cards after toggle
            fetchCards()
        }
    }
}
