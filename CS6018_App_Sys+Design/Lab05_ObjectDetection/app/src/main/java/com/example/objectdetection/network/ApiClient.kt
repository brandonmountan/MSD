package com.example.objectdetection.network

// Import Android context for accessing app resources
import android.content.Context
// Import DataStore for secure local storage
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
// Import Ktor HTTP client libraries
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
// Import coroutines for async operations
import kotlinx.coroutines.flow.first
// Import serialization for JSON conversion
import kotlinx.serialization.Serializable

// DataStore for storing auth token
// Create a DataStore extension property on Context for storing authentication data
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

// IMPORTANT: All data classes sent/received as JSON must be @Serializable
// Define a data class for sending username and password to the server
@Serializable
data class UserCredentials(val username: String, val password: String)

// Define a data class for receiving login response from the server
@Serializable
data class LoginResponse(val success: Boolean, val token: String? = null, val message: String? = null)

// Define a data class for receiving photo information from the server
@Serializable
data class PhotoInfo(val id: String, val filename: String, val timestamp: Long)

// Define a data class for receiving upload response from the server
@Serializable
data class UploadResponse(val success: Boolean, val photoId: String? = null)

// Main API client class for communicating with the server
class ApiClient(private val context: Context) {

    // CHANGE THIS TO YOUR SERVER IP ADDRESS (find it with ipconfig/ifconfig)
    // If testing on emulator, use 10.0.2.2 (emulator's localhost)
    // If testing on real device, use your computer's local IP (e.g., "192.168.1.100")
    // The base URL where the server is running
    private val BASE_URL = "http://10.0.2.2:8080" // Change this!

    // Key for storing the authentication token in DataStore
    private val TOKEN_KEY = stringPreferencesKey("auth_token")

    // HTTP client for making network requests
    // Create and configure the HTTP client
    private val client = HttpClient(Android) {
        // Install plugin to handle JSON serialization/deserialization
        install(ContentNegotiation) {
            // Enable JSON support
            json()
        }
        // Install plugin to log HTTP requests and responses
        install(Logging) {
            // Set logging level to INFO to see request details
            level = LogLevel.INFO
        }
    }

    // Save auth token to DataStore
    // Function to save the authentication token to secure storage
    private suspend fun saveToken(token: String) {
        // Edit the DataStore preferences
        context.dataStore.edit { preferences ->
            // Store the token with our key
            preferences[TOKEN_KEY] = token
        }
    }

    // Get auth token from DataStore
    // Function to retrieve the stored authentication token
    suspend fun getToken(): String? {
        // Get the first value from DataStore and return the token (or null if not found)
        return context.dataStore.data.first()[TOKEN_KEY]
    }

    // Check if user is logged in
    // Function to check if the user has a valid token (is logged in)
    suspend fun isLoggedIn(): Boolean {
        // Return true if token exists, false if it doesn't
        return getToken() != null
    }

    // Clear auth token (logout)
    // Function to remove the authentication token from storage
    suspend fun clearToken() {
        // Edit the DataStore preferences
        context.dataStore.edit { preferences ->
            // Remove the token
            preferences.remove(TOKEN_KEY)
        }
    }

    // Register new user
    // Function to create a new user account on the server
    suspend fun register(username: String, password: String): Result<String> {
        // Use try-catch to handle any errors
        return try {
            // Make a POST request to the register endpoint
            val response: LoginResponse = client.post("$BASE_URL/register") {
                // Set the content type to JSON
                contentType(ContentType.Application.Json)
                // Send the username and password in the request body
                setBody(UserCredentials(username, password))
            }.body() // Parse the response body as LoginResponse

            // Check if registration was successful
            if (response.success) {
                // Return success with a message
                Result.success("Registration successful! Please login.")
            } else {
                // Return failure with the error message from server
                Result.failure(Exception(response.message ?: "Registration failed"))
            }
        } catch (e: Exception) {
            // If any error occurs, return failure with the exception
            Result.failure(e)
        }
    }

