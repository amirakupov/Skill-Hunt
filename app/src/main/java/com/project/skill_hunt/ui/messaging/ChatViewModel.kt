package com.project.skill_hunt.ui.messaging

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.skill_hunt.data.model.Message // Your Message data class
import com.project.skill_hunt.data.model.SendMessageRequest // Your SendMessageRequest data class
import com.project.skill_hunt.domainlayer_usecases.messaging.GetMessagesForConversationUseCase
import com.project.skill_hunt.domainlayer_usecases.messaging.SendMessageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

// Argument names used in Navigation graph (ensure these match your NavHost)
const val CHAT_ARG_CONVERSATION_ID = "conversationId"
const val CHAT_ARG_OTHER_USER_ID = "otherUserId" // If starting a new chat
const val CHAT_ARG_USER_NAME = "userName" // For displaying the other user's name

sealed class ChatUiState {
    object Idle : ChatUiState()
    object LoadingMessages : ChatUiState()
    data class MessagesLoaded(val messages: List<Message>) : ChatUiState()
    object SendingMessage : ChatUiState()
    data class MessageSent(val newConversationId: String?) : ChatUiState() // Optional: to signal new conv ID
    data class Error(val message: String) : ChatUiState()
}

class ChatViewModel(
    private val getMessagesUseCase: GetMessagesForConversationUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val savedStateHandle: SavedStateHandle, // Injected by the ChatViewModelFactory
    private val currentUserId: String // Added: Injected by ChatViewModelFactory
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Idle)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _messageInput = MutableStateFlow("")
    val messageInput: StateFlow<String> = _messageInput.asStateFlow()

    // conversationId can be updated if a new chat is started
    var conversationId: String? = savedStateHandle[CHAT_ARG_CONVERSATION_ID]
        private set // Allow internal update via SavedStateHandle or after sending first message

    private val otherUserIdFromNav: String? = savedStateHandle[CHAT_ARG_OTHER_USER_ID]
    val otherUserName: String? = savedStateHandle[CHAT_ARG_USER_NAME]

    init {
        // If a conversationId is provided, load its messages.
        // The currentUserId is now available from the constructor.
        conversationId?.let {
            loadMessages(it)
        }
        // If conversationId is null but otherUserIdFromNav is present,
        // we are ready to send the first message to start a new chat.
        // The UI might prompt for input or remain Idle.
    }

    fun loadMessages(convId: String) {
        viewModelScope.launch {
            // currentUserId is now a constructor parameter
            getMessagesUseCase(userId = currentUserId, conversationId = convId)
                .onStart { _uiState.value = ChatUiState.LoadingMessages }
                .catch { throwable ->
                    _uiState.value = ChatUiState.Error(throwable.message ?: "Failed to load messages")
                }
                .collect { result -> // UseCase now returns Flow<Result<List<Message>>>
                    result.fold(
                        onSuccess = { messages ->
                            _uiState.value = ChatUiState.MessagesLoaded(messages)
                        },
                        onFailure = { throwable ->
                            _uiState.value = ChatUiState.Error(throwable.message ?: "Failed to load messages")
                        }
                    )
                }
        }
    }

    fun onMessageInputChange(newInput: String) {
        _messageInput.value = newInput
    }

    fun sendMessage() {
        val content = _messageInput.value.trim()
        if (content.isEmpty()) {
            // _uiState.value = ChatUiState.Error("Message cannot be empty.") // Optional
            return
        }

        val recipientId: String? = determineRecipientId()

        if (recipientId == null) {
            _uiState.value = ChatUiState.Error("Cannot determine message recipient.")
            return
        }

        viewModelScope.launch {
            _uiState.value = ChatUiState.SendingMessage
            val request = SendMessageRequest(
                senderId = currentUserId, // Use the injected currentUserId
                receiverUserId = recipientId,
                content = content,
                // Pass current conversationId if it exists,
                // SendMessageUseCase/Repository should handle creating a new one if null
                conversationId = this@ChatViewModel.conversationId
            )

            // SendMessageUseCase now returns Result<Message> (the sent message, including new convId)
            val result = sendMessageUseCase(request)

            result.fold(
                onSuccess = { sentMessage ->
                    _messageInput.value = "" // Clear input

                    val newConversationId = sentMessage.conversationId
                    var didConversationIdChange = false

                    if (this@ChatViewModel.conversationId == null && newConversationId != null) {
                        // This was the first message of a new chat, update our conversationId
                        this@ChatViewModel.conversationId = newConversationId
                        savedStateHandle[CHAT_ARG_CONVERSATION_ID] = newConversationId
                        didConversationIdChange = true
                    }

                    // Reload messages for the (potentially new) conversation
                    // The UI state will transition through LoadingMessages to MessagesLoaded via loadMessages
                    // Or, if you want to immediately show the sent message, you could update MessagesLoaded state directly
                    // and then trigger a full refresh if needed. For simplicity, reloading all.
                    loadMessages(newConversationId) // Use the new/confirmed conversation ID

                    if (didConversationIdChange) {
                        // Optionally emit a specific state if the UI needs to react to the new conv ID
                        // _uiState.value = ChatUiState.MessageSent(newConversationId = newConversationId)
                        // This state would then transition to LoadingMessages -> MessagesLoaded
                    }
                },
                onFailure = { throwable ->
                    _uiState.value = ChatUiState.Error(throwable.message ?: "Failed to send message")
                }
            )
        }
    }

    private fun determineRecipientId(): String? {
        // If explicitly starting a new chat with otherUserId from navigation arguments
        if (otherUserIdFromNav != null) {
            return otherUserIdFromNav
        }

        // If in an existing conversation, try to derive the other user from messages
        // This relies on currentUserId being correctly set.
        if (this.conversationId != null) {
            val currentMessages = (_uiState.value as? ChatUiState.MessagesLoaded)?.messages
            // Find the first message not sent by the current user to identify the other participant,
            // or if all messages are from current user (e.g. only sent, no replies yet), take receiver of first.
            // This logic might need to be more robust depending on how conversations are structured.
            return currentMessages?.firstNotNullOfOrNull { msg ->
                if (msg.senderId == currentUserId) msg.receiverId else msg.senderId
            }
        }
        return null // Cannot determine if conversationId is null and otherUserIdFromNav is also null
    }
}