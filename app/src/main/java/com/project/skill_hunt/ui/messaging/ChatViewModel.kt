package com.project.skill_hunt.ui.messaging

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.skill_hunt.data.model.Message
import com.project.skill_hunt.domainlayer_usecases.messaging.GetMessagesForConversationUseCase
import com.project.skill_hunt.domainlayer_usecases.messaging.SendMessageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Argument names used in Navigation graph
const val CHAT_ARG_CONVERSATION_ID = "conversationId"
const val CHAT_ARG_OTHER_USER_ID = "otherUserId" // If starting a new chat

sealed class ChatUiState {
    object Idle : ChatUiState()
    object LoadingMessages : ChatUiState()
    data class MessagesLoaded(val messages: List<Message>) : ChatUiState()
    object SendingMessage : ChatUiState()
    data class Error(val message: String) : ChatUiState()
}

class ChatViewModel(
    private val getMessagesUseCase: GetMessagesForConversationUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val savedStateHandle: SavedStateHandle // Injected by Hilt or passed by factory if not using Hilt
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Idle)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _messageInput = MutableStateFlow("")
    val messageInput: StateFlow<String> = _messageInput.asStateFlow()

    // These will be determined from savedStateHandle
    private var targetConversationId: String? = savedStateHandle[CHAT_ARG_CONVERSATION_ID]
    private var targetOtherUserId: String? = savedStateHandle[CHAT_ARG_OTHER_USER_ID]

    init {
        // If a conversationId is provided, load its messages.
        // If otherUserId is provided (and no conversationId), it implies starting a new chat.
        // The FakeApiService's getOrCreateConversationId will handle finding/creating the conversation.
        // For simplicity now, we assume if conversationId exists, we use it.
        // If not, we expect otherUserId for sending the first message.
        targetConversationId?.let {
            loadMessages(it)
        }
    }

    fun loadMessages(conversationId: String) {
        // Update the internal state if conversationId changes (e.g., navigating from one chat to another directly)
        this.targetConversationId = conversationId
        savedStateHandle[CHAT_ARG_CONVERSATION_ID] = conversationId // Keep SavedStateHandle updated

        viewModelScope.launch {
            _uiState.value = ChatUiState.LoadingMessages
            val result = getMessagesUseCase(conversationId)
            _uiState.value = result.fold(
                onSuccess = { messages -> ChatUiState.MessagesLoaded(messages) },
                onFailure = { throwable ->
                    ChatUiState.Error(
                        throwable.message ?: "Failed to load messages"
                    )
                }
            )
        }
    }

    fun onMessageInputChange(newInput: String) {
        _messageInput.value = newInput
    }

    fun sendMessage() {
        val content = _messageInput.value.trim()
        if (content.isEmpty()) {
            _uiState.value = ChatUiState.Error("Message cannot be empty.")
            return // Exit if content is empty
        }

        // --- Determine the recipient ---
        var determinedRecipientId: String? = targetOtherUserId // Start with targetOtherUserId if available

        if (determinedRecipientId == null) {
            // If targetOtherUserId wasn't provided, try to derive from existing conversation context
            val currentMessages = (_uiState.value as? ChatUiState.MessagesLoaded)?.messages
            determinedRecipientId = currentMessages?.firstOrNull()?.let { msg ->
                // Assuming "currentUser_fake_id" is our fake logged-in user
                if (msg.senderId == "currentUser_fake_id") msg.receiverId else msg.senderId
            }
        }

        // If, after all attempts, we still don't have a recipient ID, we cannot proceed.
        // This also covers the case where targetConversationId was null initially, and targetOtherUserId was null.
        if (determinedRecipientId == null) {
            _uiState.value = ChatUiState.Error("Cannot determine recipient for sending the message.")
            return // Exit because no recipient could be found
        }
        // --- End of recipient determination ---

        // At this point, determinedRecipientId is guaranteed to be non-null if we haven't returned.
        // So, we can safely use it.
        val finalRecipientIdForUseCase = determinedRecipientId

        viewModelScope.launch {
            _uiState.value = ChatUiState.SendingMessage
            // Now, finalRecipientIdForUseCase is guaranteed to be a String
            val result = sendMessageUseCase(finalRecipientIdForUseCase, content)
            _uiState.value = result.fold(
                onSuccess = { sentMessage ->
                    _messageInput.value = "" // Clear input
                    targetConversationId = sentMessage.conversationId
                    savedStateHandle[CHAT_ARG_CONVERSATION_ID] = targetConversationId
                    loadMessages(sentMessage.conversationId) // This will eventually update _uiState to Loading then MessagesLoaded/Error

                    // After sending and initiating a load, what should the immediate state be?
                    // Option 1: Go back to Idle, letting loadMessages handle subsequent states.
                    // This might cause a brief flicker if loadMessages is quick.
                    ChatUiState.Idle

                    // Option 2: Go directly to LoadingMessages, mirroring loadMessages' start.
                    // ChatUiState.LoadingMessages

                    // Option 3 (More complex, but better UX):
                    // Ito show the sent message immediately without a full reload flicker:
                    // val currentMessages = (_uiState.value as? ChatUiState.MessagesLoaded)?.messages ?: emptyList()
                    // ChatUiState.MessagesLoaded(currentMessages + sentMessage)
                    // Then, if loadMessages() is still desired for robustness (e.g. to fetch other potential new messages),
                    // it could be called, but the UI has already updated optimistically.
                    // For now, simpler state transitions.
                },
                onFailure = { throwable ->
                    ChatUiState.Error(throwable.message ?: "Failed to send message")
                }
            )        }

    }
}