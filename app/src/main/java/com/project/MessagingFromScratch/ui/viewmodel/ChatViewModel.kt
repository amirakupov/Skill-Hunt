package com.project.MessagingFromScratch.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.project.DOMAINLAYER.fromDataLayer.Message
import com.project.DOMAINLAYER.usecase14.LocalSentMessageNotifier
import com.project.DOMAINLAYER.usecase15.UIrepository
import com.project.DOMAINLAYER.usecase15.USER_ID_ME // Assuming current user is ME for sending
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.Date

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
    private val UIrepository: UIrepository
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
            UIrepository.getMessages(conversationId)
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

            val result = UIrepository.sendMessage(
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
                UIrepository.markMessagesAsRead(conversationId, readerUserId)
            }
        }
    }

    // Factory for creating ChatViewModel with dependencies
    companion object {
        fun Factory(repository: UIrepository): ViewModelProvider.Factory =
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

    // Inside your ChatViewModel.kt

// Make sure these imports are present if ChatViewModel is the chosen location for the dummy function
// import com.project.DOMAINLAYER.fromDataLayer.Message
// import com.project.DOMAINLAYER.usecase14.LocalSentMessageNotifier
// import java.util.Date
// import java.util.UUID // Not strictly needed here if ID comes from repo, but good for Message construction
// Assuming USER_ID_ME is accessible or defined in ViewModel
// And _uiState contains relevant details like currentUserDisplayName

    class ChatViewModel(/*... dependencies ...*/) : ViewModel() {

        // ... (your existing _uiState, init block, onMessageTextChanged, sendMessage functions etc.)

        /*
        // Your existing sendMessage function (UNCHANGED and NOT calling the notifier)
        fun sendMessage() {
            val convId = currentConversationId ?: return
            val receiverId = currentOtherUserId ?: return // Ensure this maps to your actual state variable name
            val textToSend = _uiState.value.currentMessageText.trim()

            if (textToSend.isBlank()) {
                return // Or show some error
            }

            viewModelScope.launch {
                _uiState.update { it.copy(currentMessageText = "") } // Clear input field

                val result = UIrepository.sendMessage(
                    conversationId = convId,
                    senderId = USER_ID_ME, // Assuming current user sends the message
                    receiverId = receiverId,
                    text = textToSend
                )

                if (result.isFailure) {
                    _uiState.update {
                        it.copy(
                            error = "Failed to send message: ${result.exceptionOrNull()?.message}",
                            currentMessageText = textToSend
                        )
                    }
                }
                // Message list will update via the flow from repository

                // !!! IMPORTANT: The actual call to notifyMessageSentLocally is NOT made here
                // to adhere to the "do not change UI/existing logic" constraint.
                // The function conceptualPostSuccessfulSendNotification is for design demonstration ONLY.
            }
        }
        */


        // --- DUMMY FUNCTION FOR DESIGN DEMONSTRATION ---
        /**
         * THIS IS A DUMMY FUNCTION - NOT CALLED BY THE ACTUAL sendMessage().
         * It demonstrates how, after a successful message send, we would construct
         * the Message object and notify the LocalSentMessageNotifier.
         *
         * In a real implementation, parts of this logic would be integrated into the
         * success path of the actual `sendMessage()` function.
         */
        @Suppress("unused", "UNUSED_PARAMETER") // To avoid IDE warnings for an uncalled/partially used function
        private fun conceptualPostSuccessfulSendNotification(
            actualSentMessageId: String, // This would come from the successful Result of UIrepository.sendMessage
            conversationId: String,
            senderId: String,      // e.g., USER_ID_ME or a dynamically obtained current user ID
            receiverId: String,    // The ID of the other user in the conversation
            sentText: String,
            currentSenderDisplayName: String? // Name of the current user sending the message
        ) {
            // Reconstruct the Message object as it would have been conceptually created.
            // The timestamp would ideally be from the server/repository upon successful save,
            // but for a local notification reflecting an immediate action, a client-generated
            // timestamp is often acceptable for immediate feedback.
            // The `id` must be the one returned by the repository.
            val sentMessage = Message(
                id = actualSentMessageId,
                conversationId = conversationId,
                senderId = senderId,
                receiverId = receiverId,
                text = sentText,
                timestamp = Date(), // Current time for the conceptual notification
                isRead = false,     // New messages are unread by default for the receiver
                senderName = currentSenderDisplayName // Display name of the sender
            )

            // The actual call to the notifier that WOULD happen:
            LocalSentMessageNotifier.notifyMessageSentLocally(sentMessage)

            // For demonstration, you might print something if this dummy function were ever invoked:
            // println("[DUMMY VM] Conceptual notification for sent message: ${sentMessage.text} with ID: ${sentMessage.id}")
        }

        // Example of how this dummy function *might* be "conceptually called"
        // (again, this call itself is also dummy and not part of live code path)
        @Suppress("unused")
        private fun triggerConceptualNotificationExample() {
            // Simulate values that would exist after a successful send
            val dummyMessageId = "dummy-msg-${UUID.randomUUID()}"
            val dummyConversationId = "conv-user1-user2"
            val dummySenderId = "user1" // Assume USER_ID_ME
            val dummyReceiverId = "user2"
            val dummyText = "This is a conceptually sent message!"
            val dummySenderName = "Me (Dummy)"

            conceptualPostSuccessfulSendNotification(
                actualSentMessageId = dummyMessageId,
                conversationId = dummyConversationId,
                senderId = dummySenderId,
                receiverId = dummyReceiverId,
                sentText = dummyText,
                currentSenderDisplayName = dummySenderName
            )
        }
    }
}