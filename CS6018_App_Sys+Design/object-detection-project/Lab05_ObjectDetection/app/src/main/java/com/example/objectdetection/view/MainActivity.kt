package com.example.objectdetection.view

// Import Android permission handling
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
// Import activity components
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
// Import Compose UI components
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
// Import permission checking utility
import androidx.core.content.ContextCompat
// Import our ViewModels
import com.example.objectdetection.viewmodel.AuthViewModel
import com.example.objectdetection.viewmodel.CameraViewModel
import com.example.objectdetection.viewmodel.GalleryViewModel

// Main activity that serves as the entry point of the app
class MainActivity : ComponentActivity() {

    // Create ViewModels using delegation (lazy initialization)
    // ViewModel for camera functionality
    private val cameraViewModel: CameraViewModel by viewModels()
    // ViewModel for authentication
    private val authViewModel: AuthViewModel by viewModels() // NEW: Auth ViewModel
    // ViewModel for photo gallery
    private val galleryViewModel: GalleryViewModel by viewModels() // NEW: Gallery ViewModel

    // Register a permission request launcher
    private val permissionLauncher = registerForActivityResult(
        // Use the built-in multiple permissions contract
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Track if all permissions were granted
        var allGranted = true
        // Loop through each permission result
        permissions.entries.forEach { permission ->
            // Check if this is a required permission that wasn't granted
            if (permission.key in REQUIRED_PERMISSIONS && !permission.value) {
                allGranted = false
            }
        }
        // If not all permissions were granted, show a message
        if (!allGranted) {
            Toast.makeText(this, "Camera permission required", Toast.LENGTH_LONG).show()
        } else {
            // If all permissions granted, recreate the activity to apply changes
            recreate()
        }
    }

    // Called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        // Call the parent class's onCreate
        super.onCreate(savedInstanceState)

        // Check if we have the required permissions
        if (!hasPermissions()) {
            // If not, request them
            requestPermissions()
        }

        // Set the content of the activity using Compose
        setContent {
            // Apply Material Theme
            MaterialTheme {
                // Surface provides a background
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Check if we have permissions
                    if (hasPermissions()) {
                        // If yes, show the main app navigation
                        AppNavigation() // NEW: Navigation system
                    } else {
                        // If no, show the permission request screen
                        PermissionScreen { requestPermissions() }
                    }
                }
            }
        }
    }

    // NEW: Navigation between screens
    // Composable function to manage navigation between different screens
    @Composable
    private fun AppNavigation() {
        // Get the current authentication state
        val authState by authViewModel.authState
        // Remember which screen we're currently on
        var currentScreen by remember { mutableStateOf("login") }

        // Navigate based on login state
        // Side effect that runs when login status changes
        LaunchedEffect(authState.isLoggedIn) {
            // If user is logged in, go to camera screen
            if (authState.isLoggedIn) {
                currentScreen = "camera"
            } else {
                // If user is not logged in, go to login screen
                currentScreen = "login"
            }
        }

        // Display the appropriate screen based on currentScreen value
        when (currentScreen) {
            // Login screen
            "login" -> {
                LoginScreen(
                    // Pass the auth ViewModel
                    viewModel = authViewModel,
                    // When login succeeds, navigate to camera
                    onLoginSuccess = { currentScreen = "camera" }
                )
            }
            // Camera screen
            "camera" -> {
                CameraScreen(
                    // Pass the camera ViewModel
                    viewModel = cameraViewModel,
                    // Pass the auth ViewModel for logout
                    authViewModel = authViewModel,
                    // When user wants to see gallery, navigate to it
                    onNavigateToGallery = { currentScreen = "gallery" },
                    // When user logs out, navigate to login
                    onLogout = { currentScreen = "login" }
                )
            }
            // Gallery screen
            "gallery" -> {
                GalleryScreen(
                    // Pass the gallery ViewModel
                    viewModel = galleryViewModel,
                    // When user wants to go back, navigate to camera
                    onBackToCamera = { currentScreen = "camera" }
                )
            }
        }
    }

    // Function to check if we have all required permissions
    private fun hasPermissions(): Boolean {
        // Check each required permission
        return REQUIRED_PERMISSIONS.all { permission ->
            // Return true only if all permissions are granted
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    // Function to request permissions from the user
    private fun requestPermissions() {
        // Launch the permission request
        permissionLauncher.launch(REQUIRED_PERMISSIONS)
    }

    // Companion object for static members
    companion object {
        // Array of permissions this app needs
        private val REQUIRED_PERMISSIONS = mutableListOf<String>().apply {
            // Always need camera permission
            add(Manifest.permission.CAMERA)
            // On older Android versions, also need write storage permission
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray() // Convert list to array
    }
}

// Composable function for the permission request screen
@Composable
fun PermissionScreen(onRequestPermissions: () -> Unit) {
    // Container that fills the screen
    Box(
        modifier = Modifier.fillMaxSize(),
        // Center the content
        contentAlignment = Alignment.Center
    ) {
        // Column to stack UI elements vertically
        Column(
            // Center items horizontally
            horizontalAlignment = Alignment.CenterHorizontally,
            // Add spacing between items
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Settings icon
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Camera Permission",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            // Title text
            Text(
                text = "Camera Permission Required",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            // Explanation text
            Text(
                text = "This app needs camera permission to detect objects.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            // Button to request permission
            Button(
                onClick = onRequestPermissions,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Grant Camera Permission")
            }
        }
    }
}