package com.project.DOMAINLAYER // Or your preferred utility package

import com.project.DOMAINLAYER.fromDataLayer.Message
import com.project.DOMAINLAYER.toUIlayer.ConversationSnippet
import java.util.Date

object MapperObserver {

    /**
     * Transforms a list of individual messages into a list of conversation snippets.
     *
     * @param messages The complete list of messages from all conversations.
     * @param currentUserId The ID of the currently logged-in user.
     * @param userNamesMap A map of UserID to UserName (e.g., DataRepositoryImpl.DEMO_USER_NAMES)
     *                     to resolve display names for users.
     * @return A list of ConversationSnippet objects.
     */
    fun mapMessagesToConversationSnippets(
        messages: List<Message>,
        currentUserId: String,
        userNamesMap: Map<String, String>
    ): List<ConversationSnippet> {
        if (messages.isEmpty()) {
            return emptyList()
        }

        val messagesByConversation: Map<String, List<Message>> = messages.groupBy { it.conversationId }
        val conversationSnippets = mutableListOf<ConversationSnippet>()

        for ((conversationId, conversationMessages) in messagesByConversation) {
            if (conversationMessages.isEmpty()) continue

            val sortedMessages = conversationMessages.sortedByDescending { it.timestamp }
            val lastMessage = sortedMessages.first()

            val otherUserId: String
            val otherUserName: String

            if (lastMessage.senderId == currentUserId) {
                otherUserId = lastMessage.receiverId
                // Get receiver's name from the provided map
                otherUserName = userNamesMap[otherUserId] ?: "User ${otherUserId.take(6)}"
            } else {
                otherUserId = lastMessage.senderId
                // Use senderName from message if available, otherwise from map, then fallback
                otherUserName = lastMessage.senderName
                    ?: userNamesMap[otherUserId]
                            ?: "User ${otherUserId.take(6)}"
            }

            val unreadCount = conversationMessages.count {
                it.senderId == otherUserId && !it.isRead // Assuming 'isRead' is from currentUserId's perspective
            }

            conversationSnippets.add(
                ConversationSnippet(
                    id = conversationId,
                    otherUserId = otherUserId,
                    otherUserName = otherUserName,
                    lastMessageText = lastMessage.text,
                    lastMessageTimestamp = lastMessage.timestamp,
                    unreadCount = unreadCount,
                    otherUserImageUrl = null // Placeholder for now
                )
            )
        }
        return conversationSnippets.sortedByDescending { it.lastMessageTimestamp }
    }
}