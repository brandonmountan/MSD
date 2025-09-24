package com.example.maraca

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.maraca.data.Shake
import com.example.maraca.data.ShakeDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ViewModel holds data for the UI
class MainViewModel(application: Application) : AndroidViewModel(application) {
    // Get database access object
    private val dao = ShakeDatabase.getDatabase(application).shakeDao()
    // Get list of recent shakes (updates automatically)
    val shakes = dao.getRecentShakes()

    // Add a new shake to the database
    fun addShake(intensity: Float) {
        // Run database operation in background thread
        viewModelScope.launch {
            // Create new shake with current time and intensity
            dao.insertShake(Shake(timestamp = System.currentTimeMillis(), intensity = intensity))
        }
    }

    // Delete shakes older than 24 hours
    fun deleteOldShakes() {
        // Run database operation in background thread
        viewModelScope.launch {
            // Calculate time 24 hours ago
            val oneDayAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
            // Delete shakes older than that time
            dao.deleteOldShakes(oneDayAgo)
        }
    }

    // Delete all shakes from database
    fun deleteAllShakes() {
        // Run database operation in background thread
        viewModelScope.launch {
            // Delete everything
            dao.deleteAllShakes()
        }
    }
}