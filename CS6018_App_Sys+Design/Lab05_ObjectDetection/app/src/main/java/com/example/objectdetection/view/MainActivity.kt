package com.example.objectdetection.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.objectdetection.viewmodel.CameraViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: CameraViewModel by viewModels() // Create ViewModel instance

    // Handle permission request results
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions() // Request multiple permissions
    ) { permissions ->
        var allGranted = true // Track if all permissions granted
        permissions.entries.forEach { permission -> // Check each permission
            if (permission.key in REQUIRED_PERMISSIONS && !permission.value) { // If required permission denied
                allGranted = false // Mark as not all granted
            }
        }
        if (!allGranted) { // If any permission denied
            Toast.makeText(this, "Camera permission required", Toast.LENGTH_LONG).show() // Show error
        } else {
            recreate() // Restart activity with permissions
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Call parent method

        if (!hasPermissions()) { // If missing permissions
            requestPermissions() // Ask for permissions
        }

        setContent { // Set up Compose UI
            MaterialTheme { // Apply theme
                Surface( // Background surface
                    modifier = Modifier.fillMaxSize(), // Fill entire screen
                    color = MaterialTheme.colorScheme.background // Use theme background
                ) {
                    if (hasPermissions()) { // If we have permissions
                        CameraScreen(viewModel = viewModel) // Show camera screen
                    } else {
                        PermissionScreen { requestPermissions() } // Show permission request
                    }
                }
            }
        }
    }

    private fun hasPermissions(): Boolean { // Check if all permissions granted
        return REQUIRED_PERMISSIONS.all { permission -> // Check each required permission
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED // Permission granted?
        }
    }

    private fun requestPermissions() { // Request all needed permissions
        permissionLauncher.launch(REQUIRED_PERMISSIONS) // Launch permission request
    }

    companion object {
        private val REQUIRED_PERMISSIONS = mutableListOf<String>().apply { // Create permission list
            add(Manifest.permission.CAMERA) // Always need camera permission
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) { // For older Android versions
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE) // Need storage permission
            }
        }.toTypedArray() // Convert to array
    }
}

@Composable
fun PermissionScreen(onRequestPermissions: () -> Unit) { // Permission request UI
    Box( // Center content
        modifier = Modifier.fillMaxSize(), // Fill screen
        contentAlignment = Alignment.Center // Center alignment
    ) {
        Column( // Vertical layout
            horizontalAlignment = Alignment.CenterHorizontally, // Center horizontally
            verticalArrangement = Arrangement.spacedBy(16.dp) // 16dp spacing
        ) {
            Icon( // Settings icon
                imageVector = Icons.Default.Settings, // Use settings icon
                contentDescription = "Camera Permission", // Accessibility description
                modifier = Modifier.size(64.dp), // 64dp size
                tint = MaterialTheme.colorScheme.primary // Primary color
            )

            Text( // Title text
                text = "Camera Permission Required", // Title
                style = MaterialTheme.typography.headlineMedium, // Large text style
                textAlign = TextAlign.Center // Center text
            )

            Text( // Description text
                text = "This app needs camera permission to detect objects.", // Description
                style = MaterialTheme.typography.bodyMedium, // Normal text style
                textAlign = TextAlign.Center, // Center text
                modifier = Modifier.padding(horizontal = 32.dp) // Side padding
            )

            Button( // Request permission button
                onClick = onRequestPermissions, // Call when clicked
                modifier = Modifier.padding(top = 16.dp) // Top padding
            ) {
                Text("Grant Camera Permission") // Button text
            }
        }
    }
}