package com.example.objectdetection.viewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.objectdetection.network.ApiClient
import com.example.objectdetection.network.PhotoInfo
import kotlinx.coroutines.launch

// State for gallery UI
data class GalleryState(
    val photos: List<PhotoInfo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val authHeaders: Map<String, String> = emptyMap()
)

class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    private val apiClient = ApiClient(application)

    private val _galleryState = mutableStateOf(GalleryState())
    val galleryState: State<GalleryState> = _galleryState

    fun loadPhotos() {
        viewModelScope.launch {
            _galleryState.value = _galleryState.value.copy(
                isLoading = true,
                error = null
            )

            val result = apiClient.getPhotos()
            val authHeaders = apiClient.getAuthHeaders()

            _galleryState.value = if (result.isSuccess) {
                _galleryState.value.copy(
                    isLoading = false,
                    photos = result.getOrNull() ?: emptyList(),
                    authHeaders = authHeaders
                )
            } else {
                _galleryState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun getPhotoUrl(photoId: String): String {
        return apiClient.getPhotoUrl(photoId)
    }
}