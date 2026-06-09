package com.fittrack.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.data.repository.AuthRepository
import com.fittrack.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<Unit>?>(null)
    val state: StateFlow<Resource<Unit>?> = _state

    fun register(email: String, password: String) = viewModelScope.launch {
        _state.value = Resource.Loading
        _state.value = authRepo.register(email, password)
    }

    fun login(email: String, password: String) = viewModelScope.launch {
        _state.value = Resource.Loading
        _state.value = authRepo.login(email, password)
    }

    fun logout() = viewModelScope.launch { authRepo.logout() }

    fun isLoggedIn() = authRepo.isLoggedIn()
}
