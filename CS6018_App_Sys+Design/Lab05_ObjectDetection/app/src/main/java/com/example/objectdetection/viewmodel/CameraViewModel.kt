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
import com.example.objectdetection.network.ApiClient
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class CameraViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CameraRepository(application)
    private val apiClient = ApiClient(application) // NEW: API client for uploads

    private val _cameraState = mutableStateOf(
        CameraState(lensFacing = CameraSelector.LENS_FACING_BACK)
    )
    val cameraState: State<CameraState> = _cameraState

    private val _cameraController = LifecycleCameraController(application).apply {
        setEnabledUseCases(
            CameraController.IMAGE_CAPTURE or CameraController.IMAGE_ANALYSIS
        )
    }
    val cameraController: LifecycleCameraController = _cameraController

    init {
        viewModelScope.launch {
            repository.initialize()
        }
        setupObjectDetection()
        updateCameraSelector()
    }

    private fun setupObjectDetection() {
        _cameraController.setImageAnalysisAnalyzer(
            ContextCompat.getMainExecutor(getApplication())
        ) { imageProxy ->
            viewModelScope.launch {
                analyzeFrame(imageProxy)
            }
        }
    }

    private suspend fun analyzeFrame(imageProxy: ImageProxy) {
        try {
            val detectedObjects = repository.detectObjects(imageProxy)
            repository.addDetectionResult(detectedObjects)

            _cameraState.value = _cameraState.value.copy(
                detectedObjects = detectedObjects,
                currentModel = repository.getCurrentModel()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            _cameraState.value = _cameraState.value.copy(
                detectedObjects = emptyList()
            )
        } finally {
            imageProxy.close()
        }
    }

    fun switchDetectionModel() {
        val currentModel = repository.getCurrentModel()
        val newModel = when (currentModel) {
            DetectionModel.MLKIT -> DetectionModel.EFFICIENTDET
            DetectionModel.EFFICIENTDET -> DetectionModel.MLKIT
        }

        repository.switchModel(newModel)

        _cameraState.value = _cameraState.value.copy(
            currentModel = newModel
        )

        val modelName = if (newModel == DetectionModel.MLKIT) "MLKit" else "EfficientDet"
        Toast.makeText(getApplication(), "Switched to $modelName", Toast.LENGTH_SHORT).show()
    }

    fun switchCamera() {
        val newLensFacing = if (_cameraState.value.lensFacing == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }

        _cameraState.value = _cameraState.value.copy(lensFacing = newLensFacing)
        updateCameraSelector()
    }

    private fun updateCameraSelector() {
        _cameraController.cameraSelector = CameraSelector.Builder()
            .requireLensFacing(_cameraState.value.lensFacing)
            .build()
    }

    fun takePhoto() {
        val context = getApplication<Application>()

        _cameraState.value = _cameraState.value.copy(isCapturing = true)

        val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis())

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ObjectDetection")
            }
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        _cameraController.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    _cameraState.value = _cameraState.value.copy(isCapturing = false)
                    Toast.makeText(context, "Photo failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    // NEW: Upload photo to server after saving locally
                    uploadPhotoToServer(output.savedUri)
                }
            }
        )
    }

    // NEW: Upload photo to server
    private fun uploadPhotoToServer(imageUri: android.net.Uri?) {
        val context = getApplication<Application>()

        viewModelScope.launch {
            try {
                if (imageUri == null) {
                    _cameraState.value = _cameraState.value.copy(isCapturing = false)
                    Toast.makeText(context, "Photo saved locally", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Read photo bytes from URI
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val photoBytes = inputStream?.readBytes()
                inputStream?.close()

                if (photoBytes == null) {
                    _cameraState.value = _cameraState.value.copy(isCapturing = false)
                    Toast.makeText(context, "Photo saved locally", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Upload to server
                val result = apiClient.uploadPhoto(photoBytes)

                _cameraState.value = _cameraState.value.copy(isCapturing = false)

                if (result.isSuccess) {
                    Toast.makeText(context, "Photo saved and uploaded!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Photo saved locally. Upload failed.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                _cameraState.value = _cameraState.value.copy(isCapturing = false)
                Toast.makeText(context, "Photo saved locally. Upload error.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun bindToLifecycle(lifecycleOwner: LifecycleOwner) {
        _cameraController.bindToLifecycle(lifecycleOwner)
    }

    override fun onCleared() {
        super.onCleared()
        repository.cleanup()
    }
}