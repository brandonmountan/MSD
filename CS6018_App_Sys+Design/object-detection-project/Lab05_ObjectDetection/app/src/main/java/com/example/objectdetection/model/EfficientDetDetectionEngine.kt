package com.example.objectdetection.model

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageProxy
import org.tensorflow.lite.support.image.TensorImage
import com.example.objectdetection.ml.AutoModel2 // Auto-generated class from our 2.tflite model file
import androidx.core.graphics.createBitmap

class EfficientDetDetectionEngine(private val context: Context) { // Class that handles EfficientDet AI object detection

    private var model: AutoModel2? = null // The TensorFlow Lite model (null until loaded)
    private var isInitialized = false // Track whether model successfully loaded

    suspend fun initialize(): Boolean { // Load the AI model when app starts
        return try { // Try to load the model
            Log.d("EfficientDet", "Initializing AutoModel2...") // Log that we're starting
            model = AutoModel2.newInstance(context) // Create new model instance from 2.tflite file
            isInitialized = true // Mark that model loaded successfully
            Log.d("EfficientDet", "AutoModel2 initialized successfully") // Log success
            true // Return true to indicate success
        } catch (e: Exception) { // If anything goes wrong
            Log.e("EfficientDet", "Failed to initialize AutoModel2", e) // Log the error
            isInitialized = false // Mark that model failed to load
            false // Return false to indicate failure
        }
    }

    suspend fun detectObjects(imageProxy: ImageProxy): List<DetectedObject> { // Main function to detect objects in camera frame
        if (!isInitialized || model == null) { // Check if model is ready to use
            Log.w("EfficientDet", "Model not initialized") // Log warning
            return emptyList() // Return empty list (no detections)
        }

        try { // Try to run object detection
            val bitmap = imageProxyToBitmap(imageProxy) // Convert camera frame to bitmap image
            if (bitmap == null) { // If conversion failed
                Log.w("EfficientDet", "Failed to convert ImageProxy to Bitmap") // Log warning
                return emptyList() // Return empty list (no detections)
            }

            Log.d("EfficientDet", "Processing bitmap: ${bitmap.width}x${bitmap.height}") // Log original image size

            // Create input tensor from bitmap (model handles resizing internally)
            val image = TensorImage.fromBitmap(bitmap) // Convert bitmap to TensorImage format

            // Run AI model inference (this is where the magic happens!)
            val outputs = model!!.process(image) // Feed image to AI and get results

            // Get the list of detection results (using the correct API as shown in example)
            val detectionResultList = outputs.detectionResultList // List of detected objects

            Log.d("EfficientDet", "Model returned ${detectionResultList.size} detections") // Log how many objects found

            val detectedObjects = mutableListOf<DetectedObject>() // List to store final detected objects

            // Loop through each detection result
            detectionResultList.forEach { detectionResult ->
                // Extract detection information (as shown in example)
                val location = detectionResult.locationAsRectF // Bounding box coordinates
                val category = detectionResult.categoryAsString // Object name (e.g., "person", "car")
                val score = detectionResult.scoreAsFloat // Confidence score (0.0 to 1.0)

                Log.d("EfficientDet", "Detection: category='$category', score=$score, location=$location") // Log detection info

                // Only keep detections with confidence > 50% (higher = fewer false positives)
                if (score > 0.5f) { // Filter out low-confidence detections

                    // Convert RectF coordinates to normalized (0-1) range
                    // The location might be in pixel coordinates, so we need to normalize
                    val left = (location.left / bitmap.width).coerceIn(0f, 1f) // Left edge (0-1)
                    val top = (location.top / bitmap.height).coerceIn(0f, 1f) // Top edge (0-1)
                    val right = (location.right / bitmap.width).coerceIn(0f, 1f) // Right edge (0-1)
                    val bottom = (location.bottom / bitmap.height).coerceIn(0f, 1f) // Bottom edge (0-1)

                    // Calculate box size and skip if too small (likely false positive)
                    val boxWidth = right - left // Width as fraction of image
                    val boxHeight = bottom - top // Height as fraction of image
                    val boxArea = boxWidth * boxHeight // Area as fraction of image

                    if (boxArea < 0.01f) { // Skip boxes smaller than 1% of image (likely noise)
                        Log.d("EfficientDet", "Skipping tiny box: area=$boxArea")
                        return@forEach // Skip to next detection
                    }

                    Log.d("EfficientDet", "Valid detection: $category at ($left,$top,$right,$bottom)") // Log valid detection

                    // Create bounding box object with normalized coordinates
                    val boundingBox = BoundingBox(left, top, right, bottom)

                    // Create object label with name and confidence
                    val objectLabel = ObjectLabel(
                        text = category, // Object name (e.g., "person", "car")
                        confidence = score, // Confidence score (0-1)
                        index = null // No index available from this API
                    )

                    // Create detected object combining box and label
                    val detectedObject = DetectedObject(
                        boundingBox = boundingBox, // Where the object is
                        labels = listOf(objectLabel) // What the object is
                    )

                    detectedObjects.add(detectedObject) // Add to our list of detections
                }
            }

            Log.d("EfficientDet", "Returning ${detectedObjects.size} valid detections before NMS") // Log count before filtering

            // Apply Non-Maximum Suppression to remove duplicate/overlapping detections
            val filteredObjects = applyNMS(detectedObjects, iouThreshold = 0.5f)

            Log.d("EfficientDet", "Returning ${filteredObjects.size} detections after NMS") // Log final count
            return filteredObjects // Return the filtered list of detected objects

        } catch (e: Exception) { // If anything goes wrong during detection
            Log.e("EfficientDet", "Error during object detection", e) // Log the error
            e.printStackTrace() // Print full error stack trace
            return emptyList() // Return empty list (no detections)
        }
    }

    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? { // Convert camera frame to bitmap
        return ImageUtils.imageProxyToBitmap(imageProxy) // Use utility function to convert
            ?: createBitmap(640, 640) // If conversion fails, create blank 640x640 bitmap
    }

