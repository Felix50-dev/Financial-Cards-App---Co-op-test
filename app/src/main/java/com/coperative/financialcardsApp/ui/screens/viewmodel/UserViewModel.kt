package com.coperative.financialcardsApp.ui.screens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coperative.financialcardsApp.common.Resource
import com.coperative.financialcardsApp.domain.model.User
import com.coperative.financialcardsApp.domain.repositories.cardRepositories.CardRepository
import com.coperative.financialcardsApp.domain.repositories.cardRepositories.CardRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: CardRepositoryImpl
) : ViewModel() {
    private val _user = MutableStateFlow<Resource<User>>(Resource.Loading())
    val user: StateFlow<Resource<User>> = _user

    init {
        fetchUser()
    }

    fun fetchUser() {
        viewModelScope.launch {
            repository.getUser().collectLatest { resource ->
                _user.value = resource
            }
        }
    }
}
