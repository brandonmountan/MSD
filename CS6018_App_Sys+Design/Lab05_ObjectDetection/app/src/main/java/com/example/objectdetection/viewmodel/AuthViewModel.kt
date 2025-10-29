package com.example.objectdetection.viewmodel

// Import Android application context
import android.app.Application
// Import Compose state management
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
// Import Android ViewModel base class
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
// Import our API client
import com.example.objectdetection.network.ApiClient
// Import coroutines for async operations
import kotlinx.coroutines.launch

// State for authentication UI
// Define a data class to hold all authentication-related state
data class AuthState(
    // Whether the user is currently logged in
    val isLoggedIn: Boolean = false,
    // Whether a login/register operation is in progress
    val isLoading: Boolean = false,
    // Any error message to display
    val error: String? = null,
    // Any success message to display
    val message: String? = null
)

// ViewModel to manage authentication logic and state
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    // Create an instance of the API client
    private val apiClient = ApiClient(application)

    // Private mutable state that can only be changed within this ViewModel
    private val _authState = mutableStateOf(AuthState())
    // Public read-only state that the UI can observe
    val authState: State<AuthState> = _authState

    // This block runs when the ViewModel is created
    init {
        // Check if user is already logged in when ViewModel is created
        checkLoginStatus()
    }

    // Function to check if the user is already logged in
    private fun checkLoginStatus() {
        // Launch a coroutine in the viewModelScope
        viewModelScope.launch {
            // Check if the user has a saved token
            val isLoggedIn = apiClient.isLoggedIn()
            // Update the state with the login status
            _authState.value = _authState.value.copy(isLoggedIn = isLoggedIn)
        }
    }

    // Function to log in a user
    fun login(username: String, password: String) {
        // Launch a coroutine in the viewModelScope
        viewModelScope.launch {
            // Update state to show we're loading and clear any previous errors/messages
            _authState.value = _authState.value.copy(
                isLoading = true,
                error = null,
                message = null
            )

            // Call the API to attempt login
            val result = apiClient.login(username, password)

            // Update state based on the result
            _authState.value = if (result.isSuccess) {
                // If login succeeded, update state to show logged in
                _authState.value.copy(
                    isLoading = false,
                    isLoggedIn = true,
                    message = result.getOrNull()
                )
            } else {
                // If login failed, update state with the error message
                _authState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }

    // Function to register a new user
    fun register(username: String, password: String) {
        // Launch a coroutine in the viewModelScope
        viewModelScope.launch {
            // Update state to show we're loading and clear any previous errors/messages
            _authState.value = _authState.value.copy(
                isLoading = true,
                error = null,
                message = null
            )

            // Call the API to attempt registration
            val result = apiClient.register(username, password)

            // Update state based on the result
            _authState.value = if (result.isSuccess) {
                // If registration succeeded, update state with success message
                _authState.value.copy(
                    isLoading = false,
                    message = result.getOrNull()
                )
            } else {
                // If registration failed, update state with the error message
                _authState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }

    // Function to log out the current user
    fun logout() {
        // Launch a coroutine in the viewModelScope
        viewModelScope.launch {
            // Call the API to log out
            apiClient.logout()
            // Reset the state to logged out with no messages
            _authState.value = AuthState(isLoggedIn = false)
        }
    }

    // Function to clear any error or success messages
    fun clearMessages() {
        // Update state to remove error and message
        _authState.value = _authState.value.copy(error = null, message = null)
    }
}