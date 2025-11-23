package com.example.captainslog.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.captainslog.viewmodel.AuthUiState
import com.example.captainslog.viewmodel.AuthViewModel

/**
 * Login/Register screen composable
 * Displays login form and handles user authentication
 * 
 * @param viewModel AuthViewModel for managing authentication state
 * @param onLoginSuccess Callback when login is successful
 */
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    // Collect UI state from ViewModel
    val uiState by viewModel.uiState.collectAsState()
    
    // Local state for input fields
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegisterMode by remember { mutableStateOf(false) }
    
    // Navigate to main screen when login is successful
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.LoggedIn) {
            onLoginSuccess()
        }
    }
    
    // Main content layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App title
        Text(
            text = "Captain's Log",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // Username input field
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Password input field (hidden text)
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Login/Register button
        Button(
            onClick = {
                if (isRegisterMode) {
                    // Call register function
                    viewModel.register(username, password)
                } else {
                    // Call login function
                    viewModel.login(username, password)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            // Disable button during loading
            enabled = uiState !is AuthUiState.Loading
        ) {
            // Show loading indicator or button text
            if (uiState is AuthUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(if (isRegisterMode) "Register" else "Login")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Toggle between login and register mode
        TextButton(
            onClick = { isRegisterMode = !isRegisterMode }
        ) {
            Text(
                if (isRegisterMode) "Already have an account? Login"
                else "Don't have an account? Register"
            )
        }
        
        // Show error message if present
        if (uiState is AuthUiState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = (uiState as AuthUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
