package com.example.camera.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.view.PreviewView
import com.example.camera.viewmodel.CameraViewModel

// Main screen that shows the camera and all UI elements
@Composable
fun CameraScreen(viewModel: CameraViewModel) {
    // Get current camera state from ViewModel
    val cameraState by viewModel.cameraState
    // Get lifecycle owner to manage camera lifecycle
    val lifecycleOwner = LocalLifecycleOwner.current

    // Bind camera to lifecycle when this composable is first created
    LaunchedEffect(viewModel.cameraController) {
        viewModel.bindToLifecycle(lifecycleOwner)
    }

    // Box layout that stacks elements on top of each other
    Box(modifier = Modifier.fillMaxSize()) {
        // Camera preview (bottom layer)
        CameraPreview(
            cameraController = viewModel.cameraController,
            modifier = Modifier.fillMaxSize()
        )

        // Overlay for point of interest (middle layer)
        cameraState.brightestPoint?.let { point ->
            PointOfInterestOverlay(
                point = point,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Control buttons (top layer)
        CameraControls(
            onSwitchCamera = { viewModel.switchCamera() }, // What to do when switch button pressed
            onTakePhoto = { viewModel.takePhoto() }, // What to do when capture button pressed
            isCapturing = cameraState.isCapturing, // Whether we're currently taking a photo
            modifier = Modifier
                .align(Alignment.BottomCenter) // Put buttons at bottom center
                .padding(16.dp) // Add some space from screen edge
        )
    }
}

// Composable that shows the live camera feed
@Composable
private fun CameraPreview(
    cameraController: androidx.camera.view.LifecycleCameraController,
    modifier: Modifier = Modifier
) {
    // AndroidView lets us use regular Android views in Compose
    AndroidView(
        factory = { context ->
            // Create a PreviewView (regular Android view for camera)
            PreviewView(context).apply {
                // Connect it to our camera controller
                this.controller = cameraController
            }
        },
        modifier = modifier
    )
}

// Composable that draws the red highlight over the brightest point
@Composable
private fun PointOfInterestOverlay(
    point: Offset, // Position of the bright point (0.0 to 1.0)
    modifier: Modifier = Modifier
) {
    // Canvas lets us draw custom graphics
    Canvas(modifier = modifier) {
        // Convert normalized coordinates to actual pixel coordinates
        val centerX = size.width * point.x
        val centerY = size.height * point.y

        // Draw highlight circle around the bright point
        drawCircle(
            color = Color.Red, // Red color
            radius = 30f, // Circle size
            center = Offset(centerX, centerY), // Center position
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f) // Outline only, not filled
        )

        // Draw crosshair lines through the center
        // Horizontal line
        drawLine(
            color = Color.Red,
            start = Offset(centerX - 20, centerY), // Left side
            end = Offset(centerX + 20, centerY), // Right side
            strokeWidth = 3f
        )
        // Vertical line
        drawLine(
            color = Color.Red,
            start = Offset(centerX, centerY - 20), // Top side
            end = Offset(centerX, centerY + 20), // Bottom side
            strokeWidth = 3f
        )
    }
}

// Composable that shows the camera control buttons
@Composable
private fun CameraControls(
    onSwitchCamera: () -> Unit, // Function to call when switch camera button pressed
    onTakePhoto: () -> Unit, // Function to call when take photo button pressed
    isCapturing: Boolean, // Whether we're currently taking a photo
    modifier: Modifier = Modifier
) {
    // Column arranges buttons vertically
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally, // Center buttons horizontally
        verticalArrangement = Arrangement.spacedBy(16.dp) // Put 16dp space between buttons
    ) {
        // Camera switch button
        FloatingActionButton(
            onClick = onSwitchCamera, // What happens when pressed
            modifier = Modifier.clip(CircleShape) // Make it perfectly round
        ) {
            Icon(
                imageVector = Icons.Default.Refresh, // Refresh icon (for switching)
                contentDescription = "Switch Camera" // For accessibility
            )
        }

        // Capture photo button
        FloatingActionButton(
            onClick = onTakePhoto, // What happens when pressed
            modifier = Modifier.clip(CircleShape) // Make it perfectly round
        ) {
            // Show different content based on whether we're capturing
            if (isCapturing) {
                // Show spinning progress indicator while taking photo
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp), // Size of the spinner
                    color = MaterialTheme.colorScheme.onPrimary // Color that shows up on button
                )
            } else {
                // Show camera icon when not taking photo
                Icon(
                    imageVector = Icons.Default.Add, // Plus icon (for capture)
                    contentDescription = "Take Photo" // For accessibility
                )
            }
        }
    }
}