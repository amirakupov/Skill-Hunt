package com.project.skill_hunt.data.model // Or  chosen model package

import kotlinx.serialization.Serializable

@Serializable
data class ConversationSnippet( // For displaying a list of conversations
    val conversationId: String,
    val otherUserId: String, // The ID of the user talking to
    val otherUserName: String, // Display name of the other user
    val otherUserAvatarUrl: String?, // Optional
    val lastMessage: String, // Snippet of the last message
    val lastMessageTimestamp: Long, // For sorting and display
    val unreadCount: Int
)

@Serializable
data class Message(
    val messageId: String,
    val conversationId: String,
    val senderId: String, // ID of the user who sent this message
    val receiverId: String, // ID of the user who should receive this
    val content: String, // The text of the message
    val timestamp: Long, // When the message was sent/received
    val isRead: Boolean = false // Status for the recipient
)

@Serializable
data class SendMessageRequest(
    val receiverUserId: String, // Who the message is for
    // val conversationId: String? = null, // Optional: if starting new or adding to existing
    val content: String
)

// No specific response needed for SendMessageRequest usually, a 200 OK or 201 Created is enough.
// Or the backend could return the created Message object.
// For now, let's assume the backend returns the newly created Message:
// typealias SendMessageResponse = Message