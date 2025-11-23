package com.example.captainslog.view

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.captainslog.viewmodel.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.example.captainslog.viewmodel.FriendsViewModel
import com.example.captainslog.model.Friend
import com.example.captainslog.model.FriendRequest
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

// FRIENDS SCREEN FUNCTIONALITY
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(viewModel: FriendsViewModel, onNavigateToLogs: () -> Unit) {

    // tab and add friend
    var tab by remember { mutableStateOf(0) }
    var addFriend by remember { mutableStateOf(false) }

    // friend and request
    val friend by viewModel.friends.collectAsState()
    val requests by viewModel.pendingRequests.collectAsState()

    // LAUNCHED EFFECT
    LaunchedEffect(Unit) { viewModel.loadFriends() }

    // SCAFFOLD HERE
    Scaffold(

        // TOP BAR
        topBar = {
            TopAppBar(
                title = { Text("FRIENDS") }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ), actions = {

                    // BUTTON
                    IconButton(onClick = { addFriend = true }) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "ADD-FRIEND")
                    }
                })
        },

        // BOTTOM
        bottomBar = {
            // Navigation one
            NavigationBar {

                // item one
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("AUDIO-LOGS") }, selected = false, onClick = onNavigateToLogs
                )
                // item two
                NavigationBarItem(
                    icon = { Icon(Icons.Default.People, contentDescription = null) },
                    label = { Text("FRIENDS") }, selected = true, onClick = { })
            }
        }

    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = tab) {

                Tab(
                    selected = tab == 0, onClick = { tab = 0 },
                    text = {
                        Text(
                            "FRIENDS ${
                                if (friend.isNotEmpty()) {
                                    "(${friend.size})"
                                } else {
                                    ""
                                }
                            }"
                        )
                    })
                Tab(
                    selected = tab == 1, onClick = { tab = 1 },
                    text = {
                        Text(
                            "REQUESTS ${
                                if (requests.isNotEmpty()) {
                                    "(${requests.size})"
                                } else {
                                    ""
                                }
                            }"
                        )
                    }
                )
            }

            when (tab) {
                // 0 or 1
                0 -> ListFriends(friend)
                1 -> PendingRequests(requests, accept = { viewModel.acceptRequest(it) })
            }
        }
    }

    // need to add friend
    if (addFriend) {
        AddFriend(dismiss = { addFriend = false }, add = { username ->
            viewModel.sendRequest(username); addFriend = false
        })
    }
}

// FRIEND
@Composable
private fun FriendItem(friend: Friend) {

    // CARD
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)) {

        // ROW
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // SURFACE
            Surface(
                Modifier.size(36.dp).clip(CircleShape),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        friend.username.first().uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // SPACER
            Spacer(Modifier.width(10.dp))

            // new column
            Column(Modifier.weight(1f)) {
                Text(
                    friend.username,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // BUTTON to remove
            IconButton(onClick = { }) {
                Icon(
                    Icons.Default.PersonRemove, contentDescription = "REMOVE",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// ADD FRIEND FUNCTION
@Composable
private fun AddFriend(dismiss: () -> Unit, add: (String) -> Unit) {

    // username
    var user by remember { mutableStateOf("") }

    // ALERT DIALOG
    AlertDialog(
        // REQUEST to dismiss
        onDismissRequest = dismiss,
        title = { Text("ADD A FRIEND") },
        text = {
            OutlinedTextField(
                value = user, onValueChange = { user = it }, label = { Text("USERNAME") },
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )
        },
        // CONFIRM
        confirmButton = {
            Button(
                onClick = { if (user.isNotBlank()) add(user.trim()) },
                enabled = user.isNotBlank()
            ) {
                Text("PLEASE SEND REQUEST")
            }
        },
        // DISMISS
        dismissButton = { TextButton(onClick = dismiss) { Text("CANCEL") } }
    )
}

// LIST OF FRIENDS FUNCTION
@Composable
private fun ListFriends(list: List<Friend>) {

    // if the list is empty or not
    if (list.isEmpty()) {

        // NEW BOX
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            // NEW COLUMN
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.PeopleOutline, contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.outline
                )

                //SPACER
                Spacer(Modifier.height(12.dp))
                Text(
                    "NO FRIENDS ON THE LIST!!!",
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    } else {
        //COLUMN
        LazyColumn(
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(list, key = { it.id }) { friend -> FriendItem(friend) }
        }
    }
}

// FRIEND REQUEST FUNCTION
@Composable
private fun FriendRequestItem(request: FriendRequest, onAccept: (String) -> Unit) {

    // CARD
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        // COLUMN
        Column(Modifier.padding(12.dp)) {
            // NEW ROW
            Row(verticalAlignment = Alignment.CenterVertically) {

                Surface(
                    Modifier.size(36.dp).clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            request.username.first().uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White, fontWeight = FontWeight.Bold
                        )
                    }
                }

                // SPACER
                Spacer(Modifier.width(10.dp))
                // COLUMN
                Column(Modifier.weight(1f)) {
                    Text(
                        request.username,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "FRIEND REQUEST", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            // SPACER
            Spacer(Modifier.height(10.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ACCEPT
                Button(onClick = { onAccept(request.id) }, Modifier.weight(1f)) {
                    Text("ACCEPT")
                }
                // DECLINE
                OutlinedButton(onClick = { }, Modifier.weight(1f)) {
                    Text("DECLINE")
                }
            }
        }
    }
}

// REQUESTS that are pending
@Composable
private fun PendingRequests(reqs: List<FriendRequest>, accept: (String) -> Unit) {

    // if the
    if (reqs.isEmpty()) {

        // BOX
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

            // COLUMN
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                // ICON
                Icon(
                    Icons.Default.MarkEmailRead, contentDescription = null,
                    modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.outline
                )

                //SPACER
                Spacer(Modifier.height(12.dp))

                Text(
                    "CURRENTLY NO REQUESTS ARE PENDING!",
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

    } else {
        // column
        LazyColumn(
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(reqs, key = { it.id }) { request -> FriendRequestItem(request, accept) }
        }
    }
}