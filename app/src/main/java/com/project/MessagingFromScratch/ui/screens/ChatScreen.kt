package com.project.MessagingFromScratch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.* // Keep this for Box, Column, Row, Spacer, etc.
// Add or ensure these specific layout imports for Insets:
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime // For WindowInsets.ime
import androidx.compose.foundation.layout.imePadding // For the modifier
import androidx.compose.foundation.layout.navigationBarsPadding // For the modifier
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
// import androidx.compose.ui.platform.LocalView // Might not be needed directly
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
// import androidx.compose.ui.unit.sp // Already imported if text styles use it
import com.project.MessagingFromScratch.data.model.Message
import com.project.MessagingFromScratch.repository.USER_ID_ME
import com.project.MessagingFromScratch.ui.viewmodel.ChatUiState
import com.project.MessagingFromScratch.ui.viewmodel.ChatViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class) // ExperimentalLayoutApi for imePadding etc.
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val density = LocalDensity.current

    // To properly react to IME visibility changes, we observe the IME WindowInset
    val imeVisible = WindowInsets.ime.getBottom(density) > 0 // A common way to check if IME is visible

    // Scroll to bottom when new messages arrive or keyboard visibility changes
    LaunchedEffect(uiState.messages.size, imeVisible) { // Key on imeVisible
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.otherUserName) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            MessageInput(
                currentText = uiState.currentMessageText,
                onTextChanged = viewModel::onMessageTextChanged,
                onSendMessage = {
                    viewModel.sendMessage()
                    keyboardController?.hide()
                }
            )
        }
    ) { paddingValues ->
        ChatScreenContent(
            modifier = Modifier
                .padding(paddingValues)
                .imePadding(), // Apply IME padding to content area
            uiState = uiState,
            listState = listState
        )
    }
}

// ... rest of the ChatScreen.kt file (ChatScreenContent, MessageBubble, MessageInput, formatMessageTimestamp)
// Ensure the rest of the file is exactly as provided in the previous correct version.
// The only change needed was in the LaunchedEffect and potentially its imports.

@Composable
fun ChatScreenContent(
    modifier: Modifier = Modifier,
    uiState: ChatUiState,
    listState: androidx.compose.foundation.lazy.LazyListState
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (uiState.isLoading && uiState.messages.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.error != null) {
            Text(
                text = "Error: ${uiState.error}",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center).padding(16.dp)
            )
        } else if (uiState.messages.isEmpty() && !uiState.isLoading) {
            Text(
                text = "No messages yet. Say hello!",
                modifier = Modifier.align(Alignment.Center).padding(16.dp)
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.messages, key = { it.id }) { message ->
                    MessageBubble(message = message, currentUserId = USER_ID_ME)
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message, currentUserId: String) {
    val isMyMessage = message.senderId == currentUserId
    val horizontalArrangement = if (isMyMessage) Arrangement.End else Arrangement.Start
    val bubbleColor = if (isMyMessage) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
    val textColor = if (isMyMessage) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
    val bubbleShape = if (isMyMessage) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp,bottomEnd = 16.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = horizontalArrangement
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f) // Max width for a bubble
                .clip(bubbleShape)
                .background(bubbleColor)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Column {
                // If not my message and sender name is available (group chat scenario), show it
                // For 1-on-1 chat, uiState.otherUserName in TopAppBar is enough.
                // if (!isMyMessage && message.senderName != null) {
                //     Text(
                //         text = message.senderName,
                //         style = MaterialTheme.typography.labelSmall,
                //         color = textColor.copy(alpha = 0.7f)
                //     )
                // }
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor
                )
                Text(
                    text = formatMessageTimestamp(message.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor.copy(alpha = 0.7f),
                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun MessageInput(
    currentText: String,
    onTextChanged: (String) -> Unit,
    onSendMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 4.dp // Optional: adds a slight shadow
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .navigationBarsPadding(), // Add padding for navigation bars
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = currentText,
                onValueChange = onTextChanged,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        onSendMessage()
                        // keyboardController?.hide() // Hiding is handled in ChatScreen's onSendMessage
                    }
                ),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors( // Use OutlinedTextFieldDefaults
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                ),
                maxLines = 5
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = onSendMessage,
                enabled = currentText.isNotBlank(),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send message"
                )
            }
        }
    }
}

private fun formatMessageTimestamp(date: Date): String {
    // Simple time format, customize as needed
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
}
