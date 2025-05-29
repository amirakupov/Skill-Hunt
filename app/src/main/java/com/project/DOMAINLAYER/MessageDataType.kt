// File: Skill-Hunt/app/src/main/java/com/project/DOMAINLAYER/MessageDataType.kt
package com.project.DOMAINLAYER

/**
 * A common data type representing a message.
 * For simplicity in UseCase15, this includes a flag relevant to the viewing user.
 */
data class MessageDataType(
    val messageId: String,
    val conversationId: String,
    val senderId: String,
    val text: String,
    val timestamp: Long,
    /**
     * True if this message was sent by the user who is currently viewing it.
     * This flag should be set by the module providing the message list,
     * based on the context of the viewing user.
     */
    val isSentByViewingUser: Boolean
)