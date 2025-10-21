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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.objectdetection.viewmodel.AuthViewModel
import com.example.objectdetection.viewmodel.CameraViewModel
import com.example.objectdetection.viewmodel.GalleryViewModel

class MainActivity : ComponentActivity() {

    private val cameraViewModel: CameraViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels() // NEW: Auth ViewModel
    private val galleryViewModel: GalleryViewModel by viewModels() // NEW: Gallery ViewModel

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        var allGranted = true
        permissions.entries.forEach { permission ->
            if (permission.key in REQUIRED_PERMISSIONS && !permission.value) {
                allGranted = false
            }
        }
        if (!allGranted) {
            Toast.makeText(this, "Camera permission required", Toast.LENGTH_LONG).show()
        } else {
            recreate()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!hasPermissions()) {
            requestPermissions()
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (hasPermissions()) {
                        AppNavigation() // NEW: Navigation system
                    } else {
                        PermissionScreen { requestPermissions() }
                    }
                }
            }
        }
    }

    // NEW: Navigation between screens
    @Composable
    private fun AppNavigation() {
        val authState by authViewModel.authState
        var currentScreen by remember { mutableStateOf("login") }

        // Navigate based on login state
        LaunchedEffect(authState.isLoggedIn) {
            if (authState.isLoggedIn) {
                currentScreen = "camera"
            } else {
                currentScreen = "login"
            }
        }

        when (currentScreen) {
            "login" -> {
                LoginScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = { currentScreen = "camera" }
                )
            }
            "camera" -> {
                CameraScreen(
                    viewModel = cameraViewModel,
                    authViewModel = authViewModel,
                    onNavigateToGallery = { currentScreen = "gallery" },
                    onLogout = { currentScreen = "login" }
                )
            }
            "gallery" -> {
                GalleryScreen(
                    viewModel = galleryViewModel,
                    onBackToCamera = { currentScreen = "camera" }
                )
            }
        }
    }

    private fun hasPermissions(): Boolean {
        return REQUIRED_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        permissionLauncher.launch(REQUIRED_PERMISSIONS)
    }

    companion object {
        private val REQUIRED_PERMISSIONS = mutableListOf<String>().apply {
            add(Manifest.permission.CAMERA)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }
}

@Composable
fun PermissionScreen(onRequestPermissions: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Camera Permission",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Camera Permission Required",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Text(
                text = "This app needs camera permission to detect objects.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Button(
                onClick = onRequestPermissions,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Grant Camera Permission")
            }
        }
    }
}