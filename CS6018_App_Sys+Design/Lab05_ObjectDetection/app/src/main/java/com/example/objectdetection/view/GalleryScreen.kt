package com.example.objectdetection.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import coil.compose.AsyncImage
import com.example.objectdetection.viewmodel.GalleryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel,
    onBackToCamera: () -> Unit // Navigate back to camera
) {
    val galleryState by viewModel.galleryState

    // Load photos when screen opens
    LaunchedEffect(Unit) {
        viewModel.loadPhotos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Photos") },
                navigationIcon = {
                    IconButton(onClick = onBackToCamera) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back to Camera")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadPhotos() }) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                // Loading state
                galleryState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // Error state
                galleryState.error != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = galleryState.error!!,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { viewModel.loadPhotos() }) {
                            Text("Retry")
                        }
                    }
                }

                // Empty state
                galleryState.photos.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No photos yet",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = "Take photos with the camera to see them here",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Photos grid
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2), // 2 columns
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(galleryState.photos) { photo ->
                            PhotoCard(
                                photoUrl = viewModel.getPhotoUrl(photo.id),
                                authHeaders = galleryState.authHeaders,
                                timestamp = photo.timestamp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotoCard(
    photoUrl: String,
    authHeaders: Map<String, String>,
    timestamp: Long
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f), // Square photos
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            // Photo image with authentication headers
            AsyncImage(
                model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                    .data(photoUrl)
                    .apply {
                        // Add auth headers to request
                        authHeaders.forEach { (key, value) ->
                            addHeader(key, value)
                        }
                    }
                    .crossfade(true)
                    .build(),
                contentDescription = "Photo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Timestamp overlay
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = formatTimestamp(timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val now = Date()
    val diff = now.time - date.time

    return when {
        diff < 60_000 -> "Just now"
        diff < 3600_000 -> "${diff / 60_000}m ago"
        diff < 86400_000 -> "${diff / 3600_000}h ago"
        else -> SimpleDateFormat("MMM d", Locale.US).format(date)
    }
}