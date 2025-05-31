// Expected by: BackendApiRepository
// To be defined in: com.project.DOMAINLAYER.data.remote.dto.MessageBackendDto.kt

package com.project.DOMAINLAYER.data.remote.dto

// @Serializable
data class MessageBackendDto(
    val id: String,                     // Unique identifier for the message
    val conversationId: String,         // ID of the conversation this message belongs to
    val senderId: String,               // User ID of the message sender
    val text: String,                   // The content of the message
    val timestamp: Long,                // Epoch milliseconds when the message was sent/created on the server
    val readByParticipantIds: List<String>?, // Optional: List of user IDs who have read this message
    val status: String?                 // Optional: e.g., "SENT", "DELIVERED", "READ", "FAILED"
    // Any other fields like attachments, reactions, etc.
)