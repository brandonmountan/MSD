package com.example.objectdetection.viewmodel

// Import Android application context and content storage
import android.app.Application
import android.content.ContentValues
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
// Import CameraX libraries for camera functionality
import androidx.camera.core.*
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
// Import Compose state management
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
// Import utilities for threading
import androidx.core.content.ContextCompat
// Import Android ViewModel and lifecycle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
// Import our model classes
import com.example.objectdetection.model.CameraRepository
import com.example.objectdetection.model.CameraState
import com.example.objectdetection.model.DetectionModel
// Import our API client
import com.example.objectdetection.network.ApiClient
// Import coroutines for async operations
import kotlinx.coroutines.launch
// Import utilities for date formatting
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

// ViewModel to manage camera functionality and object detection
class CameraViewModel(application: Application) : AndroidViewModel(application) {

    // Repository that handles object detection logic
    private val repository = CameraRepository(application)
    // API client for uploading photos to server
    private val apiClient = ApiClient(application) // NEW: API client for uploads

    // Private mutable state for camera settings
    private val _cameraState = mutableStateOf(
        // Initialize with back camera
        CameraState(lensFacing = CameraSelector.LENS_FACING_BACK)
    )
    // Public read-only state that the UI can observe
    val cameraState: State<CameraState> = _cameraState

    // Camera controller that manages the camera
    private val _cameraController = LifecycleCameraController(application).apply {
        // Enable both image capture and image analysis
        setEnabledUseCases(
            CameraController.IMAGE_CAPTURE or CameraController.IMAGE_ANALYSIS
        )
    }
    // Public camera controller for the UI to use
    val cameraController: LifecycleCameraController = _cameraController

    // This block runs when the ViewModel is created
    init {
        // Launch a coroutine to initialize the repository
        viewModelScope.launch {
            repository.initialize()
        }
        // Set up object detection on camera frames
        setupObjectDetection()
        // Apply the initial camera settings
        updateCameraSelector()
    }

    // Function to set up real-time object detection
    private fun setupObjectDetection() {
        // Set an analyzer that will process each camera frame
        _cameraController.setImageAnalysisAnalyzer(
            // Use the main thread executor
            ContextCompat.getMainExecutor(getApplication())
        ) { imageProxy ->
            // For each frame, launch a coroutine to analyze it
            viewModelScope.launch {
                analyzeFrame(imageProxy)
            }
        }
    }

    // Function to analyze a single camera frame for objects
    private suspend fun analyzeFrame(imageProxy: ImageProxy) {
        try {
            // Use the repository to detect objects in this frame
            val detectedObjects = repository.detectObjects(imageProxy)
            // Add the detection results to history
            repository.addDetectionResult(detectedObjects)

            // Update state with the detected objects
            _cameraState.value = _cameraState.value.copy(
                detectedObjects = detectedObjects,
                currentModel = repository.getCurrentModel()
            )
        } catch (e: Exception) {
            // If detection fails, print the error
            e.printStackTrace()
            // Update state to show no objects detected
            _cameraState.value = _cameraState.value.copy(
                detectedObjects = emptyList()
            )
        } finally {
            // Always close the image proxy to free up resources
            imageProxy.close()
        }
    }

    // Function to switch between different object detection models
    fun switchDetectionModel() {
        // Get the current model
        val currentModel = repository.getCurrentModel()
        // Determine the new model (toggle between MLKit and EfficientDet)
        val newModel = when (currentModel) {
            DetectionModel.MLKIT -> DetectionModel.EFFICIENTDET
            DetectionModel.EFFICIENTDET -> DetectionModel.MLKIT
        }

        // Tell the repository to switch to the new model
        repository.switchModel(newModel)

        // Update the state with the new model
        _cameraState.value = _cameraState.value.copy(
            currentModel = newModel
        )

        // Get the model name for display
        val modelName = if (newModel == DetectionModel.MLKIT) "MLKit" else "EfficientDet"
        // Show a toast message to inform the user
        Toast.makeText(getApplication(), "Switched to $modelName", Toast.LENGTH_SHORT).show()
    }

    // Function to switch between front and back camera
    fun switchCamera() {
        // Determine the new camera (toggle between back and front)
        val newLensFacing = if (_cameraState.value.lensFacing == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }

        // Update the state with the new camera
        _cameraState.value = _cameraState.value.copy(lensFacing = newLensFacing)
        // Apply the camera change
        updateCameraSelector()
    }

