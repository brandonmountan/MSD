package com.example.captainslog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.captainslog.model.Friend
import com.example.captainslog.model.FriendRequest
import com.example.captainslog.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for friends screen
 * Manages friends list, friend requests, and friend operations
 */
class FriendsViewModel(private val apiClient: ApiClient) : ViewModel() {

    // List of friends
    private val _friends = MutableStateFlow<List<Friend>>(emptyList())
    val friends: StateFlow<List<Friend>> = _friends.asStateFlow()

    // Pending friend requests
    private val _pendingRequests = MutableStateFlow<List<FriendRequest>>(emptyList())
    val pendingRequests: StateFlow<List<FriendRequest>> = _pendingRequests.asStateFlow()

    /**
     * Load friends and pending requests from server
     */
    fun loadFriends() {
        viewModelScope.launch {
            // Load friends list
            val friendsResult = apiClient.getFriends()
            if (friendsResult.isSuccess) {
                val friendUsernames = friendsResult.getOrNull() ?: emptyList()
                _friends.value = friendUsernames.mapIndexed { index, username ->
                    Friend(id = index.toString(), username = username)
                }
            }

            // TODO: Load pending friend requests
            // For prototype, we use an empty list
            // In production, you would have an API endpoint for this
            _pendingRequests.value = emptyList()
        }
    }

    /**
     * Send a friend request to another user
     *
     * @param username Username of the user to send request to
     */
    fun sendRequest(username: String) {
        viewModelScope.launch {
            val result = apiClient.addFriend(username)
            if (result.isSuccess) {
                // Reload friends list
                loadFriends()
            }
        }
    }

    /**
     * Accept a friend request
     *
     * @param requestId ID of the request to accept
     */
    fun acceptRequest(requestId: String) {
        viewModelScope.launch {
            // TODO: Implement accept friend request API call
            // For prototype, this is a no-op
            // In production: apiClient.acceptFriendRequest(requestId)

            // Reload friends list
            loadFriends()
        }
    }

    /**
     * Decline a friend request
     *
     * @param requestId ID of the request to decline
     */
    fun declineRequest(requestId: String) {
        viewModelScope.launch {
            // TODO: Implement decline friend request API call
            // For prototype, this is a no-op
            // In production: apiClient.declineFriendRequest(requestId)

            // Reload friends list
            loadFriends()
        }
    }

    /**
     * Remove a friend
     *
     * @param friendId ID of the friend to remove
     */
    fun removeFriend(friendId: String) {
        viewModelScope.launch {
            // TODO: Implement remove friend API call
            // For prototype, this is a no-op
            // In production: apiClient.removeFriend(friendId)

            // Reload friends list
            loadFriends()
        }
    }
}