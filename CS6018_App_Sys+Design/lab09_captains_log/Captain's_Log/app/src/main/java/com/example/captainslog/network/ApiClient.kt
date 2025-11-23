package com.example.captainslog.network

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.captainslog.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.first

// Extension property for DataStore to persist auth token
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

/**
 * API Client for communicating with the Captain's Log backend server
 * Handles authentication, log management, friends, and search
 *
 * @property context Android context for DataStore access
 */
class ApiClient(private val context: Context) {

    // IMPORTANT: Change this to your server IP address
    // For Android Emulator: use 10.0.2.2 (maps to localhost on host machine)
    // For real device: use your computer's local IP (e.g., "192.168.1.100")
    private val BASE_URL = "http://10.0.2.2:8080"

    // URL for ML transcription server
    private val ML_SERVER_URL = "http://10.0.2.2:5000"

    // Key for storing auth token in DataStore
    private val TOKEN_KEY = stringPreferencesKey("auth_token")

    // HTTP client for making network requests with JSON serialization
    private val client = HttpClient(Android) {
        // Install JSON content negotiation for automatic serialization
        install(ContentNegotiation) {
            json()
        }
        // Install logging to see network requests in Logcat
        install(Logging) {
            level = LogLevel.INFO
        }
    }

    /**
     * Save authentication token to DataStore (persistent storage)
     *
     * @param token The authentication token to save
     */
    private suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    /**
     * Get authentication token from DataStore
     *
     * @return The stored token or null if not logged in
     */
    suspend fun getToken(): String? {
        return context.dataStore.data.first()[TOKEN_KEY]
    }

    /**
     * Check if user is currently logged in
     *
     * @return true if user has a valid token
     */
    suspend fun isLoggedIn(): Boolean {
        return getToken() != null
    }

