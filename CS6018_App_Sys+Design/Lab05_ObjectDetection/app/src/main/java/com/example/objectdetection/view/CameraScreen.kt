package com.example.objectdetection.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.view.PreviewView
import com.example.objectdetection.model.DetectedObject
import com.example.objectdetection.model.DetectionModel
import com.example.objectdetection.viewmodel.CameraViewModel

@Composable
fun CameraScreen(viewModel: CameraViewModel) { // Main camera screen UI
    val cameraState by viewModel.cameraState // Get current camera state
    val lifecycleOwner = LocalLifecycleOwner.current // Get lifecycle owner

    LaunchedEffect(viewModel.cameraController) { // Run once when screen starts
        viewModel.bindToLifecycle(lifecycleOwner) // Connect camera to app lifecycle
    }

    Box(modifier = Modifier.fillMaxSize()) { // Stack UI elements on top of each other
        CameraPreview( // Camera feed (bottom layer)
            cameraController = viewModel.cameraController, // Camera controller
            modifier = Modifier.fillMaxSize() // Fill entire screen
        )

        ObjectDetectionOverlay( // Bounding boxes (middle layer)
            detectedObjects = cameraState.detectedObjects, // Objects to draw boxes around
            modifier = Modifier.fillMaxSize() // Fill entire screen
        )

        ObjectLabelsOverlay( // Text labels (top layer)
            detectedObjects = cameraState.detectedObjects, // Objects to show labels for
            modifier = Modifier.fillMaxSize() // Fill entire screen
        )

        ModelIndicator( // Current AI model indicator (top left)
            currentModel = cameraState.currentModel, // Which model is active
            modifier = Modifier
                .align(Alignment.TopStart) // Position in top left
                .padding(16.dp) // 16dp padding from edges
        )

        CameraControls( // Control buttons (bottom center)
            onSwitchCamera = { viewModel.switchCamera() }, // Switch front/back camera
            onTakePhoto = { viewModel.takePhoto() }, // Take photo
            onSwitchModel = { viewModel.switchDetectionModel() }, // Switch AI model
            isCapturing = cameraState.isCapturing, // Currently taking photo?
            modifier = Modifier
                .align(Alignment.BottomCenter) // Position at bottom center
                .padding(16.dp) // 16dp padding from edges
        )
    }
}

@Composable
private fun CameraPreview( // Shows live camera feed
    cameraController: androidx.camera.view.LifecycleCameraController,
    modifier: Modifier = Modifier
) {
    AndroidView( // Embed Android View in Compose
        factory = { context -> // Create the view
            PreviewView(context).apply { // Create camera preview view
                this.controller = cameraController // Connect to camera controller
            }
        },
        modifier = modifier // Apply modifiers
    )
}

@Composable
private fun ObjectDetectionOverlay( // Draws bounding boxes around detected objects
    detectedObjects: List<DetectedObject>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) { // Custom drawing canvas
        detectedObjects.forEach { detectedObject -> // For each detected object
            drawBoundingBox(detectedObject) // Draw rectangle around it
        }
    }
}

