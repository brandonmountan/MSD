package com.example.captainslog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.captainslog.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for authentication screens (login and register)
 * Manages authentication state and user login/registration
 *
 * @property apiClient API client for network requests
 */
class AuthViewModel(
    private val apiClient: ApiClient
) : ViewModel() {

    // UI state for authentication screen
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    /**
     * Check if user is already logged in on app start
     */
    fun checkLoginStatus() {
        viewModelScope.launch {
            // Check if authentication token exists in DataStore
            if (apiClient.isLoggedIn()) {
                // User is already logged in, navigate to main screen
                _uiState.value = AuthUiState.LoggedIn
            }
        }
    }

    /**
     * Login with username and password
     *
     * @param username User's username
     * @param password User's password
     */
    fun login(username: String, password: String) {
        // Validate input fields
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Username and password required")
            return
        }

        // Set loading state
        _uiState.value = AuthUiState.Loading

        // Launch coroutine for network request
        viewModelScope.launch {
            // Call API client to login
            val result = apiClient.login(username, password)

            // Update UI state based on result
            _uiState.value = if (result.isSuccess) {
                AuthUiState.LoggedIn
            } else {
                AuthUiState.Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    /**
     * Register a new user account
     *
     * @param username Username for new account
     * @param password Password for new account
     */
    fun register(username: String, password: String) {
        // Validate input fields
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Username and password required")
            return
        }

        // Validate password length
        if (password.length < 6) {
            _uiState.value = AuthUiState.Error("Password must be at least 6 characters")
            return
        }

        // Set loading state
        _uiState.value = AuthUiState.Loading

        // Launch coroutine for network request
        viewModelScope.launch {
            // Call API client to register
            val result = apiClient.register(username, password)

            // Update UI state based on result
            _uiState.value = if (result.isSuccess) {
                // Registration successful, show success message
                AuthUiState.Error(result.getOrNull() ?: "Registration successful!")
            } else {
                AuthUiState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    /**
     * Reset UI state to idle (e.g., after showing error message)
     */
    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    /**
     * Logout current user
     */
    fun logout() {
        viewModelScope.launch {
            // Call API client to logout
            apiClient.logout()
            // Reset UI state to idle
            _uiState.value = AuthUiState.Idle
        }
    }
}

/**
 * Sealed class representing different states of authentication UI
 */
sealed class AuthUiState {
    // Initial/idle state - waiting for user action
    object Idle : AuthUiState()

    // Loading state - network request in progress
    object Loading : AuthUiState()

    // Logged in state - successful authentication
    object LoggedIn : AuthUiState()

    // Error state - show error message to user
    data class Error(val message: String) : AuthUiState()
}