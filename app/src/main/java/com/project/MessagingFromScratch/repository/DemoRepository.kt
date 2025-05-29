// File: /MessagingFromScratch/repository/demo/DemoRepository.kt
package com.project.MessagingFromScratch.repository.demo

import com.project.MessagingFromScratch.data.model.ConversationSnippet
import com.project.MessagingFromScratch.data.model.Message
import kotlinx.coroutines.flow.Flow

interface DemoRepository {
    // These methods operate on demo data
    fun getDemoConversationSnippets(userId: String): Flow<List<ConversationSnippet>>
    fun getDemoMessages(conversationId: String): Flow<List<Message>>
    suspend fun sendDemoMessage(
        conversationId: String,
        senderId: String,
        receiverId: String,
        text: String
    ): Result<String>
    fun getOrCreateDemoConversationId(userId1: String, userId2: String): Flow<String>
    suspend fun markDemoMessagesAsRead(conversationId: String, readerUserId: String)
    fun startFullDemoSimulation()

    // Method to get the demo user ID for "Me"
    fun getDemoUserIdMe(): String
    // Method to get other demo user IDs if needed by InMemoryMessageRepository
    // fun getDemoUserIdOther1(): String
    // fun getDemoUserNamesMap(): Map<String, String>
}