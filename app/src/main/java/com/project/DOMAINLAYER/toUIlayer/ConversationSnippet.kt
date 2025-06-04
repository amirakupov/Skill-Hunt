package com.project.DOMAINLAYER.toUIlayer

import java.util.Date

data class ConversationSnippet(
    val id: String, // This is the conversationId
    val otherUserId: String,
    val otherUserName: String,
    val lastMessageText: String,
    val lastMessageTimestamp: Date,
    val unreadCount: Int,
    val otherUserImageUrl: String? = null // Placeholder for future UI enhancement
)