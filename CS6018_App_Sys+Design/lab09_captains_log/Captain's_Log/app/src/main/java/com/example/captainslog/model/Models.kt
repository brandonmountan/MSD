package com.example.captainslog.model

import kotlinx.serialization.Serializable

/**
 * Data class for user credentials (login/register)
 *
 * @property username User's username
 * @property password User's password
 */
@Serializable
data class UserCredentials(
    val username: String,
    val password: String
)

/**
 * Data class for login/register response from server
 *
 * @property success Whether the operation was successful
 * @property token Authentication token (for login)
 * @property message Error or success message
 */
@Serializable
data class LoginResponse(
    val success: Boolean,
    val token: String? = null,
    val message: String? = null
)

/**
 * Data class for log upload response
 *
 * @property success Whether upload was successful
 * @property logId ID of the uploaded log
 * @property message Error or success message
 */
@Serializable
data class UploadLogResponse(
    val success: Boolean,
    val logId: String? = null,
    val message: String? = null
)

/**
 * Data class for search results
 *
 * @property logs List of matching log entries
 */
@Serializable
data class SearchResult(
    val logs: List<LogEntry>
)

/**
 * Friend data model for friends list
 */
@Serializable
data class Friend(
    val id: String,
    val username: String
)

/**
 * Friend request data model for pending requests
 */
@Serializable
data class FriendRequest(
    val id: String,
    val username: String
)

/**
 * Data class for adding a friend by username
 *
 * @property friendUsername Username of friend to add
 */
@Serializable
data class AddFriendRequest(
    val friendUsername: String
)

/**
 * Data class for sharing a log with a friend
 *
 * @property logId ID of the log to share
 * @property friendUsername Username of friend to share with
 */
@Serializable
data class ShareLogRequest(
    val logId: String,
    val friendUsername: String
)

/**
 * Data class for friends list response
 *
 * @property friends List of friend usernames
 */
@Serializable
data class FriendsResponse(
    val friends: List<String>
)

/**
 * Upload state for recording uploads
 */
sealed class UploadState {
    object Idle : UploadState()
    object Uploading : UploadState()
    object Success : UploadState()
    data class Error(val message: String) : UploadState()
}