@Composable
private fun ObjectLabelsOverlay( // Shows text labels for detected objects
    detectedObjects: List<DetectedObject>,
    modifier: Modifier = Modifier
) {
    if (detectedObjects.isNotEmpty()) { // If objects detected
        Column( // Vertical layout
            modifier = modifier
                .fillMaxWidth() // Full width
                .padding(16.dp), // 16dp padding
            verticalArrangement = Arrangement.Top // Align to top
        ) {
            detectedObjects.take(3).forEach { detectedObject -> // Show max 3 objects
                if (detectedObject.labels.isNotEmpty()) { // If object has labels
                    val primaryLabel = detectedObject.labels.first() // Get first label
                    val labelText = "${primaryLabel.text} ${(primaryLabel.confidence * 100).toInt()}%" // Format text

                    Card( // Background card for text
                        modifier = Modifier
                            .padding(vertical = 2.dp) // Vertical spacing
                            .wrapContentWidth(), // Wrap to text width
                        colors = CardDefaults.cardColors( // Card colors
                            containerColor = getColorForObject(detectedObject).copy(alpha = 0.9f) // Semi-transparent
                        ),
                        shape = RoundedCornerShape(4.dp) // Rounded corners
                    ) {
                        Text( // Label text
                            text = labelText, // Display text
                            color = Color.White, // White text
                            fontSize = 12.sp, // Small font
                            fontWeight = FontWeight.Bold, // Bold text
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp) // Text padding
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawBoundingBox(detectedObject: DetectedObject) { // Draw rectangle around object
    val boundingBox = detectedObject.boundingBox // Get bounding box coordinates

    val left = boundingBox.left * size.width // Convert to pixel coordinates
    val top = boundingBox.top * size.height
    val right = boundingBox.right * size.width
    val bottom = boundingBox.bottom * size.height

    val boxColor = getColorForObject(detectedObject) // Choose color based on object type

    drawRect( // Draw rectangle outline
        color = boxColor, // Color of rectangle
        topLeft = Offset(left, top), // Top-left corner
        size = Size(right - left, bottom - top), // Width and height
        style = Stroke(width = 4f) // Outline only, 4px thick
    )
}

private fun getColorForObject(detectedObject: DetectedObject): Color { // Choose color based on object type
    return when { // Check object labels to pick color
        detectedObject.labels.any { it.text.contains("person", ignoreCase = true) } -> Color.Green // Person = Green
        detectedObject.labels.any { it.text.contains("car", ignoreCase = true) } -> Color.Blue // Car = Blue
        detectedObject.labels.any { it.text.contains("dog", ignoreCase = true) } -> Color.Yellow // Dog = Yellow
        detectedObject.labels.any { it.text.contains("cat", ignoreCase = true) } -> Color.Magenta // Cat = Magenta
        detectedObject.labels.any { it.text.contains("food", ignoreCase = true) } -> Color.Cyan // Food = Cyan
        else -> Color.Red // Everything else = Red
    }
}

@Composable
private fun ModelIndicator( // Shows which AI model is currently active
    currentModel: DetectionModel,
    modifier: Modifier = Modifier
) {
    Card( // Background card
        modifier = modifier,
        colors = CardDefaults.cardColors( // Card styling
            containerColor = Color.Black.copy(alpha = 0.7f) // Semi-transparent black
        ),
        shape = RoundedCornerShape(8.dp) // Rounded corners
    ) {
        Text( // Model name text
            text = when (currentModel) { // Show model name
                DetectionModel.MLKIT -> "MLKit" // Google MLKit
                DetectionModel.EFFICIENTDET -> "EfficientDet" // TensorFlow EfficientDet
            },
            color = Color.White, // White text
            fontSize = 14.sp, // Medium font size
            fontWeight = FontWeight.Bold, // Bold text
            modifier = Modifier.padding(8.dp) // Text padding
        )
    }
}

@Composable
private fun CameraControls( // Camera control buttons
    onSwitchCamera: () -> Unit, // Callback to switch camera
    onTakePhoto: () -> Unit, // Callback to take photo
    onSwitchModel: () -> Unit, // Callback to switch AI model
    isCapturing: Boolean, // Currently taking photo?
    modifier: Modifier = Modifier
) {
    Row( // Horizontal layout
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp), // 16dp spacing between buttons
        verticalAlignment = Alignment.CenterVertically // Center buttons vertically
    ) {
        FloatingActionButton( // Switch camera button
            onClick = onSwitchCamera, // Switch front/back camera
            modifier = Modifier
                .size(56.dp) // Medium size
                .clip(CircleShape), // Circular shape
            containerColor = MaterialTheme.colorScheme.secondary // Secondary theme color
        ) {
            Icon( // Refresh icon
                imageVector = Icons.Default.Refresh,
                contentDescription = "Switch Camera", // Accessibility description
                tint = Color.White // White icon
            )
        }

        FloatingActionButton( // Capture photo button (center, largest)
            onClick = onTakePhoto, // Take photo
            modifier = Modifier
                .size(72.dp) // Large size
                .clip(CircleShape), // Circular shape
            containerColor = MaterialTheme.colorScheme.primary // Primary theme color
        ) {
            if (isCapturing) { // If currently taking photo
                CircularProgressIndicator( // Show spinning progress
                    modifier = Modifier.size(32.dp), // Medium size
                    color = Color.White, // White progress indicator
                    strokeWidth = 3.dp // Thin stroke
                )
            } else { // If not taking photo
                Icon( // Camera icon
                    imageVector = Icons.Default.Add, // Plus icon as camera symbol
                    contentDescription = "Take Photo", // Accessibility description
                    tint = Color.White, // White icon
                    modifier = Modifier.size(32.dp) // Large icon
                )
            }
        }

        FloatingActionButton( // Switch AI model button
            onClick = onSwitchModel, // Switch between MLKit and EfficientDet
            modifier = Modifier
                .size(56.dp) // Medium size
                .clip(CircleShape), // Circular shape
            containerColor = MaterialTheme.colorScheme.tertiary // Tertiary theme color
        ) {
            Icon( // Settings icon
                imageVector = Icons.Default.Settings,
                contentDescription = "Switch AI Model", // Accessibility description
                tint = Color.White // White icon
            )
        }
    }
}