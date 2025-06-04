package com.project.DOMAINLAYER.usecase15

import com.project.DOMAINLAYER.toUIlayer.ConversationSnippet
import com.project.DOMAINLAYER.fromDataLayer.Message
import kotlinx.coroutines.flow.Flow

interface UIrepository {
    fun getConversationSnippets(userId: String): Flow<List<ConversationSnippet>>
    fun getMessages(conversationId: String): Flow<List<Message>>
    suspend fun sendMessage(
        conversationId: String,
        senderId: String,
        receiverId: String,
        text: String
    ): Result<String> // Returns new message ID on success

    fun getOrCreateConversationId(userId1: String, userId2: String): Flow<String>
    suspend fun markMessagesAsRead(conversationId: String, readerUserId: String)
}