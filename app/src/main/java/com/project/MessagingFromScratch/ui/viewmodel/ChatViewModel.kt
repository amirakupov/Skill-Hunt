package com.project.MessagingFromScratch.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.project.MessagingFromScratch.data.model.Message
import com.project.MessagingFromScratch.repository.MessageRepository
import com.project.MessagingFromScratch.repository.USER_ID_ME // Assuming current user is ME for sending
import com.project.MessagingFromScratch.repository.USER_NAMES
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// --- ChatUiState ---
data class ChatUiState(
    val conversationId: String = "",
    val otherUserId: String = "",
    val otherUserName: String = "Chat",
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val currentMessageText: String = ""
)

// --- ChatViewModel ---
class ChatViewModel(
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var currentConversationId: String? = null
    private var currentOtherUserId: String? = null

    fun loadConversation(conversationId: String, otherUserId: String, otherUserName: String) {
        if (currentConversationId == conversationId && !_uiState.value.isLoading) {
            // Already loaded and not in initial loading state
            // Mark messages as read if necessary
            markMessagesAsReadIfNeeded(conversationId, USER_ID_ME)
            return
        }
        currentConversationId = conversationId
        currentOtherUserId = otherUserId

        _uiState.update {
            it.copy(
                conversationId = conversationId,
                otherUserId = otherUserId,
                otherUserName = otherUserName,
                isLoading = true,
                error = null
            )
        }

        viewModelScope.launch {
            messageRepository.getMessages(conversationId)
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            error = "Failed to load messages: ${e.message}",
                            isLoading = false
                        )
                    }
                }
                .collect { messages ->
                    _uiState.update {
                        it.copy(
                            messages = messages,
                            isLoading = false
                        )
                    }
                    // Mark messages as read after loading them
                    markMessagesAsReadIfNeeded(conversationId, USER_ID_ME)
                }
        }
    }

    fun onMessageTextChanged(newText: String) {
        _uiState.update { it.copy(currentMessageText = newText) }
    }

    fun sendMessage() {
        val convId = currentConversationId ?: return
        val receiverId = currentOtherUserId ?: return
        val textToSend = _uiState.value.currentMessageText.trim()

        if (textToSend.isBlank()) {
            return // Or show some error
        }

        viewModelScope.launch {
            // Optimistically update UI or show sending indicator if desired
            _uiState.update { it.copy(currentMessageText = "") } // Clear input field

            val result = messageRepository.sendMessage(
                conversationId = convId,
                senderId = USER_ID_ME, // Assuming current user sends the message
                receiverId = receiverId,
                text = textToSend
            )

            if (result.isFailure) {
                _uiState.update {
                    it.copy(
                        error = "Failed to send message: ${result.exceptionOrNull()?.message}",
                        // Optionally revert optimistic update or show retry
                        currentMessageText = textToSend // Restore text for retry
                    )
                }
            }
            // Message list will update via the flow from repository
        }
    }

    private fun markMessagesAsReadIfNeeded(conversationId: String, readerUserId: String) {
        viewModelScope.launch {
            // Check if there are unread messages for the current user in this conversation before marking
            // This logic could be more sophisticated (e.g., only mark visible messages)
            val unreadMessagesExist = _uiState.value.messages.any {
                it.conversationId == conversationId && it.receiverId == readerUserId && !it.isRead
            }
            if (unreadMessagesExist) {
                messageRepository.markMessagesAsRead(conversationId, readerUserId)
            }
        }
    }

    // Factory for creating ChatViewModel with dependencies
    companion object {
        fun Factory(repository: MessageRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
                        return ChatViewModel(repository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
    }
}