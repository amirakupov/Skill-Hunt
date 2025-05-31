package com.project.DOMAINLAYER.repository

import com.project.DOMAINLAYER.data.remote.dto.ConversationDto
import com.project.DOMAINLAYER.data.remote.dto.MessageDto
import com.project.DOMAINLAYER.data.remote.dto.UserDto
import kotlinx.coroutines.flow.Flow // Using Flow if backend supports streaming/updates, else suspend fun List<T>

interface BackendApiRepository {
    // For Users
    suspend fun getUserById(userId: String): Result<UserDto> // Result wrapper for error handling
    suspend fun getAllUsers(): Result<List<UserDto>> // Or Flow if it's a stream

    // For Conversations (Chats)
    suspend fun getConversationsForUser(userId: String): Result<List<ConversationDto>> // Or Flow
    suspend fun getConversationById(conversationId: String): Result<ConversationDto>

    // For Messages
    suspend fun getMessagesForConversation(conversationId: String): Result<List<MessageDto>> // Or Flow
    suspend fun sendMessage(
        conversationId: String,
        senderId: String,
        receiverId: String, // May not be needed if backend derives from conversationId + senderId
        text: String
    ): Result<MessageDto> // Backend returns the sent message DTO

    // Potentially for marking messages as read on backend
    // suspend fun markMessagesAsReadOnBackend(conversationId: String, readerUserId: String): Result<Unit>
}