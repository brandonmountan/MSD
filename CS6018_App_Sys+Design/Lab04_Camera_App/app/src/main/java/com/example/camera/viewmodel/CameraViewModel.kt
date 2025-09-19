package com.example.camera.viewmodel

import android.app.Application
import android.content.ContentValues
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import com.example.camera.model.CameraState
import com.example.camera.model.CameraRepository
import com.example.camera.model.PointOfInterest
import java.text.SimpleDateFormat
import java.util.*

// ViewModel that handles all camera logic and state management
class CameraViewModel(application: Application) : AndroidViewModel(application) {

    // Repository to store camera data
    private val repository = CameraRepository()

    // Private mutable state that only this ViewModel can change
    private val _cameraState = mutableStateOf(
        CameraState(lensFacing = CameraSelector.LENS_FACING_BACK)
    )
    // Public read-only state that the UI can observe
    val cameraState: State<CameraState> = _cameraState

    // Camera controller that manages camera operations
    private val _cameraController = LifecycleCameraController(application).apply {
        // Enable both photo capture and image analysis
        setEnabledUseCases(
            CameraController.IMAGE_CAPTURE or CameraController.IMAGE_ANALYSIS
        )
    }
    // Public access to camera controller for the UI
    val cameraController: LifecycleCameraController = _cameraController

    // Initialize the ViewModel
    init {
        // Set up image analysis to find bright spots
        setupImageAnalysis()
        // Set which camera to use (front or back)
        updateCameraSelector()
    }

    // Set up the image analysis to find bright spots in camera feed
    private fun setupImageAnalysis() {
        _cameraController.setImageAnalysisAnalyzer(
            // Run analysis on main thread
            ContextCompat.getMainExecutor(getApplication())
        ) { imageProxy ->
            // Analyze each camera frame
            analyzeImage(imageProxy)
        }
    }

    // Analyze a single camera frame to find the brightest pixel
    private fun analyzeImage(imageProxy: ImageProxy) {
        // Get pixel data from the camera frame
        val buffer = imageProxy.planes[0].buffer
        val data = ByteArray(buffer.remaining())
        buffer.get(data)

        // Variables to track the brightest pixel
        var maxBrightness = 0
        var brightestX = 0
        var brightestY = 0

        // Get image dimensions
        val width = imageProxy.width
        val height = imageProxy.height

        // Sample every 10th pixel for performance (don't check every single pixel)
        for (y in 0 until height step 10) {
            for (x in 0 until width step 10) {
                // Calculate pixel position in the data array
                val index = y * width + x
                if (index < data.size) {
                    // Get brightness value (0-255)
                    val brightness = data[index].toInt() and 0xFF
                    // Check if this is the brightest pixel we've seen
                    if (brightness > maxBrightness) {
                        maxBrightness = brightness
                        brightestX = x
                        brightestY = y
                    }
                }
            }
        }

        // Convert pixel coordinates to normalized coordinates (0.0 to 1.0)
        val normalizedPoint = Offset(
            brightestX.toFloat() / width,
            brightestY.toFloat() / height
        )

        // Add this point to our repository for storage
        repository.addPointOfInterest(
            PointOfInterest(
                position = normalizedPoint,
                brightness = maxBrightness
            )
        )

        // Update the UI state with the new brightest point
        _cameraState.value = _cameraState.value.copy(
            brightestPoint = normalizedPoint
        )

        // Important: Close the image to free memory
        imageProxy.close()
    }

    // Switch between front and back camera
    fun switchCamera() {
        // Determine which camera to switch to
        val newLensFacing = if (_cameraState.value.lensFacing == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT // Switch to front camera
        } else {
            CameraSelector.LENS_FACING_BACK // Switch to back camera
        }

        // Update the state with new camera
        _cameraState.value = _cameraState.value.copy(lensFacing = newLensFacing)
        // Apply the camera change
        updateCameraSelector()
    }

    // Apply the selected camera (front or back) to the camera controller
    private fun updateCameraSelector() {
        _cameraController.cameraSelector = CameraSelector.Builder()
            .requireLensFacing(_cameraState.value.lensFacing)
            .build()
    }

    // Take a photo and save it to device storage
    fun takePhoto() {
        val context = getApplication<Application>()

        // Show that we're currently taking a photo
        _cameraState.value = _cameraState.value.copy(isCapturing = true)

        // Create a unique filename with current date/time
        val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis())

        // Set up photo metadata
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name) // File name
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg") // File type
            // For newer Android versions, specify folder
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ARCamera")
            }
        }

        // Set up where to save the photo
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        // Actually take the photo
        _cameraController.takePicture(
            outputOptions, // Where to save
            ContextCompat.getMainExecutor(context), // Run callback on main thread
            object : ImageCapture.OnImageSavedCallback {
                // Called if photo capture fails
                override fun onError(exception: ImageCaptureException) {
                    // Hide the capturing indicator
                    _cameraState.value = _cameraState.value.copy(isCapturing = false)
                    // Show error message to user
                    Toast.makeText(context, "Photo capture failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                }

                // Called if photo capture succeeds
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    // Hide the capturing indicator
                    _cameraState.value = _cameraState.value.copy(isCapturing = false)
                    // Show success message to user
                    Toast.makeText(context, "Photo capture succeeded", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    // Connect the camera to the app's lifecycle (so it stops when app is paused)
    fun bindToLifecycle(lifecycleOwner: LifecycleOwner) {
        _cameraController.bindToLifecycle(lifecycleOwner)
    }

    // Get recent bright points we found (for debugging or future features)
    fun getRecentPointsOfInterest(count: Int = 10): List<PointOfInterest> {
        return repository.getRecentPointsOfInterest(count)
    }

    // Clear all stored bright point history
    fun clearPointHistory() {
        repository.clearHistory()
    }
}