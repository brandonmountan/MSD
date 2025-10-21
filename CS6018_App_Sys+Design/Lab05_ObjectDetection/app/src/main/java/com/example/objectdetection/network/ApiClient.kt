package com.example.objectdetection.network

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable

// DataStore for storing auth token
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

// Data models matching server
@Serializable
data class UserCredentials(val username: String, val password: String)

@Serializable
data class LoginResponse(val success: Boolean, val token: String? = null, val message: String? = null)

@Serializable
data class PhotoInfo(val id: String, val filename: String, val timestamp: Long)

@Serializable
data class UploadResponse(val success: Boolean, val photoId: String? = null)

class ApiClient(private val context: Context) {

    // CHANGE THIS TO YOUR SERVER IP ADDRESS (find it with ipconfig/ifconfig)
    // If testing on emulator, use 10.0.2.2 (emulator's localhost)
    // If testing on real device, use your computer's local IP (e.g., "155.98.131.3")
    private val BASE_URL = "http://155.98.131.3:8080" // Change this!

    private val TOKEN_KEY = stringPreferencesKey("auth_token")

    // HTTP client for making network requests
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json()
        }
        install(Logging) {
            level = LogLevel.INFO
        }
    }

    // Save auth token to DataStore
    private suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    // Get auth token from DataStore
    suspend fun getToken(): String? {
        return context.dataStore.data.first()[TOKEN_KEY]
    }

    // Check if user is logged in
    suspend fun isLoggedIn(): Boolean {
        return getToken() != null
    }

    // Clear auth token (logout)
    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }

    // Register new user
    suspend fun register(username: String, password: String): Result<String> {
        return try {
            val response: LoginResponse = client.post("$BASE_URL/register") {
                contentType(ContentType.Application.Json)
                setBody(UserCredentials(username, password))
            }.body()

            if (response.success) {
                Result.success("Registration successful! Please login.")
            } else {
                Result.failure(Exception(response.message ?: "Registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Login user
    suspend fun login(username: String, password: String): Result<String> {
        return try {
            val response: LoginResponse = client.post("$BASE_URL/login") {
                contentType(ContentType.Application.Json)
                setBody(UserCredentials(username, password))
            }.body()

            if (response.success && response.token != null) {
                saveToken(response.token) // Save token for future requests
                Result.success("Login successful!")
            } else {
                Result.failure(Exception(response.message ?: "Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Connection error: ${e.message}"))
        }
    }

    // Logout user
    suspend fun logout(): Result<Unit> {
        return try {
            val token = getToken()
            if (token != null) {
                client.post("$BASE_URL/logout") {
                    header("Authorization", "Bearer $token")
                }
            }
            clearToken()
            Result.success(Unit)
        } catch (e: Exception) {
            clearToken() // Clear token even if request fails
            Result.success(Unit)
        }
    }

    // Upload photo to server
    suspend fun uploadPhoto(photoBytes: ByteArray): Result<String> {
        return try {
            val token = getToken() ?: return Result.failure(Exception("Not logged in"))

            val response: UploadResponse = client.post("$BASE_URL/photos/upload") {
                header("Authorization", "Bearer $token")
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("photo", photoBytes, Headers.build {
                                append(HttpHeaders.ContentType, "image/jpeg")
                                append(HttpHeaders.ContentDisposition, "filename=photo.jpg")
                            })
                        }
                    )
                )
            }.body()

            if (response.success) {
                Result.success("Photo uploaded successfully!")
            } else {
                Result.failure(Exception("Upload failed"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Upload error: ${e.message}"))
        }
    }

    // Get list of user's photos
    suspend fun getPhotos(): Result<List<PhotoInfo>> {
        return try {
            val token = getToken() ?: return Result.failure(Exception("Not logged in"))

            val photos: List<PhotoInfo> = client.get("$BASE_URL/photos") {
                header("Authorization", "Bearer $token")
            }.body()

            Result.success(photos)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to load photos: ${e.message}"))
        }
    }

    // Get URL for displaying a photo (used with Coil image loader)
    fun getPhotoUrl(photoId: String): String {
        return "$BASE_URL/photos/$photoId"
    }

    // Get auth headers for image loading
    suspend fun getAuthHeaders(): Map<String, String> {
        val token = getToken()
        return if (token != null) {
            mapOf("Authorization" to "Bearer $token")
        } else {
            emptyMap()
        }
    }
}