// File: /MessagingFromScratch/repository/InMemoryMessageRepository.kt
package com.project.MessagingFromScratch.repository

import com.project.MessagingFromScratch.data.model.ConversationSnippet
import com.project.MessagingFromScratch.data.model.Message
import com.project.MessagingFromScratch.repository.demo.DemoRepository // Import the interface
import com.project.MessagingFromScratch.repository.demo.DemoRepositoryImpl // Import the implementation
import kotlinx.coroutines.flow.Flow

// Constants that were previously defined here can still be,
// but their values will now come from DemoRepositoryImpl for consistency if they are demo-specific.
// Or, they can be defined directly here if they are truly global to InMemoryMessageRepository
// and not just demo-specific.

// For constants like USER_ID_ME that are specifically "who the current demo user is",
// it makes sense for them to originate from the demo source.
const val USER_ID_ME: String = DemoRepositoryImpl.DEMO_USER_ID_ME // Get value from DemoRepositoryImpl
// If you had other constants previously defined here, like USER_ID_OTHER_1 etc.,
// and your UI used them from InMemoryMessageRepository, do the same:
// const val USER_ID_OTHER_1: String = DemoRepositoryImpl.DEMO_USER_ID_OTHER_1
// const val USER_ID_OTHER_2: String = DemoRepositoryImpl.DEMO_USER_ID_OTHER_2

// The USER_NAMES map might also be needed if UI code was accessing it this way.
val USER_NAMES: Map<String, String> = DemoRepositoryImpl.DEMO_USER_NAMES


class InMemoryMessageRepository : MessageRepository { // No constructor parameters needed from outside

    // Internally create and hold the DemoRepositoryImpl instance
    // This `demoRepository` variable will handle all demo-related logic.
    private val demoRepository: DemoRepository = DemoRepositoryImpl()

    init {
        // Start the demo simulation when this repository is initialized
        demoRepository.startFullDemoSimulation()
    }

    override fun getConversationSnippets(userId: String): Flow<List<ConversationSnippet>> {
        // Delegate to the demoRepository instance
        return demoRepository.getDemoConversationSnippets(userId)
    }

    override fun getMessages(conversationId: String): Flow<List<Message>> {
        // Delegate to the demoRepository instance
        return demoRepository.getDemoMessages(conversationId)
    }

    override suspend fun sendMessage(
        conversationId: String,
        senderId: String,
        receiverId: String,
        text: String
    ): Result<String> {
        // Delegate to the demoRepository instance
        return demoRepository.sendDemoMessage(conversationId, senderId, receiverId, text)
    }

    override fun getOrCreateConversationId(userId1: String, userId2: String): Flow<String> {
        // Delegate to the demoRepository instance
        return demoRepository.getOrCreateDemoConversationId(userId1, userId2)
    }

    override suspend fun markMessagesAsRead(conversationId: String, readerUserId: String) {
        // Delegate to the demoRepository instance
        demoRepository.markDemoMessagesAsRead(conversationId, readerUserId)
    }
}