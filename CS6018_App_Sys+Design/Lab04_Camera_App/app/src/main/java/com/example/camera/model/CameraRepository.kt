package com.example.camera.model

import androidx.compose.ui.geometry.Offset

// Repository class that manages storage and retrieval of camera data
class CameraRepository {

    // List to store all the bright points found
    private val pointsOfInterest = mutableListOf<PointOfInterest>()

    // Add a new bright point to storage
    fun addPointOfInterest(point: PointOfInterest) {
        // Add the new point to the end of the list
        pointsOfInterest.add(point)

        // Keep only last 100 points to avoid memory issues
        if (pointsOfInterest.size > 100) {
            // Remove the oldest point (first in list)
            pointsOfInterest.removeAt(0)
        }
    }

    // Get the most recent bright points found
    fun getRecentPointsOfInterest(count: Int = 10): List<PointOfInterest> {
        // Return the last 'count' points from list
        return pointsOfInterest.takeLast(count)
    }

    // Calculate average position of recent bright points
    fun getAverageBrightestPoint(): Offset? {
        // If we have no points, return null
        if (pointsOfInterest.isEmpty()) return null

        // Get the last 5 points
        val recent = pointsOfInterest.takeLast(5)
        // Calculate average X coordinate
        val avgX = recent.map { it.position.x }.average().toFloat()
        // Calculate average Y coordinate
        val avgY = recent.map { it.position.y }.average().toFloat()

        // Return the average position
        return Offset(avgX, avgY)
    }

    // Remove all stored points
    fun clearHistory() {
        pointsOfInterest.clear()
    }
}