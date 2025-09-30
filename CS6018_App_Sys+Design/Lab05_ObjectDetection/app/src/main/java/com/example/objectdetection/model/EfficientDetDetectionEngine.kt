package com.example.objectdetection.model

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageProxy
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import com.example.objectdetection.ml.AutoModel2 // Auto-generated class
import androidx.core.graphics.createBitmap

class EfficientDetDetectionEngine(private val context: Context) {

    private var model: AutoModel2? = null
    private var isInitialized = false

    // Image processor for 640x640 input (as specified in tensor description)
    private val imageProcessor = ImageProcessor.Builder()
        .add(ResizeOp(640, 640, ResizeOp.ResizeMethod.BILINEAR))
        .build()

    // COCO dataset labels for object detection
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

    suspend fun initialize(): Boolean {
        return try {
            Log.d("EfficientDet", "Initializing AutoModel2...")
            model = AutoModel2.newInstance(context)
            isInitialized = true
            Log.d("EfficientDet", "AutoModel2 initialized successfully")
            true
        } catch (e: Exception) {
            Log.e("EfficientDet", "Failed to initialize Model2", e)
            isInitialized = false
            false
        }
    }

    suspend fun detectObjects(imageProxy: ImageProxy): List<DetectedObject> {
        if (!isInitialized || model == null) {
            Log.w("EfficientDet", "Model not initialized")
            return emptyList()
        }

        try {
            val bitmap = imageProxyToBitmap(imageProxy)
            if (bitmap == null) {
                Log.w("EfficientDet", "Failed to convert ImageProxy to Bitmap")
                return emptyList()
            }

            Log.d("EfficientDet", "Processing bitmap: ${bitmap.width}x${bitmap.height}")

            // Resize and process image to 640x640 as expected by model
            val tensorImage = TensorImage.fromBitmap(bitmap)
            val processedImage = imageProcessor.process(tensorImage)

            Log.d("EfficientDet", "Processed image to: ${processedImage.width}x${processedImage.height}")

            // Run model inference
            val outputs = model!!.process(processedImage)

            // Access the specific outputs based on tensor specification
            val locationFeature = outputs.locationAsTensorBuffer
            val categoryFeature = outputs.categoryAsTensorBuffer
            val scoreFeature = outputs.scoreAsTensorBuffer
            val numDetectionsFeature = outputs.numberOfDetectionsAsTensorBuffer

            val numDetections = numDetectionsFeature.floatArray[0].toInt()
            Log.d("EfficientDet", "Model returned $numDetections detections")

            val locations = locationFeature.floatArray
            val categories = categoryFeature.floatArray
            val scores = scoreFeature.floatArray

            val detectedObjects = mutableListOf<DetectedObject>()

            for (i in 0 until numDetections.coerceAtMost(100)) {
                val score = scores[i]

                Log.d("EfficientDet", "Detection $i: score=$score")

                // Lower threshold for testing
                if (score > 0.3f) {
                    // Extract bounding box coordinates (typically ymin, xmin, ymax, xmax)
                    val ymin = locations[i * 4 + 0]     // top
                    val xmin = locations[i * 4 + 1]     // left
                    val ymax = locations[i * 4 + 2]     // bottom
                    val xmax = locations[i * 4 + 3]     // right

                    Log.d("EfficientDet", "Raw coordinates: ymin=$ymin, xmin=$xmin, ymax=$ymax, xmax=$xmax")

                    // Ensure coordinates are normalized (0-1 range)
                    val left = xmin.coerceIn(0f, 1f)
                    val top = ymin.coerceIn(0f, 1f)
                    val right = xmax.coerceIn(0f, 1f)
                    val bottom = ymax.coerceIn(0f, 1f)

                    // Get category index and map to label
                    val categoryIndex = categories[i].toInt()
                    val label = if (categoryIndex < labels.size && categoryIndex >= 0) {
                        labels[categoryIndex]
                    } else {
                        "Object $categoryIndex"
                    }

                    Log.d("EfficientDet", "Final detection: $label, score=$score, cords=($left,$top,$right,$bottom)")

                    val boundingBox = BoundingBox(left, top, right, bottom)
                    val objectLabel = ObjectLabel(
                        text = label,
                        confidence = score,
                        index = categoryIndex
                    )

                    val detectedObject = DetectedObject(
                        boundingBox = boundingBox,
                        labels = listOf(objectLabel)
                    )

                    detectedObjects.add(detectedObject)
                }
            }

            Log.d("EfficientDet", "Returning ${detectedObjects.size} valid detections")
            return detectedObjects

        } catch (e: Exception) {
            Log.e("EfficientDet", "Error during object detection", e)
            e.printStackTrace()
            return emptyList()
        }
    }

    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
        return ImageUtils.imageProxyToBitmap(imageProxy)
            ?: createBitmap(640, 640)
    }

    fun close() {
        try {
            model?.close()
            model = null
            isInitialized = false
            Log.d("EfficientDet", "AutoModel2 closed successfully")
        } catch (e: Exception) {
            Log.e("EfficientDet", "Error closing model", e)
        }
    }
}