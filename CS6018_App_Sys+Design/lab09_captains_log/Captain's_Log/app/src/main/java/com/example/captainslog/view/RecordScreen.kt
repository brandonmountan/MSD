package com.example.captainslog.view

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.captainslog.viewmodel.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import com.example.captainslog.model.UploadState
import com.example.captainslog.viewmodel.RecordViewModel

// Duration Function
private fun Duration(seconds: Int): String {
    // time in minutes and seconds
    val min = (seconds / 60)
    val sec = (seconds % 60)

    // return the duration string format
    return String.format("%02d:%02d", min, sec)
}

// RECORDING FUNCTION
@Composable
private fun Recording() {

    // pulse transition
    val transition = rememberInfiniteTransition(label = "Record Pulse Check")

    val scale by transition.animateFloat(
        initialValue = 1f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ), label = "scale"
    )

    Box(
        modifier = Modifier
            .size(70.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.error),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Mic,
            contentDescription = null,
            modifier = Modifier.size(30.dp),
            tint = Color.White
        )
    }
}

// RECORD SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordScreen(viewModel: RecordViewModel, onNavigateBack: () -> Unit) {

    // DIALOG
    var saveDialog by remember { mutableStateOf(false) }
    var deleteDialog by remember { mutableStateOf(false) }

    val recording by viewModel.isRecording.collectAsState()
    val status by viewModel.uploadStatus.collectAsState()
    val recordingTime by viewModel.recordingDuration.collectAsState()

    // SCAFFOLD
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("RECORD AUDIO FOR CAPTAIN's LOG") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
    ) { padding ->

        // BOX in SCAFFOLD that is aligned to center
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {

            // COLUMN inside the box
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                // if-else record indicator
                if (recording) {
                    Recording()
                } else {
                    Icon(
                        Icons.Default.Mic, contentDescription = null,
                        modifier = Modifier.size(60.dp), tint = MaterialTheme.colorScheme.outline
                    )
                }

                // Duration TEXT
                Text(
                    text = Duration(recordingTime), style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold, color =
                        if (recording) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                )

                // Status Text
                Text(
                    text = when {
                        // if recording
                        recording -> ("RECORDING In Progress!!!")
                        // recording time is greater than 0
                        recordingTime > 0 -> ("RECORDING STOPPED!!!")
                        // else click start
                        else -> ("PRESS TO START")
                    }, style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.outline
                )

                // SPACER
                Spacer(modifier = Modifier.height(24.dp))

                // if-else not recording and duration
                if (!recording && recordingTime == 0) {

                    // Start Recording Button
                    Button(
                        onClick = { viewModel.startRecording() },
                        modifier = Modifier.size(70.dp).clip(CircleShape),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {

                        // ICON that is inside the button
                        Icon(
                            Icons.Default.FiberManualRecord,
                            contentDescription = "PLEASE START RECORDING!",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                } else if (recording) {

                    // Stop Recording Button
                    Button(
                        onClick = { viewModel.stopRecording() },
                        modifier = Modifier.size(70.dp).clip(CircleShape),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {

                        // ICON that is inside the button
                        Icon(
                            Icons.Default.Stop, contentDescription = "STOP RECORDING!!!",
                            modifier = Modifier.size(40.dp)
                        )
                    }

                } else {

                    // SAVE/DELETE
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        OutlinedButton(
                            onClick = { deleteDialog = true },
                            modifier = Modifier.height(56.dp).width(120.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {

                            // DELETE so it will be null
                            Icon(Icons.Default.Delete, contentDescription = null)

                            // SPACER
                            Spacer(modifier = Modifier.width(10.dp))

                            Text("DELETE")
                        }

                        // BUTTON
                        Button(
                            onClick = { saveDialog = true },
                            modifier = Modifier.height(50.dp).width(100.dp)
                        ) {

                            // ICON
                            Icon(Icons.Default.Save, contentDescription = null)

                            //SPACER
                            Spacer(modifier = Modifier.width(10.dp))

                            // SAVE TEXT
                            Text("SAVE")
                        }
                    }
                }

                // WHEN trying to see Upload Status
                when (status) {
                    is UploadState.Uploading -> {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CircularProgressIndicator()
                            Text(
                                "CURRENTLY UPLOADING!!!",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                    }

                    // state
                    is UploadState.Success -> {
                        LaunchedEffect(Unit) {
                            // delay
                            kotlinx.coroutines.delay(1000)
                            onNavigateBack()
                        }

                        Icon(
                            Icons.Default.CheckCircle, contentDescription = null,
                            tint = Color(0xFF4CAF50), modifier = Modifier.size(42.dp)
                        )
                    }

                    // state error
                    is UploadState.Error -> {
                        Text(
                            "Error: ${(status as UploadState.Error).message}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    // if-else
                    else -> {}
                }
            }
        }
    }

    if (deleteDialog) {
        AlertDialog(
            onDismissRequest = { deleteDialog = false },
            title = { Text("RECORDING TO DELETE") },
            text = { Text("CONFIRM, CANNOT UNDO DELETE!") },
            confirmButton = {
                TextButton(onClick = {
                    deleteDialog = false; onNavigateBack()
                }) {
                    Text("DELETE", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }

    // dialogue saved
    if (saveDialog) {

        // save log dialog
        SaveLog(onDismiss = { saveDialog = false }, onSave = { title ->
            viewModel.save(title)
            //save dialog is false
            saveDialog = false
        }
        )
    }
}

// save log function
@Composable
private fun SaveLog(onDismiss: () -> Unit, onSave: (String) -> Unit) {

    // title
    var title by remember { mutableStateOf("") }

    // ALERT to double check before going ahead
    AlertDialog(onDismissRequest = onDismiss, title = { Text("SAVE") },

        text = {
            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("TITLE") }, singleLine = true, modifier = Modifier.fillMaxWidth()
            )
        },

        confirmButton = {
            Button(
                onClick = { if (title.isNotBlank()) onSave(title.trim()) },
                enabled = title.isNotBlank()
            ) {
                Text("SAVE")
            }
        },

        // cancel which is dismiss button
        dismissButton = { TextButton(onClick = onDismiss) { Text("CANCEL") } }
    )
}