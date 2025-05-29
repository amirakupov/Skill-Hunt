package com.project.skill_hunt.data.repository

import com.project.skill_hunt.data.model.ConversationSnippet
import com.project.skill_hunt.data.model.Message
import com.project.skill_hunt.data.model.SendMessageRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date
import kotlin.random.Random

class FakeMessageRepositoryImpl : MessageRepository {

    private val fakeConversations = mutableListOf<ConversationSnippet>()
    private val fakeMessages = mutableMapOf<String, MutableList<Message>>() // Key: conversationId
    private var messageIdCounter = 0L
    private var conversationIdCounter = 0
    private val currentUserId = "currentUser123" // Simulate current logged-in user

    init {
        // Initialize with some fake data
        // Conversation 1
        addFakeConversation(
            otherUserId = "user2",
            otherUserName = "Alice Wonderland",
            initialMessageContent = "Hey, how are you?",
            initialMessageSenderIsCurrentUser = false,
            timestamp = Date(System.currentTimeMillis() - 1000L * 60 * 60 * 2) // 2 hours ago
        )
        // Conversation 2
        addFakeConversation(
            otherUserId = "user3",
            otherUserName = "Bob The Builder",
            initialMessageContent = "Can we build it? Yes, we can!",
            initialMessageSenderIsCurrentUser = true,
            timestamp = Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24) // 1 day ago
        )
        // Conversation 3
        addFakeConversation(
            otherUserId = "user4",
            otherUserName = "Charlie Chaplin",
            initialMessageContent = "A day without laughter is a day wasted.",
            initialMessageSenderIsCurrentUser = false,
            timestamp = Date(System.currentTimeMillis() - 1000L * 60 * 60 * 48) // 2 days ago
        )

