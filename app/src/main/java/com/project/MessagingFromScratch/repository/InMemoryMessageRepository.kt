package com.project.MessagingFromScratch.repository

import com.project.MessagingFromScratch.data.model.ConversationSnippet
import com.project.MessagingFromScratch.data.model.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.random.Random

// --- Hardcoded User Constants ---
const val USER_ID_ME = "userMe"
const val USER_ID_OTHER_1 = "userOther1"
const val USER_ID_OTHER_2 = "userOther2"

val USER_NAMES = mapOf(
    USER_ID_ME to "Me (You)",
    USER_ID_OTHER_1 to "Alice",
    USER_ID_OTHER_2 to "Bob"
)
// --- End Hardcoded User Constants ---

class InMemoryMessageRepository : MessageRepository {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _allMessagesFlow = MutableStateFlow<List<Message>>(emptyList())

    private val _conversationsMapFlow: Flow<Map<String, List<Message>>> =
        _allMessagesFlow.asStateFlow().map { messages ->
            messages.groupBy { it.conversationId }.mapValues { entry ->
                entry.value.sortedBy { it.timestamp }
            }
        }

    init {
        generateInitialFakeData()
        simulateReceivingMessages()
    }

    private fun generateInitialFakeData() {
        applicationScope.launch {
            val conv1Id = generateConversationIdInternal(USER_ID_ME, USER_ID_OTHER_1)
            val conv2Id = generateConversationIdInternal(USER_ID_ME, USER_ID_OTHER_2)

            val initialMessages = mutableListOf(
                Message(conversationId = conv1Id, senderId = USER_ID_ME, receiverId = USER_ID_OTHER_1, text = "Hello Alice!", timestamp = Date(System.currentTimeMillis() - 5000000), isRead = true, senderName = USER_NAMES[USER_ID_ME]),
                Message(conversationId = conv1Id, senderId = USER_ID_OTHER_1, receiverId = USER_ID_ME, text = "Hi Me!", timestamp = Date(System.currentTimeMillis() - 4000000), isRead = true, senderName = USER_NAMES[USER_ID_OTHER_1]),
                Message(conversationId = conv1Id, senderId = USER_ID_ME, receiverId = USER_ID_OTHER_1, text = "How are you doing?", timestamp = Date(System.currentTimeMillis() - 3000000), isRead = false, senderName = USER_NAMES[USER_ID_ME]),
                Message(conversationId = conv2Id, senderId = USER_ID_OTHER_2, receiverId = USER_ID_ME, text = "Hey, what's up?", timestamp = Date(System.currentTimeMillis() - 6000000), isRead = true, senderName = USER_NAMES[USER_ID_OTHER_2]),
                Message(conversationId = conv2Id, senderId = USER_ID_ME, receiverId = USER_ID_OTHER_2, text = "Not much, Bob. Just coding.", timestamp = Date(System.currentTimeMillis() - 5500000), isRead = false, senderName = USER_NAMES[USER_ID_ME])
            )
            _allMessagesFlow.value = initialMessages.sortedBy { it.timestamp }
        }
    }

    private fun simulateReceivingMessages() {
        applicationScope.launch {
            delay(15000)
            val aliceConvId = generateConversationIdInternal(USER_ID_ME, USER_ID_OTHER_1)
            val newMessageFromAlice = Message(
                conversationId = aliceConvId,
                senderId = USER_ID_OTHER_1,
                receiverId = USER_ID_ME,
                text = "Are you there? It's been a while!",
                senderName = USER_NAMES[USER_ID_OTHER_1],
                isRead = false
            )
            _allMessagesFlow.value = (_allMessagesFlow.value + newMessageFromAlice).sortedBy { it.timestamp }

            delay(25000) // Adjusted delay to be from the start of this simulation
            val bobConvId = generateConversationIdInternal(USER_ID_ME, USER_ID_OTHER_2)
            val newMessageFromBob = Message(
                conversationId = bobConvId,
                senderId = USER_ID_OTHER_2,
                receiverId = USER_ID_ME,
                text = "Just checking in. Saw your coding message.",
                senderName = USER_NAMES[USER_ID_OTHER_2],
                isRead = false
            )
            _allMessagesFlow.value = (_allMessagesFlow.value + newMessageFromBob).sortedBy { it.timestamp }
        }
    }

    private fun generateConversationIdInternal(userId1: String, userId2: String): String {
        return if (userId1 < userId2) "$userId1-$userId2" else "$userId2-$userId1"
    }

    override fun getConversationSnippets(userId: String): Flow<List<ConversationSnippet>> {
        return _conversationsMapFlow.map { convMap ->
            convMap.mapNotNull { (convId, messagesInConv) ->
                val lastMessage = messagesInConv.lastOrNull() ?: return@mapNotNull null
                // Corrected logic for determining the other user
                val otherUserIdInConv = messagesInConv.firstOrNull { it.senderId != userId }?.senderId
                    ?: messagesInConv.firstOrNull { it.receiverId != userId }?.receiverId
                    ?: return@mapNotNull null // Should not happen in a 2-party chat

                val unreadCount = messagesInConv.count { !it.isRead && it.receiverId == userId }

                ConversationSnippet(
                    id = convId,
                    otherUserId = otherUserIdInConv,
                    otherUserName = USER_NAMES[otherUserIdInConv] ?: "Unknown User",
                    lastMessageText = lastMessage.text,
                    lastMessageTimestamp = lastMessage.timestamp,
                    unreadCount = unreadCount
                )
            }.sortedByDescending { it.lastMessageTimestamp }
        }
    }

    override fun getMessages(conversationId: String): Flow<List<Message>> {
        return _allMessagesFlow.asStateFlow().map { allMessages ->
            allMessages.filter { it.conversationId == conversationId }.sortedBy { it.timestamp }
        }
    }

    override suspend fun sendMessage(
        conversationId: String,
        senderId: String,
        receiverId: String,
        text: String
    ): Result<String> {
        delay(Random.nextLong(100, 500))
        if (text.isBlank()) {
            return Result.failure(IllegalArgumentException("Message text cannot be blank."))
        }
        val newMessage = Message(
            conversationId = conversationId,
            senderId = senderId,
            receiverId = receiverId,
            text = text.trim(),
            senderName = USER_NAMES[senderId]
        )
        _allMessagesFlow.value = (_allMessagesFlow.value + newMessage).sortedBy { it.timestamp }
        return Result.success(newMessage.id)
    }

    override fun getOrCreateConversationId(userId1: String, userId2: String): Flow<String> = flow {
        emit(generateConversationIdInternal(userId1, userId2))
    }

    override suspend fun markMessagesAsRead(conversationId: String, readerUserId: String) {
        delay(100)
        _allMessagesFlow.value = _allMessagesFlow.value.map { message ->
            if (message.conversationId == conversationId && message.receiverId == readerUserId && !message.isRead) {
                message.copy(isRead = true)
            } else {
                message
            }
        }
        // No need to sort again here if the order doesn't change by marking as read
        // However, if timestamps could be identical and sorting stability matters, keep it:
        // .sortedBy { it.timestamp }
    }
}