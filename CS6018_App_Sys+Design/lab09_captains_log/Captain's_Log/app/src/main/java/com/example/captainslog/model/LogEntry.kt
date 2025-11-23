package com.example.captainslog.model

import kotlinx.serialization.Serializable

/**
 * Data class representing a Captain's Log entry
 * 
 * @property id Unique identifier for the log entry
 * @property title Title/summary of the log entry
 * @property audioFilename Name of the audio file on server
 * @property transcription Text transcription of the audio
 * @property timestamp Unix timestamp when log was created
 * @property stardate Star Trek style stardate
 * @property isShared Whether this log has been shared with friends
 */
@Serializable
data class LogEntry(
    val id: String,
    val title: String,
    val audioFilename: String,
    val transcription: String,
    val timestamp: Long,
    val stardate: String,
    val isShared: Boolean = false
)
