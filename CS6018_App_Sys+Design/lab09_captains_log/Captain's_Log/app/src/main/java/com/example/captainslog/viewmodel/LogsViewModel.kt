package com.example.captainslog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.captainslog.model.LogEntry
import com.example.captainslog.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for logs list screen
 * Manages log entries, search, playback, and sharing
 */
class LogsViewModel(private val apiClient: ApiClient) : ViewModel() {

    // List of all logs
    private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())
    val logs: StateFlow<List<LogEntry>> = _logs.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Currently playing log ID
    private val _currentlyPlaying = MutableStateFlow<String?>(null)
    val currentlyPlaying: StateFlow<String?> = _currentlyPlaying.asStateFlow()

    // Expanded log ID (for showing transcription)
    private val _expandedLogId = MutableStateFlow<String?>(null)
    val expandedLogId: StateFlow<String?> = _expandedLogId.asStateFlow()

    /**
     * Load all logs from server
     */
    fun loadLogs() {
        viewModelScope.launch {
            val result = apiClient.getLogs()
            if (result.isSuccess) {
                _logs.value = result.getOrNull() ?: emptyList()
            }
        }
    }

    /**
     * Update search query and filter logs
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query

        if (query.isBlank()) {
            loadLogs()
        } else {
            viewModelScope.launch {
                val result = apiClient.searchLogs(query)
                if (result.isSuccess) {
                    _logs.value = result.getOrNull() ?: emptyList()
                }
            }
        }
    }

    /**
     * Toggle expansion of a log (to show/hide transcription)
     */
    fun toggleExpanded(logId: String) {
        _expandedLogId.value = if (_expandedLogId.value == logId) {
            null
        } else {
            logId
        }
    }

    /**
     * Play a log's audio
     */
    fun playLog(logId: String) {
        // TODO: Implement actual audio playback
        // For prototype, just track the playing state
        _currentlyPlaying.value = logId
    }

    /**
     * Pause the currently playing log
     */
    fun pauseLog() {
        // TODO: Implement actual audio pause
        // For prototype, just clear the playing state
        _currentlyPlaying.value = null
    }

    /**
     * Share a log with friends
     *
     * @param logId ID of the log to share
     * @param friendIds List of friend IDs to share with
     */
    fun share(logId: String, friendIds: List<String>) {
        // TODO: Implement sharing functionality
        // This would call the backend API to share the log
        viewModelScope.launch {
            // For prototype, this is a no-op
            // In production: apiClient.shareLog(logId, friendId)
        }
    }
}