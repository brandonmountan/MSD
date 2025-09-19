package com.example.camera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.camera.view.CameraScreen
import com.example.camera.viewmodel.CameraViewModel

// Main activity that starts when the app launches
class MainActivity : ComponentActivity() {

    // Create the ViewModel (survives screen rotations)
    private val viewModel: CameraViewModel by viewModels()

    // Handler for permission requests (camera and storage)
    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions() // Request multiple permissions at once
    ) { permissions ->
        // This runs after user responds to permission dialog
        var permissionGranted = true
        // Check if all required permissions were granted
        permissions.entries.forEach {
            if (it.key in REQUIRED_PERMISSIONS && !it.value) {
                permissionGranted = false // At least one permission was denied
            }
        }
        // If any permission was denied, show error message
        if (!permissionGranted) {
            Toast.makeText(this, "Permission request denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Called when activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if we have all required permissions
        if (!allPermissionsGranted()) {
            // If not, request them from the user
            requestPermissions()
        }

        // Set up the UI using Jetpack Compose
        setContent {
            // Apply app theme
            ARCameraAppTheme {
                // Surface provides background and theme colors
                Surface(
                    modifier = Modifier.fillMaxSize(), // Take up entire screen
                    color = MaterialTheme.colorScheme.background // Use theme background color
                ) {
                    // Show the main camera screen
                    CameraScreen(viewModel = viewModel)
                }
            }
        }
    }

    // Ask the user for permissions we need
    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    // Check if we have all the permissions we need
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        // Check each required permission
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    // Static variables that belong to the class (not instances)
    companion object {
        // List of permissions we need for the app to work
        private val REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA // Always need camera permission
        ).apply {
            // For older Android versions, we also need storage permission
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray() // Convert to array format
    }
}

// Simple theme wrapper for our app
@Composable
fun ARCameraAppTheme(
    content: @Composable () -> Unit // Content to wrap with theme
) {
    // Use Material Design 3 theme
    MaterialTheme {
        content() // Show the content with theme applied
    }
}