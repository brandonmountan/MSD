package com.example.maraca.sensors

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

// Class that listens to accelerometer sensor
class ShakeDetector(private val onShake: (Float) -> Unit) : SensorEventListener {
    // Track when last shake happened (for cooldown)
    private var lastShakeTime = 0L
    // Remember last accelerometer values
    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f

    // Called when sensor values change
    override fun onSensorChanged(event: SensorEvent?) {
        // Only care about accelerometer data
        if (event?.sensor?.type != Sensor.TYPE_ACCELEROMETER) return

        // Get current time in milliseconds
        val currentTime = System.currentTimeMillis()
        // Get current accelerometer values
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        // Only check for shakes if 500ms have passed since last shake
        if (currentTime - lastShakeTime > 500) { // 500ms cooldown
            // Calculate how much acceleration changed
            val deltaX = x - lastX
            val deltaY = y - lastY
            val deltaZ = z - lastZ
            // Calculate total acceleration change using Pythagorean theorem
            val acceleration = sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ)

            // If acceleration is big enough, it's a shake
            if (acceleration > 1f) { // Shake threshold
                // Remember when this shake happened
                lastShakeTime = currentTime
                // Tell the app about the shake
                onShake(acceleration)
            }
        }

        // Remember current values for next time
        lastX = x
        lastY = y
        lastZ = z
    }

    // Called when sensor accuracy changes (don't need this)
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}