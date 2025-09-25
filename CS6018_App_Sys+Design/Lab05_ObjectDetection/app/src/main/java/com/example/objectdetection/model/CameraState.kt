package com.example.objectdetection.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

enum class DetectionModel { // Available AI models
    MLKIT, // Google's MLKit (easier setup)
    EFFICIENTDET // TensorFlow Lite EfficientDet (better accuracy)
}

data class CameraState( // Current state of camera app
    val lensFacing: Int, // Which camera (front/back)
    val detectedObjects: List<DetectedObject> = emptyList(), // Objects found in current frame
    val isCapturing: Boolean = false, // Currently taking photo?
    val currentModel: DetectionModel = DetectionModel.EFFICIENTDET // Default to EfficientDet
)

data class DetectedObject( // Single object found by AI
    val boundingBox: BoundingBox, // Rectangle around object
    val labels: List<ObjectLabel>, // What the object is
    val trackingId: Int? = null, // ID for tracking across frames
    val timestamp: Long = System.currentTimeMillis() // When object was detected
)

data class BoundingBox( // Rectangle coordinates for detected object
    val left: Float, // Left edge (0.0 = left side of image, 1.0 = right side)
    val top: Float, // Top edge (0.0 = top of image, 1.0 = bottom)
    val right: Float, // Right edge (0.0 = left side of image, 1.0 = right side)
    val bottom: Float // Bottom edge (0.0 = top of image, 1.0 = bottom)
) {
    fun toOffset(): Offset = Offset(left, top) // Convert to Compose coordinates
    fun toSize(): Size = Size(right - left, bottom - top) // Get width and height

    fun center(): Offset = Offset( // Get center point of box
        (left + right) / 2f, // X coordinate of center
        (top + bottom) / 2f // Y coordinate of center
    )
}

data class ObjectLabel( // Classification of detected object
    val text: String, // Object name (e.g. "person", "car", "dog")
    val confidence: Float, // How sure AI is (0.0 = not sure, 1.0 = very sure)
    val index: Int? = null // Category index from AI model
)