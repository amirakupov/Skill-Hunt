package com.project.DOMAINLAYER.usecase15

// File: Skill-Hunt/app/src/main/java/com/project/DOMAINLAYER/usecase15/ConversationMessagesStore.kt

import com.project.DOMAINLAYER.MessageDataType
// import com.project.DOMAINLAYER.UserDataType // Only if UserDataType is directly embedded in MessageDataType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * FOR TEAM: This object acts as a shared 'letterbox' for the messages of a SINGLE,
 * currently active/viewed conversation.
 * The module responsible for fetching messages for a specific conversation and for sending
 * new messages should update this store.
 * The messaging UI (chat screen) will observe `messagesFlow` and `activeConversationIdFlow`.
 */
object ConversationMessagesStore {
    private val _activeConversationId = MutableStateFlow<String?>(null)

    /**
     * MESSAGING MODULE USE: Observes this Flow to know which conversation's messages are currently loaded.
     */
    val activeConversationIdFlow: StateFlow<String?> = _activeConversationId.asStateFlow()

    private val _messagesDataSource = MutableStateFlow<List<MessageDataType>>(emptyList())

    /**
     * MESSAGING MODULE USE: Observes this Flow to display the list of messages for the active conversation.
     */
    val messagesFlow: StateFlow<List<MessageDataType>> = _messagesDataSource.asStateFlow()

    /**
     * FOR TEAM: Call this method from your module to provide the list of messages
     * for a specific conversation that the user has navigated to.
     * This typically happens when a user opens a chat screen.
     * This will replace any previously held messages.
     *
     * @param conversationId The ID of the conversation these messages belong to.
     * @param messages The complete list of `MessageDataType` instances for this conversation.
     *                 Ensure the `isSentByViewingUser` flag within each `MessageDataType`
     *                 is correctly set based on the `CurrentLoggedInUser`.
     *
     * Example from your module (when a user opens chat "conv789"):
     *   val loggedInUserId = CurrentLoggedInUser.dataFlow.value?.id
     *   if (loggedInUserId != null) {
     *       val fetchedMessages = yourBackend.getMessagesFor("conv789") // Returns List of your backend message type
     *       val uiMessages = fetchedMessages.map { backendMsg ->
     *           MessageDataType(
     *               messageId = backendMsg.id,
     *               conversationId = "conv789",
     *               senderId = backendMsg.sender,
     *               text = backendMsg.content,
     *               timestamp = backendMsg.time,
     *               isSentByViewingUser = (backendMsg.sender == loggedInUserId)
     *           )
     *       }
     *       ConversationMessagesStore.setMessagesForConversation("conv789", uiMessages)
     *   }
     */
    fun setMessagesForConversation(conversationId: String, messages: List<MessageDataType>) {
        _activeConversationId.value = conversationId
        _messagesDataSource.value = messages
    }

    /**
     * FOR TEAM: Call this when messages for the active conversation should be cleared.
     * This could be when the user navigates away from the chat screen, logs out,
     * or the chat context becomes invalid.
     */
    fun clearActiveConversationMessages() {
        _activeConversationId.value = null
        _messagesDataSource.value = emptyList()
    }

    // FOR TEAM: Important Note on Sending New Messages:
    // This ConversationMessagesStore is primarily for DISPLAYING messages.
    // To SEND a new message:
    // 1. The Messaging UI will capture the user's input.
    // 2. The Messaging UI will call a dedicated function IN YOUR MODULE/BACKEND SERVICE
    //    (e.g., `yourMessagingService.sendMessage(conversationId, text)`).
    // 3. YOUR messaging service will handle the actual sending logic (network call, DB save).
    // 4. After YOUR service confirms the message is processed (e.g., successfully sent or stored),
    //    YOUR service should then call `ConversationMessagesStore.setMessagesForConversation(...)`
    //    again with the updated list of messages that includes the new message.
    // This ensures this store always reflects the latest state as known by your backend.
}