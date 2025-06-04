package com.project.DOMAINLAYER.fromDataLayer

import java.util.Date
import java.util.UUID

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val conversationId: String, // Combination of two user IDs, sorted
    val senderId: String,
    val receiverId: String,
    val text: String,
    val timestamp: Date = Date(),
    var isRead: Boolean = false, // Will be true if receiver has "seen" it (simulated)
    val senderName: String? = null // For display convenience
)