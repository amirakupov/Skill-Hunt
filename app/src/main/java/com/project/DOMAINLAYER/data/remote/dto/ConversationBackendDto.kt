package com.project.DOMAINLAYER.data.remote.dto

// Expected by: BackendApiRepository
// To be defined in: com.project.DOMAINLAYER.data.remote.dto.ConversationBackendDto.kt


// @Serializable
data class ConversationBackendDto(
    val id: String,                         // Unique identifier for the conversation/chat
    val participantIds: List<String>,       // List of user IDs participating in this chat
    // For 1-on-1, this would typically have two IDs.
    val lastMessage: MessageBackendDto?,    // The most recent message object (or null if no messages yet)
    // Some backends might only send a preview (text, timestamp, senderId)
    val lastActivityTimestamp: Long,        // Epoch milliseconds of the last interaction (e.g., last message sent)
    val unreadMessagesCountForUser: Map<String, Int>?, // Optional: map of userId to their unread count in this convo
    // More commonly, this might be a single field if the endpoint
    // is user-specific, e.g., `currentUserUnreadCount: Int`
    val createdTimestamp: Long,             // Epoch milliseconds when the conversation was created
    val customTitle: String?                // Optional: For group chats or user-defined titles
    // Any other fields relevant to a conversation overview
)