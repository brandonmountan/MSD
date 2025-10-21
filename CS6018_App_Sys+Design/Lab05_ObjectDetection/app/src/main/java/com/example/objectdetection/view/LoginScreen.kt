package com.example.objectdetection.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.objectdetection.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit // Navigate to camera when logged in
) {
    val authState by viewModel.authState

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegisterMode by remember { mutableStateOf(false) } // Toggle between login/register

    // Show success message and navigate
    LaunchedEffect(authState.isLoggedIn) {
        if (authState.isLoggedIn) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title
                Text(
                    text = if (isRegisterMode) "Create Account" else "Login",
                    style = MaterialTheme.typography.headlineMedium
                )

                // Username field
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !authState.isLoading
                )

                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !authState.isLoading
                )

                // Error message
                if (authState.error != null) {
                    Text(
                        text = authState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Success message
                if (authState.message != null) {
                    Text(
                        text = authState.message!!,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Login/Register button
                Button(
                    onClick = {
                        if (isRegisterMode) {
                            viewModel.register(username, password)
                        } else {
                            viewModel.login(username, password)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !authState.isLoading && username.isNotBlank() && password.isNotBlank()
                ) {
                    if (authState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(if (isRegisterMode) "Register" else "Login")
                    }
                }

                // Toggle between login/register
                TextButton(
                    onClick = {
                        isRegisterMode = !isRegisterMode
                        viewModel.clearMessages() // Clear any messages when switching
                    },
                    enabled = !authState.isLoading
                ) {
                    Text(
                        if (isRegisterMode) "Already have an account? Login"
                        else "Don't have an account? Register"
                    )
                }
            }
        }
    }
}