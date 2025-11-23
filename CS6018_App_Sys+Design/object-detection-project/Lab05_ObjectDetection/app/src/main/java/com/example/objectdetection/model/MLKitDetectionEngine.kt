package com.example.objectdetection.model

import android.graphics.Rect
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import kotlinx.coroutines.tasks.await

class MLKitDetectionEngine {

    private val detector: ObjectDetector by lazy { // Create detector when first used
        val options = ObjectDetectorOptions.Builder() // Configure MLKit options
            .setDetectorMode(ObjectDetectorOptions.STREAM_MODE) // Optimize for video stream
            .enableMultipleObjects() // Detect multiple objects at once
            .enableClassification() // Try to identify what objects are
            .build()

        ObjectDetection.getClient(options) // Create MLKit detector
    }

    suspend fun detectObjects(imageProxy: ImageProxy): List<DetectedObject> { // Analyze camera frame
        try {
            val inputImage = InputImage.fromMediaImage( // Convert camera frame to MLKit format
                imageProxy.image!!, // Get image data
                imageProxy.imageInfo.rotationDegrees // Handle device rotation
            )

            val mlkitObjects = detector.process(inputImage).await() // Run MLKit detection

            return mlkitObjects.map { mlkitObject -> // Convert MLKit results to our format
                val boundingBox = mlkitObject.boundingBox.toNormalizedBoundingBox( // Convert bounding box
                    imageProxy.width, // Image width
                    imageProxy.height // Image height
                )

                val labels = mlkitObject.labels.map { label -> // Convert labels
                    ObjectLabel(
                        text = label.text ?: "Unknown", // Object type (e.g. "Food", "Plant")
                        confidence = label.confidence, // How sure MLKit is (0.0 to 1.0)
                        index = label.index // Category index
                    )
                }

                DetectedObject( // Create our detected object
                    boundingBox = boundingBox, // Rectangle around object
                    labels = labels, // Object classifications
                    trackingId = mlkitObject.trackingId // MLKit tracking ID
                )
            }

        } catch (e: Exception) {
            e.printStackTrace() // Log error
            return emptyList() // Return empty list on error
        }
    }

    fun close() { // Clean up MLKit detector
        detector.close()
    }
}

private fun Rect.toNormalizedBoundingBox(imageWidth: Int, imageHeight: Int): BoundingBox { // Convert Android Rect to our BoundingBox
    return BoundingBox(
        left = left.toFloat() / imageWidth, // Convert pixel to 0.0-1.0 range
        top = top.toFloat() / imageHeight, // Convert pixel to 0.0-1.0 range
        right = right.toFloat() / imageWidth, // Convert pixel to 0.0-1.0 range
        bottom = bottom.toFloat() / imageHeight // Convert pixel to 0.0-1.0 range
    )
}