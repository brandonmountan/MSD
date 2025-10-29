package com.example.objectdetection.view

// Import Compose UI components for building the interface
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
// Import Material icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
// Import Coil for image loading
import coil.compose.AsyncImage
// Import our ViewModel
import com.example.objectdetection.viewmodel.GalleryViewModel
// Import utilities for date formatting
import java.text.SimpleDateFormat
import java.util.*

// Opt-in to use experimental Material3 API
@OptIn(ExperimentalMaterial3Api::class)
// Main composable function for the gallery screen
@Composable
fun GalleryScreen(
    // ViewModel that handles gallery logic
    viewModel: GalleryViewModel,
    // Callback function to navigate back to camera
    onBackToCamera: () -> Unit // Navigate back to camera
) {
    // Get the current gallery state from the ViewModel
    val galleryState by viewModel.galleryState

    // Load photos when screen opens
    // Side effect that runs once when the screen is first shown
    LaunchedEffect(Unit) {
        // Call the function to load photos from the server
        viewModel.loadPhotos()
    }

    // Scaffold provides the basic material design layout structure
    Scaffold(
        // Top app bar with title and buttons
        topBar = {
            TopAppBar(
                // Title text
                title = { Text("My Photos") },
                // Back button on the left
                navigationIcon = {
                    IconButton(onClick = onBackToCamera) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back to Camera")
                    }
                },
                // Action buttons on the right
                actions = {
                    // Refresh button
                    IconButton(onClick = { viewModel.loadPhotos() }) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        // Main content area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Show different content based on the current state
            when {
                // Loading state
                // Show loading spinner while fetching photos
                galleryState.isLoading -> {
                    CircularProgressIndicator(
                        // Center the spinner
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // Error state
                // Show error message if loading failed
                galleryState.error != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        // Center items horizontally
                        horizontalAlignment = Alignment.CenterHorizontally,
                        // Add spacing between items
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Display the error message
                        Text(
                            text = galleryState.error!!,
                            color = MaterialTheme.colorScheme.error
                        )
                        // Retry button
                        Button(onClick = { viewModel.loadPhotos() }) {
                            Text("Retry")
                        }
                    }
                }

                // Empty state
                // Show message if there are no photos yet
                galleryState.photos.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        // Center items horizontally
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Main message
                        Text(
                            text = "No photos yet",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        // Instruction text
                        Text(
                            text = "Take photos with the camera to see them here",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Photos grid
                // Show the photos in a grid if we have any
                else -> {
                    // Lazy grid that only renders visible items (for performance)
                    LazyVerticalGrid(
                        // Use 2 columns
                        columns = GridCells.Fixed(2), // 2 columns
                        // Padding around the entire grid
                        contentPadding = PaddingValues(8.dp),
                        // Spacing between columns
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        // Spacing between rows
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Create an item for each photo
                        items(galleryState.photos) { photo ->
                            // Display the photo card
                            PhotoCard(
                                // URL to load the photo from
                                photoUrl = viewModel.getPhotoUrl(photo.id),
                                // Authentication headers needed to access the photo
                                authHeaders = galleryState.authHeaders,
                                // When the photo was taken
                                timestamp = photo.timestamp
                            )
                        }
                    }
                }
            }
        }
    }
}

// Composable function for displaying a single photo card
@Composable
private fun PhotoCard(
    // URL to load the photo from
    photoUrl: String,
    // Authentication headers for the request
    authHeaders: Map<String, String>,
    // Timestamp of when photo was taken
    timestamp: Long
) {
    // Card container for the photo
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f), // Square photos
        // Add shadow/elevation to the card
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        // Box to layer the image and timestamp
        Box {
            // Photo image with authentication headers
            // AsyncImage loads images from URLs asynchronously
            AsyncImage(
                // Build the image request
                model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                    // Set the URL
                    .data(photoUrl)
                    .apply {
                        // Add auth headers to request
                        // Loop through each header and add it to the request
                        authHeaders.forEach { (key, value) ->
                            addHeader(key, value)
                        }
                    }
                    // Enable crossfade animation
                    .crossfade(true)
                    .build(),
                // Description for accessibility
                contentDescription = "Photo",
                // Fill the entire card
                modifier = Modifier.fillMaxSize(),
                // Crop the image to fit
                contentScale = ContentScale.Crop
            )

            // Timestamp overlay
            // Surface to show the timestamp with a background
            Surface(
                modifier = Modifier
                    // Position at bottom right
                    .align(Alignment.BottomEnd)
                    .padding(4.dp),
                // Semi-transparent background
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                // Rounded corners
                shape = MaterialTheme.shapes.small
            ) {
                // Display formatted timestamp
                Text(
                    text = formatTimestamp(timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

// Function to format timestamp into human-readable text
private fun formatTimestamp(timestamp: Long): String {
    // Convert timestamp to Date object
    val date = Date(timestamp)
    // Get current date
    val now = Date()
    // Calculate the difference in milliseconds
    val diff = now.time - date.time

    // Return different formats based on how old the photo is
    return when {
        // Less than 1 minute ago
        diff < 60_000 -> "Just now"
        // Less than 1 hour ago - show minutes
        diff < 3600_000 -> "${diff / 60_000}m ago"
        // Less than 1 day ago - show hours
        diff < 86400_000 -> "${diff / 3600_000}h ago"
        // Older - show date
        else -> SimpleDateFormat("MMM d", Locale.US).format(date)
    }
}