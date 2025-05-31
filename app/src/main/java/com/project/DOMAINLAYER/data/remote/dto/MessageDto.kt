package com.project.DOMAINLAYER.data.remote.dto

// @Serializable
data class MessageDto(
    val id: String, // Or Int
    val chatId: String, // Or conversationId
    val senderId: String,
    val text: String,
    val timestamp: Long, // Assuming backend sends as epoch milliseconds
    // val readBy: List<String>? = null // If backend provides this
    // Add other relevant fields
)