    // Login user
    // Function to authenticate a user with username and password
    suspend fun login(username: String, password: String): Result<String> {
        // Use try-catch to handle any errors
        return try {
            // Make a POST request to the login endpoint
            val response: LoginResponse = client.post("$BASE_URL/login") {
                // Set the content type to JSON
                contentType(ContentType.Application.Json)
                // Send the username and password in the request body
                setBody(UserCredentials(username, password))
            }.body() // Parse the response body as LoginResponse

            // Check if login was successful and we received a token
            if (response.success && response.token != null) {
                saveToken(response.token) // Save token for future requests
                // Return success with a message
                Result.success("Login successful!")
            } else {
                // Return failure with the error message from server
                Result.failure(Exception(response.message ?: "Login failed"))
            }
        } catch (e: Exception) {
            // If any error occurs (like network error), return failure
            Result.failure(Exception("Connection error: ${e.message}"))
        }
    }

    // Logout user
    // Function to log out the user and clear their session
    suspend fun logout(): Result<Unit> {
        // Use try-catch to handle any errors
        return try {
            // Get the current authentication token
            val token = getToken()
            // If we have a token, notify the server we're logging out
            if (token != null) {
                // Make a POST request to the logout endpoint
                client.post("$BASE_URL/logout") {
                    // Include the token in the Authorization header
                    header("Authorization", "Bearer $token")
                }
            }
            // Clear the token from local storage
            clearToken()
            // Return success
            Result.success(Unit)
        } catch (e: Exception) {
            clearToken() // Clear token even if request fails
            // Still return success since we cleared the local token
            Result.success(Unit)
        }
    }

    // Upload photo to server
    // Function to upload a photo to the server
    suspend fun uploadPhoto(photoBytes: ByteArray): Result<String> {
        // Use try-catch to handle any errors
        return try {
            // Get the authentication token, or return error if not logged in
            val token = getToken() ?: return Result.failure(Exception("Not logged in"))

            // Make a POST request to the photo upload endpoint
            val response: UploadResponse = client.post("$BASE_URL/photos/upload") {
                // Include the authentication token in the header
                header("Authorization", "Bearer $token")
                // Set the request body as multipart form data
                setBody(
                    // Create multipart form data content
                    MultiPartFormDataContent(
                        // Build the form data
                        formData {
                            // Add the photo bytes as a form field named "photo"
                            append("photo", photoBytes, Headers.build {
                                // Specify the content type as JPEG image
                                append(HttpHeaders.ContentType, "image/jpeg")
                                // Specify the filename
                                append(HttpHeaders.ContentDisposition, "filename=photo.jpg")
                            })
                        }
                    )
                )
            }.body() // Parse the response body as UploadResponse

            // Check if upload was successful
            if (response.success) {
                // Return success message
                Result.success("Photo uploaded successfully!")
            } else {
                // Return failure message
                Result.failure(Exception("Upload failed"))
            }
        } catch (e: Exception) {
            // If any error occurs, return failure with error details
            Result.failure(Exception("Upload error: ${e.message}"))
        }
    }

    // Get list of user's photos
    // Function to retrieve a list of all photos for the logged-in user
    suspend fun getPhotos(): Result<List<PhotoInfo>> {
        // Use try-catch to handle any errors
        return try {
            // Get the authentication token, or return error if not logged in
            val token = getToken() ?: return Result.failure(Exception("Not logged in"))

            // Make a GET request to the photos endpoint
            val photos: List<PhotoInfo> = client.get("$BASE_URL/photos") {
                // Include the authentication token in the header
                header("Authorization", "Bearer $token")
            }.body() // Parse the response body as a list of PhotoInfo

            // Return the list of photos
            Result.success(photos)
        } catch (e: Exception) {
            // If any error occurs, return failure with error details
            Result.failure(Exception("Failed to load photos: ${e.message}"))
        }
    }

    // Get URL for displaying a photo (used with Coil image loader)
    // Function to build the URL for accessing a specific photo
    fun getPhotoUrl(photoId: String): String {
        // Return the full URL to the photo
        return "$BASE_URL/photos/$photoId"
    }

    // Get auth headers for image loading
    // Function to get authentication headers for loading images
    suspend fun getAuthHeaders(): Map<String, String> {
        // Get the current authentication token
        val token = getToken()
        // If we have a token, return it in a map for the Authorization header
        return if (token != null) {
            mapOf("Authorization" to "Bearer $token")
        } else {
            // If no token, return empty map
            emptyMap()
        }
    }
}