    // Function to update which camera is being used
    private fun updateCameraSelector() {
        // Build a new camera selector with the current lens facing
        _cameraController.cameraSelector = CameraSelector.Builder()
            .requireLensFacing(_cameraState.value.lensFacing)
            .build()
    }

    // Function to take a photo
    fun takePhoto() {
        // Get the application context
        val context = getApplication<Application>()

        // Update state to show we're capturing
        _cameraState.value = _cameraState.value.copy(isCapturing = true)

        // Create a filename based on the current date and time
        val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis())

        // Create content values for storing the photo in the gallery
        val contentValues = ContentValues().apply {
            // Set the display name
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            // Set the file type as JPEG
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            // For newer Android versions, specify the folder
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ObjectDetection")
            }
        }

        // Create output options specifying where to save the photo
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            // Use the content resolver to save to the gallery
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        // Take the picture
        _cameraController.takePicture(
            // Use the output options we created
            outputOptions,
            // Use the main thread executor
            ContextCompat.getMainExecutor(context),
            // Provide a callback for when the photo is saved
            object : ImageCapture.OnImageSavedCallback {
                // Called if photo capture fails
                override fun onError(exception: ImageCaptureException) {
                    // Update state to show we're done capturing
                    _cameraState.value = _cameraState.value.copy(isCapturing = false)
                    // Show error message to user
                    Toast.makeText(context, "Photo failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                }

                // Called when photo is successfully saved
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    // NEW: Upload photo to server after saving locally
                    uploadPhotoToServer(output.savedUri)
                }
            }
        )
    }

    // NEW: Upload photo to server
    // Function to upload a photo to the server after it's been saved locally
    private fun uploadPhotoToServer(imageUri: android.net.Uri?) {
        // Get the application context
        val context = getApplication<Application>()

        // Launch a coroutine for the upload
        viewModelScope.launch {
            try {
                // Check if we have a valid URI
                if (imageUri == null) {
                    // Update state to show we're done capturing
                    _cameraState.value = _cameraState.value.copy(isCapturing = false)
                    // Show message that photo was only saved locally
                    Toast.makeText(context, "Photo saved locally", Toast.LENGTH_SHORT).show()
                    // Exit the function
                    return@launch
                }

                // Read photo bytes from URI
                // Open an input stream to read the photo file
                val inputStream = context.contentResolver.openInputStream(imageUri)
                // Read all bytes from the stream
                val photoBytes = inputStream?.readBytes()
                // Close the input stream
                inputStream?.close()

                // Check if we successfully read the photo bytes
                if (photoBytes == null) {
                    // Update state to show we're done capturing
                    _cameraState.value = _cameraState.value.copy(isCapturing = false)
                    // Show message that photo was only saved locally
                    Toast.makeText(context, "Photo saved locally", Toast.LENGTH_SHORT).show()
                    // Exit the function
                    return@launch
                }

                // Upload to server
                // Call the API to upload the photo
                val result = apiClient.uploadPhoto(photoBytes)

                // Update state to show we're done capturing
                _cameraState.value = _cameraState.value.copy(isCapturing = false)

                // Check if upload was successful
                if (result.isSuccess) {
                    // Show success message
                    Toast.makeText(context, "Photo saved and uploaded!", Toast.LENGTH_SHORT).show()
                } else {
                    // Show message that photo was saved locally but upload failed
                    Toast.makeText(context, "Photo saved locally. Upload failed.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // If any error occurs during upload
                // Update state to show we're done capturing
                _cameraState.value = _cameraState.value.copy(isCapturing = false)
                // Show message that photo was saved locally but upload had error
                Toast.makeText(context, "Photo saved locally. Upload error.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to bind the camera to the lifecycle
    fun bindToLifecycle(lifecycleOwner: LifecycleOwner) {
        // Bind the camera controller to the activity/fragment lifecycle
        _cameraController.bindToLifecycle(lifecycleOwner)
    }

    // Called when the ViewModel is being destroyed
    override fun onCleared() {
        // Call the parent's onCleared
        super.onCleared()
        // Clean up the repository resources
        repository.cleanup()
    }
}