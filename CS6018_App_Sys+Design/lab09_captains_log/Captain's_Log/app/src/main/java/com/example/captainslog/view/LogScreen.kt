package com.example.captainslog.view

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.captainslog.viewmodel.LogsViewModel
import com.example.captainslog.model.LogEntry

// LOG SCREEN FUNCTIONALITY
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogScreen(viewModel: LogsViewModel, Record: () -> Unit, Friends: () -> Unit, Logout: () -> Unit) {

    // vals
    val log by viewModel.logs.collectAsState()
    val search by viewModel.searchQuery.collectAsState()
    val curr by viewModel.currentlyPlaying.collectAsState()
    val id by viewModel.expandedLogId.collectAsState()

    // Launched Effect
    LaunchedEffect(Unit) { viewModel.loadLogs() }

    // Scaffold
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CAPTAIN LOGS - AUDIO") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                actions = {
                    // Logout button
                    IconButton(onClick = Logout) {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = "LOGOUT",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        },

        // floating button
        floatingActionButton = {
            FloatingActionButton(
                onClick = Record,
                containerColor = MaterialTheme.colorScheme.primary
            ) { Icon(Icons.Default.Mic, contentDescription = "RECORD") }
        },

        // bottom bar
        bottomBar = {

            // navigation
            NavigationBar {

                //navigating bar
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("LOGS NAVIGATION") },
                    selected = true,
                    onClick = { })

                // navigating bar
                NavigationBarItem(
                    icon = { Icon(Icons.Default.People, contentDescription = null) },
                    label = { Text("FRIENDS NAVIGATION") },
                    selected = false,
                    onClick = Friends
                )
            }
        }

    ) { padding ->

        // column
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // bar for SEARCH
            OutlinedTextField(
                value = search,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                placeholder = { Text("SEARCH-LOGS!") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {

                    // check to see if it is empty
                    if (search.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "CLEAR")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(20.dp)
            )

            // List of logs if it is empty
            if (log.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.MicNone,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No logs yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            "Tap + to record your first log",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                // if the logs are not empty
            } else {

                // column
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // items
                    items(log, key = { it.id }) { log ->

                        // log card for each log, current and for each id
                        LogCard(
                            log = log,
                            expanded = id == log.id,
                            playing = curr == log.id,
                            expand = { viewModel.toggleExpanded(log.id) },
                            play = {
                                // if it is the current id
                                if (curr == log.id) {
                                    viewModel.pauseLog()
                                } else {
                                    viewModel.playLog(log.id)
                                }
                            },
                            share = { viewModel.share(log.id, emptyList()) }
                        )
                    }
                }
            }
        }
    }
}

// this is log card item function
@Composable
private fun LogCard(
    log: LogEntry,
    expanded: Boolean,
    playing: Boolean,
    expand: () -> Unit,
    play: () -> Unit,
    share: () -> Unit
) {

    // CARD
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {

        // COLUMN
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {

            // top row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // COLUMN
                Column(modifier = Modifier.weight(1f)) {
                    // text inside the column
                    Text(
                        text = log.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = log.stardate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                // NEW ROW WITH A PLAY BUTTON
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Play/Pause Button
                    IconButton(
                        onClick = play,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {

                        Icon(
                            if (!playing) {
                                Icons.Default.PlayArrow
                            } else {
                                Icons.Default.Pause
                            },
                            contentDescription = if (!playing) {
                                "PLAY"
                            } else {
                                "PAUSE"
                            },
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    // button to share
                    IconButton(onClick = share) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "SHARE",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Expand Button
                    IconButton(onClick = expand) {
                        Icon(
                            if (!expanded) {
                                Icons.Default.ExpandMore
                            } else {
                                Icons.Default.ExpandLess
                            },
                            contentDescription = if (!expanded) {
                                "EXPAND"
                            } else {
                                "LIMIT"
                            }
                        )
                    }
                }
            }

            // transition just like in record
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {

                Column(modifier = Modifier.padding(top = 12.dp)) {

                    // TEXT
                    Text(
                        text = log.transcription.ifEmpty { "LOADING!!!" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (log.transcription.isEmpty()) {
                            MaterialTheme.colorScheme.outline
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }
        }
    }
}