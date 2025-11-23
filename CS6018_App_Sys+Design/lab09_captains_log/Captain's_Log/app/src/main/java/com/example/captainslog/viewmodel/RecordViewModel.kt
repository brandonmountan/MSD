package com.example.captainslog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.captainslog.model.UploadState
import com.example.captainslog.network.ApiClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

/**
 * ViewModel for recording screen
 * Manages audio recording, timing, and uploading
 */
class RecordViewModel(private val apiClient: ApiClient) : ViewModel() {

    // Recording state
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    // Recording duration in seconds
    private val _recordingDuration = MutableStateFlow(0)
    val recordingDuration: StateFlow<Int> = _recordingDuration.asStateFlow()

    // Upload status
    private val _uploadStatus = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadStatus: StateFlow<UploadState> = _uploadStatus.asStateFlow()

    // Store recorded audio bytes
    private var recordedAudio: ByteArray? = null

    /**
     * Start recording audio
     */
    fun startRecording() {
        _isRecording.value = true
        _recordingDuration.value = 0

        // Start timer
        viewModelScope.launch {
            while (_isRecording.value) {
                delay(1000)
                _recordingDuration.value += 1
            }
        }

        // TODO: Start actual audio recording
        // For prototype, we simulate recording
    }

    /**
     * Stop recording audio
     */
    fun stopRecording() {
        _isRecording.value = false

        // TODO: Stop actual audio recording and get bytes
        // For prototype, we create dummy audio data
        recordedAudio = ByteArray(1000) // Dummy audio data
    }

    /**
     * Save the recording with a title
     */
    fun save(title: String) {
        val audio = recordedAudio
        if (audio == null) {
            _uploadStatus.value = UploadState.Error("No recording available")
            return
        }

        _uploadStatus.value = UploadState.Uploading

        viewModelScope.launch {
            // Generate stardate
            val stardate = generateStardate()

            // For prototype: use empty transcription
            // In production, you would call the ML server here
            val transcription = ""

            val result = apiClient.uploadLog(
                audioBytes = audio,
                title = title,
                transcription = transcription,
                stardate = stardate
            )

            _uploadStatus.value = if (result.isSuccess) {
                UploadState.Success
            } else {
                UploadState.Error(result.exceptionOrNull()?.message ?: "Upload failed")
            }

            // Reset after delay
            if (_uploadStatus.value is UploadState.Success) {
                delay(1000)
                resetState()
            }
        }
    }

    /**
     * Generate Star Trek style stardate
     */
    private fun generateStardate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        return String.format("%d.%03d%02d", year, dayOfYear, hour * 60 + minute)
    }

    /**
     * Reset the recording state
     */
    fun resetState() {
        _isRecording.value = false
        _recordingDuration.value = 0
        _uploadStatus.value = UploadState.Idle
        recordedAudio = null
    }
}