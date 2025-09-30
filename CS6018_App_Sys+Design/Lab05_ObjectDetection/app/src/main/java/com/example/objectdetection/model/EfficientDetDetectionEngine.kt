package com.example.objectdetection.model

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageProxy
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import com.example.objectdetection.ml.AutoModel2 // Auto-generated class from our 2.tflite model file
import androidx.core.graphics.createBitmap

class EfficientDetDetectionEngine(private val context: Context) { // Class that handles EfficientDet AI object detection

    private var model: AutoModel2? = null // The TensorFlow Lite model (null until loaded)
    private var isInitialized = false // Track whether model successfully loaded

    // Image processor that resizes images to 640x640 pixels (required by our model)
    private val imageProcessor = ImageProcessor.Builder() // Create image processor builder
        .add(ResizeOp(640, 640, ResizeOp.ResizeMethod.BILINEAR)) // Add resize operation to 640x640
        .build() // Build the processor

    // COCO dataset labels - these are the 80 objects our model can detect
    private val labels = arrayOf(
        "person", "bicycle", "car", "motorcycle", "airplane", "bus", "train", "truck", "boat",
        "traffic light", "fire hydrant", "stop sign", "parking meter", "bench", "bird", "cat",
        "dog", "horse", "sheep", "cow", "elephant", "bear", "zebra", "giraffe", "backpack",
        "umbrella", "handbag", "tie", "suitcase", "frisbee", "skis", "snowboard", "sports ball",
        "kite", "baseball bat", "baseball glove", "skateboard", "surfboard", "tennis racket",
        "bottle", "wine glass", "cup", "fork", "knife", "spoon", "bowl", "banana", "apple",
        "sandwich", "orange", "broccoli", "carrot", "hot dog", "pizza", "donut", "cake", "chair",
        "couch", "potted plant", "bed", "dining table", "toilet", "tv", "laptop", "mouse",
        "remote", "keyboard", "cell phone", "microwave", "oven", "toaster", "sink", "refrigerator",
        "book", "clock", "vase", "scissors", "teddy bear", "hair drier", "toothbrush"
    )

    suspend fun initialize(): Boolean { // Load the AI model when app starts
        return try { // Try to load the model
            Log.d("EfficientDet", "Initializing AutoModel2...") // Log that we're starting
            model = AutoModel2.newInstance(context) // Create new model instance from 2.tflite file
            isInitialized = true // Mark that model loaded successfully
            Log.d("EfficientDet", "AutoModel2 initialized successfully") // Log success
            true // Return true to indicate success
        } catch (e: Exception) { // If anything goes wrong
            Log.e("EfficientDet", "Failed to initialize Model2", e) // Log the error
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

            // Prepare image for AI model
            val tensorImage = TensorImage.fromBitmap(bitmap) // Convert bitmap to TensorImage format
            val processedImage = imageProcessor.process(tensorImage) // Resize to 640x640 pixels

            Log.d("EfficientDet", "Processed image to: ${processedImage.width}x${processedImage.height}") // Log processed size (should be 640x640)

            // Run AI model inference (this is where the magic happens!)
            val outputs = model!!.process(processedImage) // Feed image to AI and get results

            // Extract the model's outputs (locations, categories, scores, count)
            val locationFeature = outputs.locationAsTensorBuffer // Bounding box coordinates for each object
            val categoryFeature = outputs.categoryAsTensorBuffer // Category index for each object (0-79)
            val scoreFeature = outputs.scoreAsTensorBuffer // Confidence score for each object (0-1)
            val numDetectionsFeature = outputs.numberOfDetectionsAsTensorBuffer // Total number of objects found

            val numDetections = numDetectionsFeature.floatArray[0].toInt() // Get count as integer
            Log.d("EfficientDet", "Model returned $numDetections detections") // Log how many objects found

            // Convert tensor buffers to regular arrays for easier access
            val locations = locationFeature.floatArray // Array of bounding box coordinates
            val categories = categoryFeature.floatArray // Array of category indices
            val scores = scoreFeature.floatArray // Array of confidence scores

            val detectedObjects = mutableListOf<DetectedObject>() // List to store final detected objects

            for (i in 0 until numDetections.coerceAtMost(100)) { // Loop through each detection (max 100)
                val score = scores[i] // Get confidence score for this detection

                Log.d("EfficientDet", "Detection $i: score=$score") // Log the confidence score

                // Only keep detections with confidence > 30%
                if (score > 0.3f) { // Filter out low-confidence detections

                    // Extract bounding box coordinates (model outputs: ymin, xmin, ymax, xmax)
                    val ymin = locations[i * 4 + 0]     // Top edge (0-1 range)
                    val xmin = locations[i * 4 + 1]     // Left edge (0-1 range)
                    val ymax = locations[i * 4 + 2]     // Bottom edge (0-1 range)
                    val xmax = locations[i * 4 + 3]     // Right edge (0-1 range)

                    Log.d("EfficientDet", "Raw coordinates: ymin=$ymin, xmin=$xmin, ymax=$ymax, xmax=$xmax") // Log raw coordinates

                    // Ensure coordinates are valid (between 0 and 1)
                    val left = xmin.coerceIn(0f, 1f) // Clamp left to 0-1 range
                    val top = ymin.coerceIn(0f, 1f) // Clamp top to 0-1 range
                    val right = xmax.coerceIn(0f, 1f) // Clamp right to 0-1 range
                    val bottom = ymax.coerceIn(0f, 1f) // Clamp bottom to 0-1 range

                    // Get category index and map it to a human-readable label
                    val categoryIndex = categories[i].toInt() // Get category index (0-79)
                    val label = if (categoryIndex < labels.size && categoryIndex >= 0) { // If valid index
                        labels[categoryIndex] // Get label name from our labels array
                    } else { // If invalid index
                        "Object $categoryIndex" // Use generic name
                    }

                    Log.d("EfficientDet", "Final detection: $label, score=$score, cords=($left,$top,$right,$bottom)") // Log final detection info

                    // Create bounding box object with normalized coordinates
                    val boundingBox = BoundingBox(left, top, right, bottom)

                    // Create object label with name and confidence
                    val objectLabel = ObjectLabel(
                        text = label, // Object name (e.g., "person", "car")
                        confidence = score, // Confidence score (0-1)
                        index = categoryIndex // Category index (0-79)
                    )

                    // Create detected object combining box and label
                    val detectedObject = DetectedObject(
                        boundingBox = boundingBox, // Where the object is
                        labels = listOf(objectLabel) // What the object is
                    )

                    detectedObjects.add(detectedObject) // Add to our list of detections
                }
            }

            Log.d("EfficientDet", "Returning ${detectedObjects.size} valid detections") // Log final count
            return detectedObjects // Return the list of detected objects

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