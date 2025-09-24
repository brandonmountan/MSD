package com.example.maraca.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tell Room this is a database table called "shakes"
@Entity(tableName = "shakes")
data class Shake(
    // Auto-generate unique ID for each shake
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    // When the shake happened (timestamp)
    val timestamp: Long,
    // How strong the shake was (intensity)
    val intensity: Float
)