package com.example.objectdetection.model

import android.content.Context
import androidx.camera.core.ImageProxy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CameraRepository(private val context: Context) {

    private val mlkitEngine = MLKitDetectionEngine() // Google's MLKit detector
    private val efficientDetEngine = EfficientDetDetectionEngine(context) // TensorFlow Lite detector

    private val detectionHistory = mutableListOf<List<DetectedObject>>() // Store recent results
    private var currentModel = DetectionModel.EFFICIENTDET // Default to EfficientDet
    private var isEfficientDetReady = false // Track if EfficientDet loaded successfully

    suspend fun initialize() { // Set up AI models
        isEfficientDetReady = efficientDetEngine.initialize() // Try to load EfficientDet model
        // MLKit doesn't need initialization
    }

    suspend fun detectObjects(imageProxy: ImageProxy): List<DetectedObject> { // Run object detection
        return withContext(Dispatchers.Default) { // Run on background thread for performance
            when (currentModel) { // Check which model to use
                DetectionModel.MLKIT -> { // Use Google MLKit
                    mlkitEngine.detectObjects(imageProxy)
                }
                DetectionModel.EFFICIENTDET -> { // Use TensorFlow EfficientDet
                    if (isEfficientDetReady) { // If model loaded successfully
                        efficientDetEngine.detectObjects(imageProxy)
                    } else { // If EfficientDet failed to load
                        mlkitEngine.detectObjects(imageProxy) // Fall back to MLKit
                    }
                }
            }
        }
    }

    fun switchModel(newModel: DetectionModel) { // Change which AI model to use
        currentModel = newModel
    }

    fun getCurrentModel(): DetectionModel = currentModel // Get current AI model

    fun isEfficientDetAvailable(): Boolean = isEfficientDetReady // Check if EfficientDet is working

    fun addDetectionResult(objects: List<DetectedObject>) { // Store detection results
        detectionHistory.add(objects) // Add to history

        if (detectionHistory.size > 50) { // Keep only recent results
            detectionHistory.removeAt(0) // Remove oldest result
        }
    }

    fun getRecentDetections(count: Int = 10): List<List<DetectedObject>> { // Get recent detection results
        return detectionHistory.takeLast(count) // Return last 'count' results
    }

    fun getObjectCountStats(): Map<String, Int> { // Count how many of each object type detected
        val allObjects = detectionHistory.flatten() // Combine all detection results
        val objectCounts = mutableMapOf<String, Int>() // Map to count objects

        allObjects.forEach { detectedObject -> // For each detected object
            detectedObject.labels.forEach { label -> // For each label of the object
                val currentCount = objectCounts[label.text] ?: 0 // Get current count (or 0)
                objectCounts[label.text] = currentCount + 1 // Increment count
            }
        }

        return objectCounts // Return count map
    }

    fun clearHistory() { // Clear all stored detection results
        detectionHistory.clear()
    }

    fun cleanup() { // Clean up resources when app closes
        mlkitEngine.close() // Close MLKit detector
        efficientDetEngine.close() // Close EfficientDet detector
    }
}