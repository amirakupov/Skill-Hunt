package com.project.MessagingFromScratch.ui.screens

// ... (all your existing imports are fine) ...
import android.icu.util.Calendar
import androidx.compose.foundation.background // For Modifier.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons // Make sure this is used or remove if not
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.project.MessagingFromScratch.data.model.ConversationSnippet
import com.project.MessagingFromScratch.repository.USER_ID_ME
import com.project.MessagingFromScratch.repository.USER_NAMES
import com.project.MessagingFromScratch.ui.viewmodel.ConversationListUiState
import com.project.MessagingFromScratch.ui.viewmodel.ConversationListViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationListScreen(
    viewModel: ConversationListViewModel,
    onNavigateToChat: (convId: String, otherUserId: String, userName: String) -> Unit,
    onNavigateToNewChat: (otherUserId: String, userName: String) -> Unit
) { // <--- CORRECT OPENING BRACE FOR THE FUNCTION BODY

    // All the UI logic and state declarations go INSIDE these braces:
    val uiState by viewModel.uiState.collectAsState()
    var showNewChatDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Conversations") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        /*
        floatingActionButton = {
            FloatingActionButton(onClick = { showNewChatDialog = true }) {
             //   Icon(Icons.Filled.AddComment, contentDescription = "Start new chat")
            }
        }
        */
    ) { paddingValues ->
        ConversationListContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            onConversationClick = { snippet ->
                viewModel.markConversationAsRead(snippet.id, USER_ID_ME)
                // Ensure snippet.otherUserName is available and passed
                onNavigateToChat(snippet.id, snippet.otherUserId, snippet.otherUserName ?: "Unknown")
            }
        )

        if (showNewChatDialog) {
            NewChatDialog(
                availableUsers = USER_NAMES.filterKeys { it != USER_ID_ME },
                onDismiss = { showNewChatDialog = false },
                onUserSelected = { selectedUserId ->
                    val selectedUserName = USER_NAMES[selectedUserId] ?: "Unknown User" // Get the user's name
                    viewModel.getOrCreateConversationWith(selectedUserId) { conversationId ->
                        // Pass all three arguments to onNavigateToChat
                        onNavigateToChat(conversationId, selectedUserId, selectedUserName)
                    }
                    showNewChatDialog = false
                }
            )
        }
    }
    // The example comments can be removed or kept inside if they help you.
    // Make sure they don't interfere with the bracing.

} // <--- THIS IS THE CORRECTED POSITION FOR THE CLOSING BRACE of ConversationListScreen

// --- Other Composable functions for this screen ---
// These are correctly placed outside ConversationListScreen but in the same file.


@Composable
fun ConversationListContent(
    modifier: Modifier = Modifier,
    uiState: ConversationListUiState,
    onConversationClick: (ConversationSnippet) -> Unit
) {
    when (uiState) {
        is ConversationListUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is ConversationListUiState.Success -> {
            if (uiState.snippets.isEmpty()) {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(16.dp), contentAlignment = Alignment.Center
                ) {
                    Text("No conversations yet. Tap the '+' button to start a new chat!")
                }
            } else {
                LazyColumn(
                    modifier = modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(uiState.snippets, key = { it.id }) { snippet ->
                        ConversationSnippetItem(
                            snippet = snippet,
                            onClick = { onConversationClick(snippet) }
                        )
                        Divider()
                    }
                }
            }
        }
        is ConversationListUiState.Error -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error loading conversations: ${uiState.message}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun ConversationSnippetItem(
    snippet: ConversationSnippet,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer), // Modifier.background applied here
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = snippet.otherUserName.firstOrNull()?.toString()?.uppercase() ?: "U",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        Spacer(Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = snippet.otherUserName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (snippet.unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    // CORRECTED: Using formatDisplayTimestamp
                    text = formatDisplayTimestamp(snippet.lastMessageTimestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = snippet.lastMessageText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (snippet.unreadCount > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (snippet.unreadCount > 0) {
                    Spacer(Modifier.width(8.dp))
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Text(
                            text = snippet.unreadCount.toString(),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun NewChatDialog(
    availableUsers: Map<String, String>,
    onDismiss: () -> Unit,
    onUserSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Start a new chat with...") },
        text = {
            if (availableUsers.isEmpty()) {
                Text("No other users available to chat with.")
            } else {
                LazyColumn { // Changed from LazyColumn to LazyColumn for key support
                    items(availableUsers.toList(), key = { it.first }) { (userId, userName) ->
                        Text(
                            text = userName,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onUserSelected(userId) }
                                .padding(vertical = 12.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun formatDisplayTimestamp(date: Date): String {
    val calendar = Calendar.getInstance() // Uses android.icu.util.Calendar due to import
    calendar.time = date

    val today = Calendar.getInstance()

    return if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
        calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
    ) {
        // Today: Show time
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
    } else if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
        calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) - 1
    ) {
        // Yesterday
        "Yesterday"
    } else if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
        (today.get(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_YEAR)) < 7 &&
        (today.get(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_YEAR)) > 0
    ) {
        // Within the last week (but not today or yesterday): Show day of the week
        SimpleDateFormat("EEE", Locale.getDefault()).format(date) // e.g., "Mon"
    } else {
        // Older or different year: Show date
        SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(date)
    }
}