package com.example.objectdetection.model

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

class EfficientDetDetectionEngine(private val context: Context) {

    private var interpreter: Interpreter? = null // TensorFlow Lite interpreter
    private var isInitialized = false // Track if model loaded successfully

    private val imageProcessor = ImageProcessor.Builder() // Image preprocessor
        .add(ResizeOp(512, 512, ResizeOp.ResizeMethod.BILINEAR)) // Resize to 512x512 pixels
        .build()

    // Objects that EfficientDet can detect (COCO dataset labels)
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

    suspend fun initialize(): Boolean { // Load the TensorFlow model
        return try {
            val modelBuffer = loadModelFile() // Load .tflite file from assets

            val options = Interpreter.Options() // TensorFlow options
            options.setNumThreads(4) // Use 4 CPU threads for speed
            interpreter = Interpreter(modelBuffer, options) // Create interpreter

            isInitialized = true // Mark as ready
            true // Success
        } catch (e: Exception) {
            e.printStackTrace() // Log error
            false // Failed
        }
    }

    private fun loadModelFile(): ByteBuffer { // Load .tflite model from assets folder
        val assetFileDescriptor = context.assets.openFd("2.tflite") // Open model file
        val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor) // Create input stream
        val fileChannel = inputStream.channel // Get file channel
        val startOffset = assetFileDescriptor.startOffset // File start position
        val declaredLength = assetFileDescriptor.declaredLength // File length
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength) // Map file to memory
    }

    suspend fun detectObjects(imageProxy: ImageProxy): List<DetectedObject> { // Analyze camera frame
        if (!isInitialized || interpreter == null) { // If model not loaded
            return emptyList() // Return no detections
        }

        try {
            val bitmap = imageProxyToBitmap(imageProxy) // Convert camera frame to bitmap
            val tensorImage = TensorImage.fromBitmap(bitmap) // Convert to tensor format
            val processedImage = imageProcessor.process(tensorImage) // Resize to 512x512

            // Prepare output arrays for model results
            val outputLocations = Array(1) { Array(100) { FloatArray(4) } } // Bounding boxes (x,y,w,h)
            val outputClasses = Array(1) { FloatArray(100) } // Object class indices
            val outputScores = Array(1) { FloatArray(100) } // Confidence scores
            val numDetections = FloatArray(1) // Total number of detections

            val outputMap = mapOf( // Map outputs to arrays
                0 to outputLocations, // Bounding boxes
                1 to outputClasses, // Classes
                2 to outputScores, // Confidence scores
                3 to numDetections // Detection count
            )

            interpreter!!.runForMultipleInputsOutputs( // Run AI inference
                arrayOf(processedImage.buffer), // Input: processed image
                outputMap // Outputs: detection results
            )

            val detectedObjects = mutableListOf<DetectedObject>() // List to store results
            val numObjects = numDetections[0].toInt().coerceAtMost(100) // Max 100 objects

            for (i in 0 until numObjects) { // For each detected object
                val confidence = outputScores[0][i] // Get confidence score

                if (confidence > 0.5f) { // Only keep high-confidence detections (>50%)
                    val top = outputLocations[0][i][0] // Bounding box coordinates
                    val left = outputLocations[0][i][1]
                    val bottom = outputLocations[0][i][2]
                    val right = outputLocations[0][i][3]

                    val classIndex = outputClasses[0][i].toInt() // Get object class
                    val label = if (classIndex < labels.size) labels[classIndex] else "Unknown" // Get label name

                    val detectedObject = DetectedObject( // Create detected object
                        boundingBox = BoundingBox(left, top, right, bottom), // Set bounding box
                        labels = listOf(ObjectLabel(label, confidence, classIndex)) // Set label and confidence
                    )

                    detectedObjects.add(detectedObject) // Add to results
                }
            }

            return detectedObjects // Return all detected objects

        } catch (e: Exception) {
            e.printStackTrace() // Log error
            return emptyList() // Return no detections on error
        }
    }

    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap { // Convert camera frame to bitmap
        return ImageUtils.imageProxyToBitmap(imageProxy) // Use utility function
            ?: Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888) // Fallback empty bitmap
    }

    fun close() { // Clean up resources
        interpreter?.close() // Close TensorFlow interpreter
        interpreter = null // Clear reference
        isInitialized = false // Mark as not initialized
    }
}