package com.example.maraca

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maraca.sensors.ShakeDetector
import java.text.SimpleDateFormat
import java.util.*

// Main activity class - the entry point of the app
class MainActivity : ComponentActivity() {
    // Get the ViewModel for this activity
    private val viewModel: MainViewModel by viewModels()
    // System service for accessing sensors
    private lateinit var sensorManager: SensorManager
    // Our custom shake detector
    private lateinit var shakeDetector: ShakeDetector

    // Called when activity is first created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the sensor manager from Android system
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // Create shake detector that calls addShake when shake detected
        shakeDetector = ShakeDetector { intensity ->
            viewModel.addShake(intensity)
        }

        // Set up the UI using Compose
        setContent {
            // Use Material Design theme
            MaterialTheme {
                // Show the main app content
                MaracaApp(viewModel)
            }
        }
    }

    // Called when app becomes visible to user
    override fun onResume() {
        super.onResume()
        // Get the accelerometer sensor
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let { sensor ->
            // Start listening to sensor changes
            sensorManager.registerListener(shakeDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    // Called when app goes into background
    override fun onPause() {
        super.onPause()
        // Stop listening to sensor to save battery
        sensorManager.unregisterListener(shakeDetector)
    }
}

// Main UI function that displays the app content
@Composable
fun MaracaApp(viewModel: MainViewModel) {
    // Watch for changes in shakes list
    val shakes by viewModel.shakes.collectAsState(initial = emptyList())

    // Vertical layout that fills the screen
    Column(
        modifier = Modifier
            .fillMaxSize() // Take up full screen
            .padding(16.dp), // Add padding around edges
        horizontalAlignment = Alignment.CenterHorizontally // Center content horizontally
    ) {
        // App title
        Text(
            text = "Maraca App",
            fontSize = 24.sp, // Large text size
            modifier = Modifier.padding(bottom = 16.dp) // Space below
        )

        // Instructions for user
        Text(
            text = "Shake your phone!",
            fontSize = 16.sp, // Medium text size
            modifier = Modifier.padding(bottom = 24.dp) // Space below
        )

        // Show total number of shakes
        Text(
            text = "Total shakes: ${shakes.size}",
            fontSize = 18.sp, // Large-ish text size
            modifier = Modifier.padding(bottom = 16.dp) // Space below
        )

        // Row of buttons side by side
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp), // Space between buttons
            modifier = Modifier.padding(bottom = 16.dp) // Space below
        ) {
            // Button to delete old shakes
            Button(onClick = { viewModel.deleteOldShakes() }) {
                Text("Delete Old") // Button text
            }
            // Button to delete all shakes
            Button(onClick = { viewModel.deleteAllShakes() }) {
                Text("Delete All") // Button text
            }
        }

        // Label for shake list
        Text(
            text = "Recent Shakes:",
            fontSize = 16.sp, // Medium text size
            modifier = Modifier.padding(bottom = 8.dp) // Space below
        )

        // Scrollable list of shakes
        LazyColumn {
            // Create item for each shake
            items(shakes) { shake ->
                // Format timestamp as time string
                val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(shake.timestamp))
                // Display time and intensity
                Text(
                    text = "$time - Intensity: ${String.format("%.1f", shake.intensity)}",
                    modifier = Modifier.padding(vertical = 4.dp) // Space above and below
                )
            }
        }
    }
}