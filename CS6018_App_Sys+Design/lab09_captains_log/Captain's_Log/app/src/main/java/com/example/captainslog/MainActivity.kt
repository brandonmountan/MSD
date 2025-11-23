package com.example.captainslog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.captainslog.network.ApiClient
import com.example.captainslog.view.FriendsScreen
import com.example.captainslog.view.LogScreen
import com.example.captainslog.view.LoginScreen
import com.example.captainslog.view.RecordScreen
import com.example.captainslog.viewmodel.AuthViewModel
import com.example.captainslog.viewmodel.FriendsViewModel
import com.example.captainslog.viewmodel.LogsViewModel
import com.example.captainslog.viewmodel.RecordViewModel

/**
 * Main Activity for the Captain's Log app
 * Manages navigation between different screens
 */
class MainActivity : ComponentActivity() {
    // Initialize API client (singleton for the app)
    private lateinit var apiClient: ApiClient

    // ViewModels for different screens
    private lateinit var authViewModel: AuthViewModel
    private lateinit var logsViewModel: LogsViewModel
    private lateinit var recordViewModel: RecordViewModel
    private lateinit var friendsViewModel: FriendsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize API client with application context
        apiClient = ApiClient(applicationContext)

        // Initialize ViewModels with API client
        authViewModel = AuthViewModel(apiClient)
        logsViewModel = LogsViewModel(apiClient)
        recordViewModel = RecordViewModel(apiClient)
        friendsViewModel = FriendsViewModel(apiClient)

        // Set up Compose UI
        setContent {
            CaptainsLogTheme {
                // Surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Main navigation composable
                    CaptainsLogApp(
                        authViewModel = authViewModel,
                        logsViewModel = logsViewModel,
                        recordViewModel = recordViewModel,
                        friendsViewModel = friendsViewModel
                    )
                }
            }
        }

        // Check if user is already logged in on app start
        authViewModel.checkLoginStatus()
    }
}

/**
 * Main app composable that handles navigation between screens
 * Simple navigation without Navigation component for prototype simplicity
 */
@Composable
fun CaptainsLogApp(
    authViewModel: AuthViewModel,
    logsViewModel: LogsViewModel,
    recordViewModel: RecordViewModel,
    friendsViewModel: FriendsViewModel
) {
    // Navigation state - tracks which screen to show
    var currentScreen by remember { mutableStateOf(Screen.Login) }

    // Render the appropriate screen based on navigation state
    when (currentScreen) {
        Screen.Login -> {
            // Show login/register screen
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    // Navigate to log list after successful login
                    currentScreen = Screen.LogList
                }
            )
        }

        Screen.LogList -> {
            // Show main log list screen (partner's design)
            LogScreen(
                viewModel = logsViewModel,
                Record = {
                    // Navigate to recording screen
                    currentScreen = Screen.Recording
                },
                Friends = {
                    // Navigate to friends screen
                    currentScreen = Screen.Friends
                },
                Logout = {
                    // Logout and navigate back to login
                    authViewModel.logout()
                    currentScreen = Screen.Login
                }
            )
        }

        Screen.Recording -> {
            // Show recording screen (partner's design)
            RecordScreen(
                viewModel = recordViewModel,
                onNavigateBack = {
                    // Navigate back to log list
                    recordViewModel.resetState()
                    logsViewModel.loadLogs()
                    currentScreen = Screen.LogList
                }
            )
        }

        Screen.Friends -> {
            // Show friends screen (partner's design)
            FriendsScreen(
                viewModel = friendsViewModel,
                onNavigateToLogs = {
                    // Navigate back to log list
                    currentScreen = Screen.LogList
                }
            )
        }
    }
}

/**
 * Enum representing different screens in the app
 * Used for simple navigation without Navigation component
 */
enum class Screen {
    Login,      // Login/Register screen
    LogList,    // Main log list screen
    Recording,  // Record new log screen
    Friends     // Friends management screen
}

/**
 * Material3 theme composable for the app
 * Uses default Material3 theme for simplicity in prototype
 */
@Composable
fun CaptainsLogTheme(content: @Composable () -> Unit) {
    // Use Material3 dynamic color scheme
    MaterialTheme(
        content = content
    )
}