package com.project.skill_hunt.data.model

import java.util.Date

data class ConversationSnippet(
    val id: String, // <--- MUST BE LOWERCASE 'id'
    val otherUserId: String,
    val otherUserName: String,
    val lastMessage: String,
    val timestamp: Date,
    val unreadCount: Int
)

data class Message(
    val id: String, // <--- MUST BE LOWERCASE 'id'
    val conversationId: String,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val timestamp: Date,
    val isRead: Boolean
)

// Not strictly needed if ViewModels handle message sending directly with hardcoded logic,
// but if ChatViewModel's sendMessage needs a structure, you could use this.
// In MessageModels.kt
data class SendMessageRequest(
    val senderId: String, // Make sure this exists if you need it
    val receiverUserId: String,
    val content: String,
    val conversationId: String? // To link to an existing conversation or null for a new one
)