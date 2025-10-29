package com.example.objectdetection.view

// Import Compose UI components for drawing and layout
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
// Import Material icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
// Import CameraX preview view
import androidx.camera.view.PreviewView
// Import our model classes
import com.example.objectdetection.model.DetectedObject
import com.example.objectdetection.model.DetectionModel
// Import our ViewModels
import com.example.objectdetection.viewmodel.AuthViewModel
import com.example.objectdetection.viewmodel.CameraViewModel

// Main composable function for the camera screen
@Composable
fun CameraScreen(
    // ViewModel that handles camera logic
    viewModel: CameraViewModel,
    // ViewModel for authentication (for logout)
    authViewModel: AuthViewModel,
    // Callback to navigate to gallery
    onNavigateToGallery: () -> Unit,
    // Callback to handle logout
    onLogout: () -> Unit
) {
    // Get the current camera state from the ViewModel
    val cameraState by viewModel.cameraState
    // Get the lifecycle owner (needed to bind camera)
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    // Bind camera to lifecycle when the composable is first shown
    LaunchedEffect(viewModel.cameraController) {
        viewModel.bindToLifecycle(lifecycleOwner)
    }

    // Main container that fills the screen
    Box(modifier = Modifier.fillMaxSize()) {

        // Layer 1: Camera preview
        // Show the live camera feed
        CameraPreview(
            cameraController = viewModel.cameraController,
            modifier = Modifier.fillMaxSize()
        )

        // Layer 2: Bounding boxes
        // Draw rectangles around detected objects
        ObjectDetectionOverlay(
            detectedObjects = cameraState.detectedObjects,
            modifier = Modifier.fillMaxSize()
        )

        // Layer 3: Text labels
        // Show labels for detected objects
        ObjectLabelsOverlay(
            detectedObjects = cameraState.detectedObjects,
            modifier = Modifier.fillMaxSize()
        )

        // Layer 4: Model indicator
        // Show which AI model is currently being used
        ModelIndicatorWithDetections(
            currentModel = cameraState.currentModel,
            detectedObjects = cameraState.detectedObjects,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        )

        // NEW: Layer 5: Top-right buttons (Gallery and Logout)
        // Show gallery and logout buttons
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            // Add spacing between buttons
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Gallery button
            // Button to navigate to photo gallery
            FloatingActionButton(
                onClick = onNavigateToGallery,
                modifier = Modifier.size(48.dp),
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "View Gallery",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            // Logout button
            // Button to log out of the app
            FloatingActionButton(
                onClick = {
                    // Call logout on the ViewModel
                    authViewModel.logout()
                    // Navigate to login screen
                    onLogout()
                },
                modifier = Modifier.size(48.dp),
                containerColor = MaterialTheme.colorScheme.errorContainer
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Logout",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        // Layer 6: Camera controls at bottom
        // Show buttons for camera operations
        CameraControls(
            // Function to switch between front/back camera
            onSwitchCamera = { viewModel.switchCamera() },
            // Function to take a photo
            onTakePhoto = { viewModel.takePhoto() },
            // Function to switch AI models
            onSwitchModel = { viewModel.switchDetectionModel() },
            // Whether we're currently capturing a photo
            isCapturing = cameraState.isCapturing,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

// Composable function to display the camera preview
@Composable
private fun CameraPreview(
    // Camera controller that manages the camera
    cameraController: androidx.camera.view.LifecycleCameraController,
    modifier: Modifier = Modifier
) {
    // AndroidView allows us to use Android Views inside Compose
    AndroidView(
        // Factory function to create the preview view
        factory = { context ->
            // Create a new PreviewView
            PreviewView(context).apply {
                // Set the camera controller
                this.controller = cameraController
            }
        },
        modifier = modifier
    )
}

// Composable function to draw bounding boxes around detected objects
@Composable
private fun ObjectDetectionOverlay(
    // List of objects that were detected
    detectedObjects: List<DetectedObject>,
    modifier: Modifier = Modifier
) {
    // Canvas allows us to draw custom graphics
    Canvas(modifier = modifier) {
        // Draw a bounding box for each detected object
        detectedObjects.forEach { detectedObject ->
            drawBoundingBox(detectedObject)
        }
    }
}

// Composable function to show text labels for detected objects
@Composable
private fun ObjectLabelsOverlay(
    // List of objects that were detected
    detectedObjects: List<DetectedObject>,
    modifier: Modifier = Modifier
) {
    // Only show labels if we have detected objects
    if (detectedObjects.isNotEmpty()) {
        // Column to stack labels vertically
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            // Align to top
            verticalArrangement = Arrangement.Top
        ) {
            // Show labels for the first 3 detected objects
            detectedObjects.take(3).forEach { detectedObject ->
                // Check if this object has labels
                if (detectedObject.labels.isNotEmpty()) {
                    // Get the primary label (first one)
                    val primaryLabel = detectedObject.labels.first()
                    // Create text showing label name and confidence percentage
                    val labelText = "${primaryLabel.text} ${(primaryLabel.confidence * 100).toInt()}%"

                    // Card to display the label
                    Card(
                        modifier = Modifier
                            .padding(vertical = 2.dp)
                            .wrapContentWidth(),
                        // Use color based on what object was detected
                        colors = CardDefaults.cardColors(
                            containerColor = getColorForObject(detectedObject).copy(alpha = 0.9f)
                        ),
                        // Rounded corners
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        // Display the label text
                        Text(
                            text = labelText,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

// Function to draw a bounding box around a detected object
private fun DrawScope.drawBoundingBox(detectedObject: DetectedObject) {
    // Get the bounding box coordinates (normalized 0-1)
    val boundingBox = detectedObject.boundingBox

    // Convert normalized coordinates to pixel coordinates
    val left = boundingBox.left * size.width
    val top = boundingBox.top * size.height
    val right = boundingBox.right * size.width
    val bottom = boundingBox.bottom * size.height

    // Get the color for this type of object
    val boxColor = getColorForObject(detectedObject)

    // Draw the rectangle
    drawRect(
        color = boxColor,
        topLeft = Offset(left, top),
        size = Size(right - left, bottom - top),
        // Use stroke style (outline only, not filled)
        style = Stroke(width = 4f)
    )
}

// Function to determine what color to use for an object based on its label
private fun getColorForObject(detectedObject: DetectedObject): Color {
    // Check what the object is and return appropriate color
    return when {
        // Person is green
        detectedObject.labels.any { it.text.contains("person", ignoreCase = true) } -> Color.Green
        // Car is blue
        detectedObject.labels.any { it.text.contains("car", ignoreCase = true) } -> Color.Blue
        // Dog is yellow
        detectedObject.labels.any { it.text.contains("dog", ignoreCase = true) } -> Color.Yellow
        // Cat is magenta
        detectedObject.labels.any { it.text.contains("cat", ignoreCase = true) } -> Color.Magenta
        // Food is cyan
        detectedObject.labels.any { it.text.contains("food", ignoreCase = true) } -> Color.Cyan
        // Everything else is red
        else -> Color.Red
    }
}

// Composable function to show which AI model is being used and what it's detecting
@Composable
private fun ModelIndicatorWithDetections(
    // Which AI model is currently active
    currentModel: DetectionModel,
    // List of detected objects
    detectedObjects: List<DetectedObject>,
    modifier: Modifier = Modifier
) {
    // Column to stack the model name and detection list
    Column(modifier = modifier) {
        // Card showing the model name
        Card(
            colors = CardDefaults.cardColors(
                // Semi-transparent black background
                containerColor = Color.Black.copy(alpha = 0.7f)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            // Display model name
            Text(
                text = when (currentModel) {
                    DetectionModel.MLKIT -> "MLKit"
                    DetectionModel.EFFICIENTDET -> "EfficientDet"
                },
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )
        }

        // Spacing between model name and detection list
        Spacer(modifier = Modifier.height(8.dp))

        // Show detection list if there are detected objects
        if (detectedObjects.isNotEmpty()) {
            // Card showing the list of detected objects
            Card(
                colors = CardDefaults.cardColors(
                    // Semi-transparent black background
                    containerColor = Color.Black.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .widthIn(max = 200.dp)
                ) {
                    // Header text
                    Text(
                        text = "Detecting:",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    // Show first 5 detected objects
                    detectedObjects.take(5).forEach { obj ->
                        // Check if object has labels
                        if (obj.labels.isNotEmpty()) {
                            // Get the primary label
                            val label = obj.labels.first()
                            // Row to show label name and confidence
                            Row(
                                modifier = Modifier.padding(vertical = 2.dp),
                                // Space between label and percentage
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Label name with bullet point
                                Text(
                                    text = "• ${label.text}",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                // Confidence percentage
                                Text(
                                    text = "${(label.confidence * 100).toInt()}%",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }

                    // If there are more than 5 objects, show count of additional ones
                    if (detectedObjects.size > 5) {
                        Text(
                            text = "+${detectedObjects.size - 5} more",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 10.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            // Show message when nothing is detected
            Card(
                colors = CardDefaults.cardColors(
                    // Semi-transparent black background
                    containerColor = Color.Black.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "No objects detected",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

// Composable function for the camera control buttons at the bottom
@Composable
private fun CameraControls(
    // Function to call when switch camera button is pressed
    onSwitchCamera: () -> Unit,
    // Function to call when take photo button is pressed
    onTakePhoto: () -> Unit,
    // Function to call when switch model button is pressed
    onSwitchModel: () -> Unit,
    // Whether we're currently capturing a photo
    isCapturing: Boolean,
    modifier: Modifier = Modifier
) {
    // Row to arrange buttons horizontally
    Row(
        modifier = modifier,
        // Add spacing between buttons
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        // Center buttons vertically
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Switch camera button (front/back)
        FloatingActionButton(
            onClick = onSwitchCamera,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape),
            containerColor = MaterialTheme.colorScheme.secondary
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Switch Camera",
                tint = Color.White
            )
        }

        // Take photo button (largest button in the center)
        FloatingActionButton(
            onClick = onTakePhoto,
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            // Show loading spinner if capturing, otherwise show camera icon
            if (isCapturing) {
                // Show circular loading indicator
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = Color.White,
                    strokeWidth = 3.dp
                )
            } else {
                // Show camera icon
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Take Photo",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // Switch AI model button
        FloatingActionButton(
            onClick = onSwitchModel,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape),
            containerColor = MaterialTheme.colorScheme.tertiary
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Switch AI Model",
                tint = Color.White
            )
        }
    }
}