    /**
     * Clear authentication token (logout)
     */
    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }

    /**
     * Register a new user account
     *
     * @param username Username for new account
     * @param password Password for new account
     * @return Result with success message or error
     */
    suspend fun register(username: String, password: String): Result<String> {
        return try {
            // Make POST request to /register endpoint
            val response: LoginResponse = client.post("$BASE_URL/register") {
                contentType(ContentType.Application.Json)
                setBody(UserCredentials(username, password))
            }.body()

            // Check if registration was successful
            if (response.success) {
                Result.success("Registration successful! Please login.")
            } else {
                Result.failure(Exception(response.message ?: "Registration failed"))
            }
        } catch (e: Exception) {
            // Handle network or other errors
            Result.failure(e)
        }
    }

    /**
     * Login with username and password
     *
     * @param username User's username
     * @param password User's password
     * @return Result with success message or error
     */
    suspend fun login(username: String, password: String): Result<String> {
        return try {
            // Make POST request to /login endpoint
            val response: LoginResponse = client.post("$BASE_URL/login") {
                contentType(ContentType.Application.Json)
                setBody(UserCredentials(username, password))
            }.body()

            // Check if login was successful and token was returned
            if (response.success && response.token != null) {
                // Save token for future authenticated requests
                saveToken(response.token)
                Result.success("Login successful!")
            } else {
                Result.failure(Exception(response.message ?: "Login failed"))
            }
        } catch (e: Exception) {
            // Handle network or authentication errors
            Result.failure(Exception("Connection error: ${e.message}"))
        }
    }

    /**
     * Logout current user
     *
     * @return Result with success or error
     */
    suspend fun logout(): Result<Unit> {
        return try {
            val token = getToken()
            if (token != null) {
                // Notify server of logout
                client.post("$BASE_URL/logout") {
                    header("Authorization", "Bearer $token")
                }
            }
            // Clear local token regardless of server response
            clearToken()
            Result.success(Unit)
        } catch (e: Exception) {
            // Clear token even if request fails
            clearToken()
            Result.success(Unit)
        }
    }

    /**
     * Upload a new log entry with audio file
     *
     * @param audioBytes Raw audio file bytes
     * @param title Title for the log entry
     * @param transcription Text transcription of the audio
     * @param stardate Star Trek style stardate
     * @return Result with success message or error
     */
    suspend fun uploadLog(
        audioBytes: ByteArray,
        title: String,
        transcription: String,
        stardate: String
    ): Result<String> {
        return try {
            // Get authentication token
            val token = getToken() ?: return Result.failure(Exception("Not logged in"))

            // Create multipart form data with audio and metadata
            val response: UploadLogResponse = client.post("$BASE_URL/logs/upload") {
                header("Authorization", "Bearer $token")
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            // Add audio file
                            append("audio", audioBytes, Headers.build {
                                append(HttpHeaders.ContentType, "audio/mp4")
                                append(HttpHeaders.ContentDisposition, "filename=log.m4a")
                            })
                            // Add text fields
                            append("title", title)
                            append("transcription", transcription)
                            append("stardate", stardate)
                        }
                    )
                )
            }.body()

            // Check upload result
            if (response.success) {
                Result.success("Log uploaded successfully!")
            } else {
                Result.failure(Exception(response.message ?: "Upload failed"))
            }
        } catch (e: Exception) {
            // Handle network or upload errors
            Result.failure(Exception("Upload error: ${e.message}"))
        }
    }

    /**
     * Get all log entries for the current user
     *
     * @return Result with list of log entries or error
     */
    suspend fun getLogs(): Result<List<LogEntry>> {
        return try {
            // Get authentication token
            val token = getToken() ?: return Result.failure(Exception("Not logged in"))

            // Make GET request to /logs endpoint
            val logs: List<LogEntry> = client.get("$BASE_URL/logs") {
                header("Authorization", "Bearer $token")
            }.body()

            Result.success(logs)
        } catch (e: Exception) {
            // Handle network errors
            Result.failure(Exception("Failed to load logs: ${e.message}"))
        }
    }

    /**
     * Get URL for streaming audio file for a log entry
     *
     * @param logId ID of the log entry
     * @return URL string for audio playback
     */
    fun getAudioUrl(logId: String): String {
        return "$BASE_URL/logs/$logId/audio"
    }

    /**
     * Search log entries by query string
     *
     * @param query Search query (searches title and transcription)
     * @return Result with list of matching log entries or error
     */
    suspend fun searchLogs(query: String): Result<List<LogEntry>> {
        return try {
            // Get authentication token
            val token = getToken() ?: return Result.failure(Exception("Not logged in"))

            // Make GET request with query parameter
            val result: SearchResult = client.get("$BASE_URL/logs/search") {
                header("Authorization", "Bearer $token")
                parameter("q", query)
            }.body()

            Result.success(result.logs)
        } catch (e: Exception) {
            // Handle network or search errors
            Result.failure(Exception("Search failed: ${e.message}"))
        }
    }

    /**
     * Add a friend by username
     *
     * @param friendUsername Username of the friend to add
     * @return Result with success message or error
     */
    suspend fun addFriend(friendUsername: String): Result<String> {
        return try {
            // Get authentication token
            val token = getToken() ?: return Result.failure(Exception("Not logged in"))

            // Make POST request to add friend
            // FIXED: Use AddFriendRequest instead of FriendRequest
            client.post("$BASE_URL/friends/add") {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(AddFriendRequest(friendUsername))
            }

            Result.success("Friend added successfully!")
        } catch (e: Exception) {
            // Handle errors
            Result.failure(Exception("Failed to add friend: ${e.message}"))
        }
    }

    /**
     * Get list of all friends
     *
     * @return Result with list of friend usernames or error
     */
    suspend fun getFriends(): Result<List<String>> {
        return try {
            // Get authentication token
            val token = getToken() ?: return Result.failure(Exception("Not logged in"))

            // Make GET request to get friends list
            val response: FriendsResponse = client.get("$BASE_URL/friends") {
                header("Authorization", "Bearer $token")
            }.body()

            Result.success(response.friends)
        } catch (e: Exception) {
            // Handle errors
            Result.failure(Exception("Failed to load friends: ${e.message}"))
        }
    }

    /**
     * Share a log entry with a friend
     *
     * @param logId ID of the log to share
     * @param friendUsername Username of friend to share with
     * @return Result with success message or error
     */
    suspend fun shareLog(logId: String, friendUsername: String): Result<String> {
        return try {
            // Get authentication token
            val token = getToken() ?: return Result.failure(Exception("Not logged in"))

            // Make POST request to share log
            client.post("$BASE_URL/logs/share") {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(ShareLogRequest(logId, friendUsername))
            }

            Result.success("Log shared successfully!")
        } catch (e: Exception) {
            // Handle errors
            Result.failure(Exception("Failed to share log: ${e.message}"))
        }
    }

    /**
     * Transcribe audio file using ML server
     *
     * @param audioBytes Raw audio file bytes
     * @return Result with transcription text or error
     */
    suspend fun transcribeAudio(audioBytes: ByteArray): Result<String> {
        return try {
            // Make POST request to ML transcription server
            val response: Map<String, Any> = client.post("$ML_SERVER_URL/transcribe") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("audio", audioBytes, Headers.build {
                                append(HttpHeaders.ContentType, "audio/mp4")
                                append(HttpHeaders.ContentDisposition, "filename=audio.m4a")
                            })
                        }
                    )
                )
            }.body()

            // Check if transcription was successful
            val success = response["success"] as? Boolean ?: false
            if (success) {
                val transcription = response["transcription"] as? String ?: ""
                Result.success(transcription)
            } else {
                val error = response["error"] as? String ?: "Transcription failed"
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            // Handle network or transcription errors
            Result.failure(Exception("Transcription error: ${e.message}"))
        }
    }
}