        // Add more messages to Alice's conversation for more detail
        fakeConversations.find { it.otherUserId == "user2" }?.let { conv ->
            addMessageToConversationInternal(
                conversationId = conv.id,
                senderId = currentUserId, // Current user replies
                receiverId = "user2",
                content = "I'm doing great, Alice! Thanks for asking. How about you?",
                timestamp = Date(System.currentTimeMillis() - 1000L * 60 * 60 * 1 - 1000L * 60 * 30), // 1.5 hours ago
            )
            addMessageToConversationInternal(
                conversationId = conv.id,
                senderId = "user2", // Alice replies back
                receiverId = currentUserId,
                content = "Fantastic! Just working on a new project. It's quite exciting.",
                timestamp = Date(System.currentTimeMillis() - 1000L * 60 * 60 * 1), // 1 hour ago
            )
            addMessageToConversationInternal(
                conversationId = conv.id,
                senderId = currentUserId, // Current user replies
                receiverId = "user2",
                content = "Oh, that sounds interesting! Tell me more if you can.",
                timestamp = Date(System.currentTimeMillis() - 1000L * 60 * 30), // 30 mins ago
            )
        }
    }

    private fun addFakeConversation(
        otherUserId: String,
        otherUserName: String,
        initialMessageContent: String,
        initialMessageSenderIsCurrentUser: Boolean,
        timestamp: Date
    ) {
        val convId = "conv${conversationIdCounter++}"
        val senderOfInitialMessage =
            if (initialMessageSenderIsCurrentUser) currentUserId else otherUserId
        val receiverOfInitialMessage =
            if (initialMessageSenderIsCurrentUser) otherUserId else currentUserId

        fakeConversations.add(
            ConversationSnippet(
                id = convId,
                otherUserId = otherUserId,
                otherUserName = otherUserName,
                lastMessage = initialMessageContent,
                timestamp = timestamp,
                unreadCount = if (!initialMessageSenderIsCurrentUser && Random.nextInt(
                        0,
                        3
                    ) > 0
                ) Random.nextInt(1, 3) else 0
            )
        )
        fakeMessages[convId] = mutableListOf() // Initialize message list

        addMessageToConversationInternal(
            conversationId = convId,
            senderId = senderOfInitialMessage,
            receiverId = receiverOfInitialMessage,
            content = initialMessageContent,
            timestamp = timestamp,
            isReadInitially = initialMessageSenderIsCurrentUser || (senderOfInitialMessage == currentUserId)
        )
    }

    // Internal helper to add messages and update snippets
    private fun addMessageToConversationInternal(
        conversationId: String,
        senderId: String,
        receiverId: String,
        content: String,
        timestamp: Date,
        isReadInitially: Boolean = (senderId == currentUserId) // Messages sent by current user are read by them
    ): Message {
        val newMessage = Message(
            id = "msg${messageIdCounter++}",
            conversationId = conversationId,
            senderId = senderId,
            receiverId = receiverId,
            content = content,
            timestamp = timestamp,
            isRead = isReadInitially
        )
        fakeMessages[conversationId]?.add(newMessage)

        // Update conversation snippet's last message, timestamp, and unread count
        val convIndex = fakeConversations.indexOfFirst { it.id == conversationId }
        if (convIndex != -1) {
            val oldSnippet = fakeConversations[convIndex]
            val newUnreadCount = if (senderId != currentUserId && receiverId == currentUserId) {
                oldSnippet.unreadCount + 1 // Increment if message is for current user and not from them
            } else if (senderId == currentUserId) {
                0 // Reset if current user sent the message
            } else {
                oldSnippet.unreadCount // No change if message is not for current user
            }
            fakeConversations[convIndex] = oldSnippet.copy(
                lastMessage = content,
                timestamp = timestamp,
                unreadCount = newUnreadCount
            )
        }
        return newMessage
    }

    override fun getConversationSnippets(userId: String): Flow<List<ConversationSnippet>> = flow {
        delay(300) // Simulate network delay
        // For this fake, we assume `userId` is the `currentUserId` and return all conversations
        emit(fakeConversations.sortedByDescending { it.timestamp })
    }

    override fun getMessagesForConversation(
        conversationId: String,
        userId: String
    ): Flow<List<Message>> = flow {
        delay(200) // Simulate network delay
        val messagesForConversation =
            fakeMessages[conversationId]?.sortedBy { it.timestamp } ?: emptyList()
        emit(messagesForConversation)

        // Simulate marking messages as read when the current user opens the conversation
        if (userId == currentUserId) {
            var snippetNeedsUpdate = false
            fakeMessages[conversationId]?.forEachIndexed { index, message ->
                if (message.receiverId == currentUserId && !message.isRead) {
                    // This modification directly changes the object in the list
                    fakeMessages[conversationId]?.set(index, message.copy(isRead = true))
                    snippetNeedsUpdate = true
                }
            }

            // If messages were marked read, update the conversation snippet's unread count
            if (snippetNeedsUpdate) {
                val convIndex = fakeConversations.indexOfFirst { it.id == conversationId }
                if (convIndex != -1 && fakeConversations[convIndex].unreadCount > 0) {
                    fakeConversations[convIndex] =
                        fakeConversations[convIndex].copy(unreadCount = 0)
                    // Note: To see this unread count change reflected *immediately* in the
                    // ConversationListScreen, the getConversationSnippets flow would need to be
                    // re-collected, or this flow would need to somehow trigger an update to that one.
                    // For a fake repository, this delayed consistency is often acceptable.
                }
            }
        }
    }

    override suspend fun sendMessage(request: SendMessageRequest): Result<Message> {
        delay(150) // Simulate network delay for sending a message

        // Determine the conversation ID.
        // If request.conversationId is null, it implies a new conversation with request.receiverUserId.
        // In a real app, you might search for an existing conversation or create a new one.
        // For this fake, if conversationId is null and we find a conversation with receiverUserId, use it.
        // Otherwise, create a new conversation.

        var targetConversationId = request.conversationId
        var conversationExisted = true

        if (targetConversationId == null) {
            // Try to find if a conversation with this other user already exists
            val existingConv = fakeConversations.find { it.otherUserId == request.receiverUserId }
            if (existingConv != null) {
                targetConversationId = existingConv.id
            } else {
                // No existing conversation, so we need to create a new one.
                // For simplicity, we'll need a name for the other user. In a real app,
                // you might fetch user details or use a placeholder.
                // Here, we'll use a placeholder name.
                val otherUserNamePlaceholder =
                    "User ${request.receiverUserId.take(4)}" // e.g., "User user"
                val newConvId = "conv${conversationIdCounter++}"

                // Add the new conversation snippet
                fakeConversations.add(
                    ConversationSnippet(
                        id = newConvId,
                        otherUserId = request.receiverUserId,
                        otherUserName = otherUserNamePlaceholder, // Placeholder
                        lastMessage = request.content, // The new message is the first and last
                        timestamp = Date(), // Current time
                        unreadCount = 0 // New conversation, current user sent, so 0 unread for current user
                    )
                )
                fakeMessages[newConvId] = mutableListOf() // Initialize message list
                targetConversationId = newConvId
                conversationExisted = false // A new conversation was created
            }
        }

        // Now that we have a targetConversationId, add the message
        return try {
            val newMessage = addMessageToConversationInternal(
                conversationId = targetConversationId!!, // Should not be null here
                senderId = currentUserId, // Current user is always the sender in this function
                receiverId = request.receiverUserId,
                content = request.content,
                timestamp = Date() // Current time for the new message
                // isReadInitially is true by default if senderId == currentUserId in addMessageToConversationInternal
            )
            Result.success(newMessage)
        } catch (e: Exception) {
            // e.g., if targetConversationId was somehow still null, though logic above prevents it.
            Result.failure(Exception("Failed to send message: ${e.message}"))
        }
    }
}