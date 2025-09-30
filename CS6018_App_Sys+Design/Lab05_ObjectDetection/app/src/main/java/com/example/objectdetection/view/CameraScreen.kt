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
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.objectdetection.model.DetectedObject
import com.example.objectdetection.model.DetectionModel
import com.example.objectdetection.viewmodel.CameraViewModel

@Composable // This is a composable function that builds UI
fun CameraScreen(viewModel: CameraViewModel) { // Main screen showing camera and detections
    val cameraState by viewModel.cameraState // Get current camera state from ViewModel
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current // Get lifecycle owner for camera

    LaunchedEffect(viewModel.cameraController) { // Run this once when screen starts
        viewModel.bindToLifecycle(lifecycleOwner) // Connect camera to app lifecycle
    }

    Box(modifier = Modifier.fillMaxSize()) { // Container that stacks everything on top of each other

        // Layer 1: Camera preview (bottom layer, shows live camera feed)
        CameraPreview(
            cameraController = viewModel.cameraController, // Pass camera controller
            modifier = Modifier.fillMaxSize() // Fill entire screen
        )

        // Layer 2: Bounding boxes (draw rectangles around detected objects)
        ObjectDetectionOverlay(
            detectedObjects = cameraState.detectedObjects, // List of detected objects
            modifier = Modifier.fillMaxSize() // Fill entire screen
        )

        // Layer 3: Text labels at top (show object names and confidence)
        ObjectLabelsOverlay(
            detectedObjects = cameraState.detectedObjects, // List of detected objects
            modifier = Modifier.fillMaxSize() // Fill entire screen
        )

        // Layer 4: Model indicator in top-left corner (shows which AI model is active)
        ModelIndicatorWithDetections(
            currentModel = cameraState.currentModel, // Which AI model (MLKit or EfficientDet)
            detectedObjects = cameraState.detectedObjects, // List of detected objects
            modifier = Modifier
                .align(Alignment.TopStart) // Position in top-left corner
                .padding(16.dp) // 16dp padding from edges
        )

        // Layer 5: Control buttons at bottom (camera controls, photo button, model switch)
        CameraControls(
            onSwitchCamera = { viewModel.switchCamera() }, // What to do when switch camera button pressed
            onTakePhoto = { viewModel.takePhoto() }, // What to do when photo button pressed
            onSwitchModel = { viewModel.switchDetectionModel() }, // What to do when model switch pressed
            isCapturing = cameraState.isCapturing, // Are we currently taking a photo?
            modifier = Modifier
                .align(Alignment.BottomCenter) // Position at bottom center
                .padding(16.dp) // 16dp padding from bottom
        )
    }
}

@Composable // Composable function for camera preview
private fun CameraPreview(
    cameraController: androidx.camera.view.LifecycleCameraController, // Controller that manages camera
    modifier: Modifier = Modifier // Optional styling modifications
) {
    AndroidView( // Embed an Android View (not Compose) into our Compose UI
        factory = { context -> // Function that creates the view
            PreviewView(context).apply { // Create PreviewView for showing camera feed
                this.controller = cameraController // Connect camera controller to preview
            }
        },
        modifier = modifier // Apply any styling modifications
    )
}

@Composable // Composable for drawing bounding boxes
private fun ObjectDetectionOverlay(
    detectedObjects: List<DetectedObject>, // List of objects to draw boxes around
    modifier: Modifier = Modifier // Optional styling modifications
) {
    Canvas(modifier = modifier) { // Create a drawing canvas that covers the screen
        detectedObjects.forEach { detectedObject -> // Loop through each detected object
            drawBoundingBox(detectedObject) // Draw a rectangle around this object
        }
    }
}

