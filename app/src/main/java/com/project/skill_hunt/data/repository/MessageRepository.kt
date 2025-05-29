package com.project.skill_hunt.data.repository

import com.project.skill_hunt.data.model.ConversationSnippet
import com.project.skill_hunt.data.model.Message
import com.project.skill_hunt.data.model.SendMessageRequest
import kotlinx.coroutines.flow.Flow

interface MessageRepository { // <--- KEY CHANGE: "interface" instead of "class"

    // Fetches a list of conversation snippets for a given user.
    // Returns a Flow to observe updates (e.g., new messages arriving).
    fun getConversationSnippets(userId: String): Flow<List<ConversationSnippet>>

    // Fetches a list of messages for a specific conversation.
    // Returns a Flow to observe updates (e.g., new messages in this conversation).
    fun getMessagesForConversation(conversationId: String, userId: String): Flow<List<Message>>

    // Sends a message.
    // Takes a SendMessageRequest which should contain all necessary info.
    // Returns a Result wrapper which can indicate success (with the sent Message) or failure.
    suspend fun sendMessage(request: SendMessageRequest): Result<Message>

    // Optional: If you need an explicit way to mark a conversation as read.
    // suspend fun markConversationAsRead(conversationId: String, userId: String): Result<Unit>
}