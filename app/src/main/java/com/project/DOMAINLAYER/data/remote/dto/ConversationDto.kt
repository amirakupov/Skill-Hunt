package com.project.DOMAINLAYER.data.remote.dto

// @Serializable
data class ConversationDto(
    val id: String, // Or Int
    val participantIds: List<String>,
    val lastMessage: MessageDto?, // Or just text/timestamp for preview
    val lastActivityTimestamp: Long, // Assuming epoch milliseconds
    // val unreadCounts: Map<String, Int>? = null // If backend provides this
    // Add other relevant fields
)