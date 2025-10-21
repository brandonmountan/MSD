package com.example.objectdetection.viewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.objectdetection.network.ApiClient
import kotlinx.coroutines.launch

// State for authentication UI
data class AuthState(
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val apiClient = ApiClient(application)

    private val _authState = mutableStateOf(AuthState())
    val authState: State<AuthState> = _authState

    init {
        // Check if user is already logged in when ViewModel is created
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            val isLoggedIn = apiClient.isLoggedIn()
            _authState.value = _authState.value.copy(isLoggedIn = isLoggedIn)
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(
                isLoading = true,
                error = null,
                message = null
            )

            val result = apiClient.login(username, password)

            _authState.value = if (result.isSuccess) {
                _authState.value.copy(
                    isLoading = false,
                    isLoggedIn = true,
                    message = result.getOrNull()
                )
            } else {
                _authState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun register(username: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(
                isLoading = true,
                error = null,
                message = null
            )

            val result = apiClient.register(username, password)

            _authState.value = if (result.isSuccess) {
                _authState.value.copy(
                    isLoading = false,
                    message = result.getOrNull()
                )
            } else {
                _authState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            apiClient.logout()
            _authState.value = AuthState(isLoggedIn = false)
        }
    }

    fun clearMessages() {
        _authState.value = _authState.value.copy(error = null, message = null)
    }
}