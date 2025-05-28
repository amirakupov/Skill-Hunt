package com.project.skill_hunt.ui.messaging

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
// Add more imports (Column, Text, Button, LazyColumn, etc.)

@Composable
fun ConversationListScreen(
    viewModelFactory: ConversationListViewModelFactory,
    onNavigateToChat: (conversationId: String?, otherUserId: String?) -> Unit
) {
    val conversationListViewModel: ConversationListViewModel = viewModel(factory = viewModelFactory)
    val uiState by conversationListViewModel.uiState.collectAsState()

    // UI for displaying list of conversations
    // Example:
    // Column {
    //     Text("Conversations")
    //     when (val state = uiState) {
    //         is ConversationListUiState.Loading -> Text("Loading conversations...")
    //         is ConversationListUiState.Success -> {
    //             if (state.conversations.isEmpty()) {
    //                 Text("No conversations yet.")
    //             } else {
    //                 LazyColumn {
    //                     items(state.conversations) { snippet ->
    //                         Button(onClick = { onNavigateToChat(snippet.id, null) }) { // Pass relevant IDs
    //                             Text("Chat with ${snippet.otherUserName} (Unread: ${snippet.unreadCount})")
    //                         }
    //                     }
    //                 }
    //             }
    //         }
    //         is ConversationListUiState.Error -> Text("Error: ${state.message}")
    //     }
    // }
}