    // Non-Maximum Suppression: Remove overlapping duplicate detections
    private fun applyNMS(detections: List<DetectedObject>, iouThreshold: Float): List<DetectedObject> {
        if (detections.isEmpty()) return detections // If no detections, return empty list

        // Sort detections by confidence (highest first)
        val sortedDetections = detections.sortedByDescending {
            it.labels.firstOrNull()?.confidence ?: 0f
        }

        val selected = mutableListOf<DetectedObject>() // List of detections to keep

        sortedDetections.forEach { detection -> // Check each detection
            var shouldKeep = true // Assume we'll keep this detection

            // Compare with already selected detections
            selected.forEach { selectedDetection ->
                val iou = calculateIoU(detection.boundingBox, selectedDetection.boundingBox) // Calculate overlap
                if (iou > iouThreshold) { // If boxes overlap too much
                    shouldKeep = false // Don't keep this detection (it's a duplicate)
                    return@forEach // Stop checking
                }
            }

            if (shouldKeep) { // If this detection is unique
                selected.add(detection) // Add it to our final list
            }
        }

        return selected // Return filtered list
    }

    // Calculate Intersection over Union (IoU) - measures how much two boxes overlap
    private fun calculateIoU(box1: BoundingBox, box2: BoundingBox): Float {
        // Calculate intersection area (overlapping region)
        val intersectionLeft = maxOf(box1.left, box2.left) // Leftmost edge of overlap
        val intersectionTop = maxOf(box1.top, box2.top) // Topmost edge of overlap
        val intersectionRight = minOf(box1.right, box2.right) // Rightmost edge of overlap
        val intersectionBottom = minOf(box1.bottom, box2.bottom) // Bottommost edge of overlap

        // Calculate intersection width and height
        val intersectionWidth = maxOf(0f, intersectionRight - intersectionLeft)
        val intersectionHeight = maxOf(0f, intersectionBottom - intersectionTop)
        val intersectionArea = intersectionWidth * intersectionHeight // Area of overlap

        // Calculate area of each box
        val box1Area = (box1.right - box1.left) * (box1.bottom - box1.top)
        val box2Area = (box2.right - box2.left) * (box2.bottom - box2.top)

        // Calculate union area (total area covered by both boxes)
        val unionArea = box1Area + box2Area - intersectionArea

        // Return IoU (0 = no overlap, 1 = complete overlap)
        return if (unionArea > 0) intersectionArea / unionArea else 0f
    }

    fun close() { // Clean up when we're done using the model
        try { // Try to close the model
            model?.close() // Close TensorFlow Lite model (free memory)
            model = null // Clear the model reference
            isInitialized = false // Mark as not initialized
            Log.d("EfficientDet", "AutoModel2 closed successfully") // Log success
        } catch (e: Exception) { // If closing fails
            Log.e("EfficientDet", "Error closing model", e) // Log the error
        }
    }
}