package com.example.objectdetection.viewmodel

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
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.example.objectdetection.model.CameraRepository
import com.example.objectdetection.model.CameraState
import com.example.objectdetection.model.DetectionModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CameraViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CameraRepository(application) // Handles AI detection logic

    private val _cameraState = mutableStateOf( // Private state (only ViewModel can modify)
        CameraState(lensFacing = CameraSelector.LENS_FACING_BACK) // Start with back camera
    )
    val cameraState: State<CameraState> = _cameraState // Public read-only state for UI

    private val _cameraController = LifecycleCameraController(application).apply { // Camera controller
        setEnabledUseCases( // Enable photo capture and analysis
            CameraController.IMAGE_CAPTURE or CameraController.IMAGE_ANALYSIS
        )
    }
    val cameraController: LifecycleCameraController = _cameraController // Expose to UI

    init {
        viewModelScope.launch { // Run initialization in background
            repository.initialize() // Initialize AI models
        }
        setupObjectDetection() // Start analyzing camera frames
        updateCameraSelector() // Apply camera settings
    }

    private fun setupObjectDetection() { // Configure frame analysis for object detection
        _cameraController.setImageAnalysisAnalyzer( // Set up analyzer
            ContextCompat.getMainExecutor(getApplication()) // Run on main thread
        ) { imageProxy -> // Process each camera frame
            viewModelScope.launch { // Run detection in background
                analyzeFrame(imageProxy) // Detect objects in frame
            }
        }
    }

    private suspend fun analyzeFrame(imageProxy: ImageProxy) { // Analyze single camera frame
        try {
            val detectedObjects = repository.detectObjects(imageProxy) // Run AI detection
            repository.addDetectionResult(detectedObjects) // Store results

            _cameraState.value = _cameraState.value.copy( // Update UI state
                detectedObjects = detectedObjects, // Show detected objects
                currentModel = repository.getCurrentModel() // Show current AI model
            )
        } catch (e: Exception) {
            e.printStackTrace() // Log error
            _cameraState.value = _cameraState.value.copy( // Clear objects on error
                detectedObjects = emptyList()
            )
        } finally {
            imageProxy.close() // Always close to prevent memory leak
        }
    }

    fun switchDetectionModel() { // Switch between MLKit and EfficientDet
        val currentModel = repository.getCurrentModel() // Get current model
        val newModel = when (currentModel) { // Switch to other model
            DetectionModel.MLKIT -> DetectionModel.EFFICIENTDET // MLKit -> EfficientDet
            DetectionModel.EFFICIENTDET -> DetectionModel.MLKIT // EfficientDet -> MLKit
        }

        repository.switchModel(newModel) // Apply model change

        _cameraState.value = _cameraState.value.copy( // Update UI state
            currentModel = newModel
        )

        val modelName = if (newModel == DetectionModel.MLKIT) "MLKit" else "EfficientDet"
        Toast.makeText(getApplication(), "Switched to $modelName", Toast.LENGTH_SHORT).show() // Notify user
    }

    fun switchCamera() { // Switch between front and back camera
        val newLensFacing = if (_cameraState.value.lensFacing == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT // Back -> Front
        } else {
            CameraSelector.LENS_FACING_BACK // Front -> Back
        }

        _cameraState.value = _cameraState.value.copy(lensFacing = newLensFacing) // Update state
        updateCameraSelector() // Apply camera change
    }

    private fun updateCameraSelector() { // Apply camera selection to controller
        _cameraController.cameraSelector = CameraSelector.Builder()
            .requireLensFacing(_cameraState.value.lensFacing) // Set which camera to use
            .build()
    }

    fun takePhoto() { // Capture and save photo
        val context = getApplication<Application>()

        _cameraState.value = _cameraState.value.copy(isCapturing = true) // Show capturing state

        val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US) // Create unique filename
            .format(System.currentTimeMillis())

        val contentValues = ContentValues().apply { // Photo metadata
            put(MediaStore.MediaColumns.DISPLAY_NAME, name) // Filename
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg") // File type
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) { // For newer Android
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ObjectDetection") // Save folder
            }
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder( // Where to save
            context.contentResolver, // Content resolver
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // Photos folder
            contentValues // Metadata
        ).build()

        _cameraController.takePicture( // Actually take photo
            outputOptions, // Save options
            ContextCompat.getMainExecutor(context), // Run callback on main thread
            object : ImageCapture.OnImageSavedCallback { // Photo result callback
                override fun onError(exception: ImageCaptureException) { // Photo failed
                    _cameraState.value = _cameraState.value.copy(isCapturing = false) // Hide capturing state
                    Toast.makeText(context, "Photo failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) { // Photo succeeded
                    _cameraState.value = _cameraState.value.copy(isCapturing = false) // Hide capturing state
                    Toast.makeText(context, "Photo saved!", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    fun bindToLifecycle(lifecycleOwner: LifecycleOwner) { // Connect camera to app lifecycle
        _cameraController.bindToLifecycle(lifecycleOwner) // Camera follows app lifecycle
    }

    override fun onCleared() { // Clean up when ViewModel destroyed
        super.onCleared()
        repository.cleanup() // Close AI models
    }
}