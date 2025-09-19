package com.example.camera.model

import androidx.compose.ui.geometry.Offset

// Data class that holds the current state of the camera
data class CameraState(
    val lensFacing: Int, // Which camera is being used (front or back)
    val brightestPoint: Offset? = null, // Location of brightest pixel on screen
    val isCapturing: Boolean = false // Whether a photo is currently being taken
)

// Data class that represents a point of interest (bright spot) found
data class PointOfInterest(
    val position: Offset, // X,Y coordinates of the point (0.0 to 1.0)
    val brightness: Int, // How bright this point is (0-255)
    val timestamp: Long = System.currentTimeMillis() // When we found this point
)