package com.example.objectdetection.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.camera.view.PreviewView
import com.example.objectdetection.model.DetectedObject
import com.example.objectdetection.model.DetectionModel
import com.example.objectdetection.viewmodel.AuthViewModel
import com.example.objectdetection.viewmodel.CameraViewModel

@Composable
fun CameraScreen(
    viewModel: CameraViewModel,
    authViewModel: AuthViewModel, // NEW: For logout
    onNavigateToGallery: () -> Unit, // NEW: Navigate to gallery
    onLogout: () -> Unit // NEW: Handle logout
) {
    val cameraState by viewModel.cameraState
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    LaunchedEffect(viewModel.cameraController) {
        viewModel.bindToLifecycle(lifecycleOwner)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // Layer 1: Camera preview
        CameraPreview(
            cameraController = viewModel.cameraController,
            modifier = Modifier.fillMaxSize()
        )

        // Layer 2: Bounding boxes
        ObjectDetectionOverlay(
            detectedObjects = cameraState.detectedObjects,
            modifier = Modifier.fillMaxSize()
        )

        // Layer 3: Text labels
        ObjectLabelsOverlay(
            detectedObjects = cameraState.detectedObjects,
            modifier = Modifier.fillMaxSize()
        )

        // Layer 4: Model indicator
        ModelIndicatorWithDetections(
            currentModel = cameraState.currentModel,
            detectedObjects = cameraState.detectedObjects,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        )

        // NEW: Layer 5: Top-right buttons (Gallery and Logout)
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Gallery button
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
            FloatingActionButton(
                onClick = {
                    authViewModel.logout()
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
        CameraControls(
            onSwitchCamera = { viewModel.switchCamera() },
            onTakePhoto = { viewModel.takePhoto() },
            onSwitchModel = { viewModel.switchDetectionModel() },
            isCapturing = cameraState.isCapturing,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

@Composable
private fun CameraPreview(
    cameraController: androidx.camera.view.LifecycleCameraController,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            PreviewView(context).apply {
                this.controller = cameraController
            }
        },
        modifier = modifier
    )
}

@Composable
private fun ObjectDetectionOverlay(
    detectedObjects: List<DetectedObject>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        detectedObjects.forEach { detectedObject ->
            drawBoundingBox(detectedObject)
        }
    }
}

@Composable
private fun ObjectLabelsOverlay(
    detectedObjects: List<DetectedObject>,
    modifier: Modifier = Modifier
) {
    if (detectedObjects.isNotEmpty()) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            detectedObjects.take(3).forEach { detectedObject ->
                if (detectedObject.labels.isNotEmpty()) {
                    val primaryLabel = detectedObject.labels.first()
                    val labelText = "${primaryLabel.text} ${(primaryLabel.confidence * 100).toInt()}%"

                    Card(
                        modifier = Modifier
                            .padding(vertical = 2.dp)
                            .wrapContentWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = getColorForObject(detectedObject).copy(alpha = 0.9f)
                        ),
                        shape = RoundedCornerShape(4.dp)
                    ) {
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

private fun DrawScope.drawBoundingBox(detectedObject: DetectedObject) {
    val boundingBox = detectedObject.boundingBox

    val left = boundingBox.left * size.width
    val top = boundingBox.top * size.height
    val right = boundingBox.right * size.width
    val bottom = boundingBox.bottom * size.height

    val boxColor = getColorForObject(detectedObject)

    drawRect(
        color = boxColor,
        topLeft = Offset(left, top),
        size = Size(right - left, bottom - top),
        style = Stroke(width = 4f)
    )
}

private fun getColorForObject(detectedObject: DetectedObject): Color {
    return when {
        detectedObject.labels.any { it.text.contains("person", ignoreCase = true) } -> Color.Green
        detectedObject.labels.any { it.text.contains("car", ignoreCase = true) } -> Color.Blue
        detectedObject.labels.any { it.text.contains("dog", ignoreCase = true) } -> Color.Yellow
        detectedObject.labels.any { it.text.contains("cat", ignoreCase = true) } -> Color.Magenta
        detectedObject.labels.any { it.text.contains("food", ignoreCase = true) } -> Color.Cyan
        else -> Color.Red
    }
}

@Composable
private fun ModelIndicatorWithDetections(
    currentModel: DetectionModel,
    detectedObjects: List<DetectedObject>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.7f)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
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

        Spacer(modifier = Modifier.height(8.dp))

        if (detectedObjects.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .widthIn(max = 200.dp)
                ) {
                    Text(
                        text = "Detecting:",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    detectedObjects.take(5).forEach { obj ->
                        if (obj.labels.isNotEmpty()) {
                            val label = obj.labels.first()
                            Row(
                                modifier = Modifier.padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "• ${label.text}",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${(label.confidence * 100).toInt()}%",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }

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
            Card(
                colors = CardDefaults.cardColors(
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

@Composable
private fun CameraControls(
    onSwitchCamera: () -> Unit,
    onTakePhoto: () -> Unit,
    onSwitchModel: () -> Unit,
    isCapturing: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

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

        FloatingActionButton(
            onClick = onTakePhoto,
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            if (isCapturing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = Color.White,
                    strokeWidth = 3.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Take Photo",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

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