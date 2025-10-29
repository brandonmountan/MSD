package com.example.objectdetection.viewmodel

// Import Android application context
import android.app.Application
// Import Compose state management
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
// Import Android ViewModel base class
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
// Import our API client
import com.example.objectdetection.network.ApiClient
import com.example.objectdetection.network.PhotoInfo
// Import coroutines for async operations
import kotlinx.coroutines.launch

// State for gallery UI
// Define a data class to hold all gallery-related state
data class GalleryState(
    // List of photos to display
    val photos: List<PhotoInfo> = emptyList(),
    // Whether we're currently loading photos from the server
    val isLoading: Boolean = false,
    // Any error message to display
    val error: String? = null,
    // Authentication headers needed to load images
    val authHeaders: Map<String, String> = emptyMap()
)

// ViewModel to manage gallery logic and state
class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    // Create an instance of the API client
    private val apiClient = ApiClient(application)

    // Private mutable state that can only be changed within this ViewModel
    private val _galleryState = mutableStateOf(GalleryState())
    // Public read-only state that the UI can observe
    val galleryState: State<GalleryState> = _galleryState

    // Function to load photos from the server
    fun loadPhotos() {
        // Launch a coroutine in the viewModelScope
        viewModelScope.launch {
            // Update state to show we're loading and clear any previous errors
            _galleryState.value = _galleryState.value.copy(
                isLoading = true,
                error = null
            )

            // Call the API to get the list of photos
            val result = apiClient.getPhotos()
            // Get the authentication headers needed for loading images
            val authHeaders = apiClient.getAuthHeaders()

            // Update state based on the result
            _galleryState.value = if (result.isSuccess) {
                // If successful, update state with the photos and auth headers
                _galleryState.value.copy(
                    isLoading = false,
                    photos = result.getOrNull() ?: emptyList(),
                    authHeaders = authHeaders
                )
            } else {
                // If failed, update state with the error message
                _galleryState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }

    // Function to get the URL for a specific photo
    fun getPhotoUrl(photoId: String): String {
        // Use the API client to build the photo URL
        return apiClient.getPhotoUrl(photoId)
    }
}