@Composable // Composable for showing text labels
private fun ObjectLabelsOverlay(
    detectedObjects: List<DetectedObject>, // List of objects to show labels for
    modifier: Modifier = Modifier // Optional styling modifications
) {
    if (detectedObjects.isNotEmpty()) { // Only show labels if we detected something
        Column( // Arrange labels vertically
            modifier = modifier
                .fillMaxWidth() // Take full width of screen
                .padding(16.dp), // 16dp padding from edges
            verticalArrangement = Arrangement.Top // Align to top of screen
        ) {
            detectedObjects.take(3).forEach { detectedObject -> // Show only first 3 objects
                if (detectedObject.labels.isNotEmpty()) { // If this object has a label
                    val primaryLabel = detectedObject.labels.first() // Get first label
                    val labelText = "${primaryLabel.text} ${(primaryLabel.confidence * 100).toInt()}%" // Format: "person 85%"

                    Card( // Create a colored background card for the text
                        modifier = Modifier
                            .padding(vertical = 2.dp) // Small vertical spacing between cards
                            .wrapContentWidth(), // Card width matches text width
                        colors = CardDefaults.cardColors( // Set card colors
                            containerColor = getColorForObject(detectedObject).copy(alpha = 0.9f) // Semi-transparent colored background
                        ),
                        shape = RoundedCornerShape(4.dp) // Slightly rounded corners
                    ) {
                        Text( // The actual text label
                            text = labelText, // Display "object name XX%"
                            color = Color.White, // White text color
                            fontSize = 12.sp, // Small font size
                            fontWeight = FontWeight.Bold, // Bold text
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp) // Padding inside card
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawBoundingBox(detectedObject: DetectedObject) { // Function to draw a rectangle around detected object
    val boundingBox = detectedObject.boundingBox // Get the box coordinates (0-1 range)

    // Convert normalized coordinates (0-1) to pixel coordinates
    val left = boundingBox.left * size.width // Left edge in pixels
    val top = boundingBox.top * size.height // Top edge in pixels
    val right = boundingBox.right * size.width // Right edge in pixels
    val bottom = boundingBox.bottom * size.height // Bottom edge in pixels

    val boxColor = getColorForObject(detectedObject) // Choose color based on object type

    drawRect( // Draw the rectangle
        color = boxColor, // Color of the rectangle outline
        topLeft = Offset(left, top), // Top-left corner position
        size = Size(right - left, bottom - top), // Width and height of rectangle
        style = Stroke(width = 4f) // Draw outline only (not filled), 4 pixels thick
    )
}

private fun getColorForObject(detectedObject: DetectedObject): Color { // Choose color based on what object was detected
    return when { // Check object type and return appropriate color
        detectedObject.labels.any { it.text.contains("person", ignoreCase = true) } -> Color.Green // Person = Green
        detectedObject.labels.any { it.text.contains("car", ignoreCase = true) } -> Color.Blue // Car = Blue
        detectedObject.labels.any { it.text.contains("dog", ignoreCase = true) } -> Color.Yellow // Dog = Yellow
        detectedObject.labels.any { it.text.contains("cat", ignoreCase = true) } -> Color.Magenta // Cat = Magenta
        detectedObject.labels.any { it.text.contains("food", ignoreCase = true) } -> Color.Cyan // Food = Cyan
        else -> Color.Red // Everything else = Red
    }
}

@Composable // Composable that shows AI model name and list of detections
private fun ModelIndicatorWithDetections(
    currentModel: DetectionModel, // Which AI model is active (MLKit or EfficientDet)
    detectedObjects: List<DetectedObject>, // List of currently detected objects
    modifier: Modifier = Modifier // Optional styling modifications
) {
    Column(modifier = modifier) { // Stack cards vertically

        // First card: Show which AI model is being used
        Card(
            colors = CardDefaults.cardColors( // Set card styling
                containerColor = Color.Black.copy(alpha = 0.7f) // Semi-transparent black background
            ),
            shape = RoundedCornerShape(8.dp) // Rounded corners
        ) {
            Text( // Display model name
                text = when (currentModel) { // Check which model is active
                    DetectionModel.MLKIT -> "MLKit" // Show "MLKit"
                    DetectionModel.EFFICIENTDET -> "EfficientDet" // Show "EfficientDet"
                },
                color = Color.White, // White text
                fontSize = 14.sp, // Medium font size
                fontWeight = FontWeight.Bold, // Bold text
                modifier = Modifier.padding(8.dp) // Padding inside card
            )
        }

        Spacer(modifier = Modifier.height(8.dp)) // 8dp gap between cards

        // Second card: Show list of detected objects or "No objects detected"
        if (detectedObjects.isNotEmpty()) { // If we detected something
            Card(
                colors = CardDefaults.cardColors( // Set card styling
                    containerColor = Color.Black.copy(alpha = 0.7f) // Semi-transparent black background
                ),
                shape = RoundedCornerShape(8.dp) // Rounded corners
            ) {
                Column( // Arrange detection list vertically
                    modifier = Modifier
                        .padding(8.dp) // Padding inside card
                        .widthIn(max = 200.dp) // Maximum width of 200dp
                ) {
                    Text( // Header text
                        text = "Detecting:", // Label for the list
                        color = Color.White.copy(alpha = 0.7f), // Slightly transparent white
                        fontSize = 11.sp, // Small font
                        fontWeight = FontWeight.Bold, // Bold text
                        modifier = Modifier.padding(bottom = 4.dp) // Space below header
                    )

                    detectedObjects.take(5).forEach { obj -> // Show up to 5 detected objects
                        if (obj.labels.isNotEmpty()) { // If object has a label
                            val label = obj.labels.first() // Get first label
                            Row( // Arrange object name and confidence horizontally
                                modifier = Modifier.padding(vertical = 2.dp), // Small vertical spacing
                                horizontalArrangement = Arrangement.SpaceBetween, // Space between name and percentage
                                verticalAlignment = Alignment.CenterVertically // Center vertically
                            ) {
                                Text( // Object name
                                    text = "• ${label.text}", // Bullet point + object name
                                    color = Color.White, // White text
                                    fontSize = 12.sp, // Small font
                                    modifier = Modifier.weight(1f) // Take up remaining space
                                )
                                Text( // Confidence percentage
                                    text = "${(label.confidence * 100).toInt()}%", // Convert 0.85 to "85%"
                                    color = Color.White.copy(alpha = 0.8f), // Slightly transparent white
                                    fontSize = 11.sp, // Small font
                                    modifier = Modifier.padding(start = 8.dp) // 8dp space from object name
                                )
                            }
                        }
                    }

                    if (detectedObjects.size > 5) { // If we detected more than 5 objects
                        Text( // Show count of additional objects
                            text = "+${detectedObjects.size - 5} more", // "e.g. +3 more"
                            color = Color.White.copy(alpha = 0.6f), // More transparent white
                            fontSize = 10.sp, // Very small font
                            modifier = Modifier.padding(top = 4.dp) // Space above this text
                        )
                    }
                }
            }
        } else { // If no objects detected
            Card(
                colors = CardDefaults.cardColors( // Set card styling
                    containerColor = Color.Black.copy(alpha = 0.7f) // Semi-transparent black background
                ),
                shape = RoundedCornerShape(8.dp) // Rounded corners
            ) {
                Text( // Message when nothing detected
                    text = "No objects detected", // Display this message
                    color = Color.White.copy(alpha = 0.6f), // Transparent white (dimmed)
                    fontSize = 11.sp, // Small font
                    modifier = Modifier.padding(8.dp) // Padding inside card
                )
            }
        }
    }
}

@Composable // Composable for camera control buttons
private fun CameraControls(
    onSwitchCamera: () -> Unit, // Function to call when switch camera button pressed
    onTakePhoto: () -> Unit, // Function to call when take photo button pressed
    onSwitchModel: () -> Unit, // Function to call when switch model button pressed
    isCapturing: Boolean, // Are we currently taking a photo?
    modifier: Modifier = Modifier // Optional styling modifications
) {
    Row( // Arrange buttons horizontally
        modifier = modifier, // Apply styling
        horizontalArrangement = Arrangement.spacedBy(16.dp), // 16dp spacing between buttons
        verticalAlignment = Alignment.CenterVertically // Center buttons vertically
    ) {

        // Button 1: Switch between front and back camera
        FloatingActionButton(
            onClick = onSwitchCamera, // What to do when button pressed
            modifier = Modifier
                .size(56.dp) // Medium size button
                .clip(CircleShape), // Make button circular
            containerColor = MaterialTheme.colorScheme.secondary // Use secondary theme color
        ) {
            Icon( // Refresh/rotate icon
                imageVector = Icons.Default.Refresh, // Built-in refresh icon
                contentDescription = "Switch Camera", // Accessibility description
                tint = Color.White // White icon color
            )
        }

        // Button 2: Take photo (center button, largest)
        FloatingActionButton(
            onClick = onTakePhoto, // What to do when button pressed
            modifier = Modifier
                .size(72.dp) // Large size button
                .clip(CircleShape), // Make button circular
            containerColor = MaterialTheme.colorScheme.primary // Use primary theme color
        ) {
            if (isCapturing) { // If currently taking photo
                CircularProgressIndicator( // Show spinning progress indicator
                    modifier = Modifier.size(32.dp), // Medium size
                    color = Color.White, // White color
                    strokeWidth = 3.dp // Thin line
                )
            } else { // If not taking photo
                Icon( // Camera icon (using plus sign)
                    imageVector = Icons.Default.Add, // Plus icon as camera symbol
                    contentDescription = "Take Photo", // Accessibility description
                    tint = Color.White, // White icon color
                    modifier = Modifier.size(32.dp) // Large icon
                )
            }
        }

        // Button 3: Switch between MLKit and EfficientDet AI models
        FloatingActionButton(
            onClick = onSwitchModel, // What to do when button pressed
            modifier = Modifier
                .size(56.dp) // Medium size button
                .clip(CircleShape), // Make button circular
            containerColor = MaterialTheme.colorScheme.tertiary // Use tertiary theme color
        ) {
            Icon( // Settings/gear icon
                imageVector = Icons.Default.Settings, // Built-in settings icon
                contentDescription = "Switch AI Model", // Accessibility description
                tint = Color.White // White icon color
            )
        }
    }
}