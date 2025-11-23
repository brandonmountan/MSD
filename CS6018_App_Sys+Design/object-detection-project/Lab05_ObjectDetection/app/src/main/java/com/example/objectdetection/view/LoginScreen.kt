package com.example.objectdetection.view

// Import Compose UI components for building the interface
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
// Import our ViewModel
import com.example.objectdetection.viewmodel.AuthViewModel

// Main composable function for the login screen
@Composable
fun LoginScreen(
    // ViewModel that handles authentication logic
    viewModel: AuthViewModel,
    // Callback function to call when login is successful
    onLoginSuccess: () -> Unit // Navigate to camera when logged in
) {
    // Get the current authentication state from the ViewModel
    val authState by viewModel.authState

    // Remember the username entered by the user (persists across recompositions)
    var username by remember { mutableStateOf("") }
    // Remember the password entered by the user
    var password by remember { mutableStateOf("") }
    // Remember whether we're in register mode or login mode
    var isRegisterMode by remember { mutableStateOf(false) } // Toggle between login/register

    // Show success message and navigate
    // Side effect that runs when isLoggedIn changes
    LaunchedEffect(authState.isLoggedIn) {
        // If user is logged in, call the success callback
        if (authState.isLoggedIn) {
            onLoginSuccess()
        }
    }

    // Main container that fills the entire screen
    Box(
        modifier = Modifier.fillMaxSize(),
        // Center the content
        contentAlignment = Alignment.Center
    ) {
        // Card that contains the login/register form
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            // Add shadow/elevation to the card
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            // Column to stack UI elements vertically
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                // Center items horizontally
                horizontalAlignment = Alignment.CenterHorizontally,
                // Add spacing between items
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title
                // Display "Create Account" or "Login" based on mode
                Text(
                    text = if (isRegisterMode) "Create Account" else "Login",
                    style = MaterialTheme.typography.headlineMedium
                )

                // Username field
                // Text input for username
                OutlinedTextField(
                    // Bind to the username state
                    value = username,
                    // Update username state when user types
                    onValueChange = { username = it },
                    // Label that appears in the field
                    label = { Text("Username") },
                    // Only allow single line
                    singleLine = true,
                    // Fill the width of the parent
                    modifier = Modifier.fillMaxWidth(),
                    // Disable if loading
                    enabled = !authState.isLoading
                )

                // Password field
                // Text input for password
                OutlinedTextField(
                    // Bind to the password state
                    value = password,
                    // Update password state when user types
                    onValueChange = { password = it },
                    // Label that appears in the field
                    label = { Text("Password") },
                    // Only allow single line
                    singleLine = true,
                    // Hide the password characters
                    visualTransformation = PasswordVisualTransformation(),
                    // Set keyboard type to password
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    // Fill the width of the parent
                    modifier = Modifier.fillMaxWidth(),
                    // Disable if loading
                    enabled = !authState.isLoading
                )

                // Error message
                // Show error message if there is one
                if (authState.error != null) {
                    Text(
                        // Display the error message
                        text = authState.error!!,
                        // Use error color from theme
                        color = MaterialTheme.colorScheme.error,
                        // Use small text style
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Success message
                // Show success message if there is one
                if (authState.message != null) {
                    Text(
                        // Display the success message
                        text = authState.message!!,
                        // Use primary color from theme
                        color = MaterialTheme.colorScheme.primary,
                        // Use small text style
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Login/Register button
                // Main action button
                Button(
                    onClick = {
                        // Determine which action to perform based on mode
                        if (isRegisterMode) {
                            // Call register function on ViewModel
                            viewModel.register(username, password)
                        } else {
                            // Call login function on ViewModel
                            viewModel.login(username, password)
                        }
                    },
                    // Fill the width of the parent
                    modifier = Modifier.fillMaxWidth(),
                    // Enable only if not loading and both fields have text
                    enabled = !authState.isLoading && username.isNotBlank() && password.isNotBlank()
                ) {
                    // Show loading spinner or button text
                    if (authState.isLoading) {
                        // Show circular loading indicator
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            // Use contrasting color
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        // Show "Register" or "Login" text based on mode
                        Text(if (isRegisterMode) "Register" else "Login")
                    }
                }

                // Toggle between login/register
                // Text button to switch modes
                TextButton(
                    onClick = {
                        // Toggle the mode
                        isRegisterMode = !isRegisterMode
                        // Clear any messages when switching
                        viewModel.clearMessages() // Clear any messages when switching
                    },
                    // Disable if loading
                    enabled = !authState.isLoading
                ) {
                    // Show appropriate text based on current mode
                    Text(
                        if (isRegisterMode) "Already have an account? Login"
                        else "Don't have an account? Register"
                    )
                }
            }
        }
    }
}