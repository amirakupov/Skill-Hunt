// File: /MessagingFromScratch/repository/demo/DemoRepositoryImpl.kt
package com.project.MessagingFromScratch.repository.demo

// ... (other imports: Message, ConversationSnippet, kotlinx.coroutines, Date, UUID, Random)
import com.project.MessagingFromScratch.data.model.ConversationSnippet
import com.project.MessagingFromScratch.data.model.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import kotlin.random.Random


class DemoRepositoryImpl : DemoRepository {

    // Make constants available to InMemoryMessageRepository if it needs them
    // Or, InMemoryMessageRepository can just use the getDemoUserIdMe() method
    companion object {
        const val DEMO_USER_ID_ME = "userMe"
        const val DEMO_USER_ID_OTHER_1 = "userOther1"
        const val DEMO_USER_ID_OTHER_2 = "userOther2"

        val DEMO_USER_NAMES = mapOf(
            DEMO_USER_ID_ME to "Me (- Demo)",
            DEMO_USER_ID_OTHER_1 to "Charlie Cooking  (Demo)",
            DEMO_USER_ID_OTHER_2 to "Bob the Builder (Demo)"
        )
    }

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _allDemoMessagesFlow = MutableStateFlow<List<Message>>(emptyList())

    private val _demoConversationsMapFlow: Flow<Map<String, List<Message>>> =
        _allDemoMessagesFlow.asStateFlow().map { messages ->
            messages.groupBy { it.conversationId }.mapValues { entry ->
                entry.value.sortedBy { it.timestamp }
            }
        }
    private var simulationStarted = false

    override fun startFullDemoSimulation() {
        if (simulationStarted) return
        simulationStarted = true
        applicationScope.launch {
            generateInitialFakeData()
            simulateReceivingMessages()
        }
    }

    override fun getDemoUserIdMe(): String {
        return DEMO_USER_ID_ME
    }

    // Implement other constant getter methods from interface if added them

    private fun generateInitialFakeData() {
        val conv1Id = generateConversationIdInternal(DEMO_USER_ID_ME, DEMO_USER_ID_OTHER_1)
        val conv2Id = generateConversationIdInternal(DEMO_USER_ID_ME, DEMO_USER_ID_OTHER_2)

        val initialMessages = mutableListOf(
            Message(id = UUID.randomUUID().toString(), conversationId = conv1Id, senderId = DEMO_USER_ID_ME, receiverId = DEMO_USER_ID_OTHER_1, text = "Hello Charlie, What are you cooking !", timestamp = Date(System.currentTimeMillis() - 5000000), isRead = true, senderName = DEMO_USER_NAMES[DEMO_USER_ID_ME]),
            Message(id = UUID.randomUUID().toString(), conversationId = conv1Id, senderId = DEMO_USER_ID_OTHER_1, receiverId = DEMO_USER_ID_ME, text = "Hi Me!", timestamp = Date(System.currentTimeMillis() - 4000000), isRead = true, senderName = DEMO_USER_NAMES[DEMO_USER_ID_OTHER_1]),
            Message(id = UUID.randomUUID().toString(), conversationId = conv1Id, senderId = DEMO_USER_ID_ME, receiverId = DEMO_USER_ID_OTHER_1, text = "How are doing?", timestamp = Date(System.currentTimeMillis() - 3000000), isRead = false, senderName = DEMO_USER_NAMES[DEMO_USER_ID_ME]),
            Message(id = UUID.randomUUID().toString(), conversationId = conv2Id, senderId = DEMO_USER_ID_OTHER_2, receiverId = DEMO_USER_ID_ME, text = "Hey, what's up?", timestamp = Date(System.currentTimeMillis() - 6000000), isRead = true, senderName = DEMO_USER_NAMES[DEMO_USER_ID_OTHER_2]),
            Message(id = UUID.randomUUID().toString(), conversationId = conv2Id, senderId = DEMO_USER_ID_ME, receiverId = DEMO_USER_ID_OTHER_2, text = "Not much, Bob the Builder. Just coding...", timestamp = Date(System.currentTimeMillis() - 5500000), isRead = false, senderName = DEMO_USER_NAMES[DEMO_USER_ID_ME])
        )
        _allDemoMessagesFlow.value = initialMessages.sortedBy { it.timestamp }
    }

// File: /MessagingFromScratch/repository/demo/DemoRepositoryImpl.kt
// ... (previous parts of DemoRepositoryImpl.kt, including the start of simulateReceivingMessages)

    private fun simulateReceivingMessages() {
        applicationScope.launch {
            delay(15000) // Initial delay before the first simulated message
            val CharlieConvId = generateConversationIdInternal(DEMO_USER_ID_ME, DEMO_USER_ID_OTHER_1)
            val newMessageFromCharlie  = Message(
                id = UUID.randomUUID().toString(),
                conversationId = CharlieConvId,
                senderId = DEMO_USER_ID_OTHER_1, // Charlie  sends
                receiverId = DEMO_USER_ID_ME,   // To Me
                text = "Are there? It's been a while!",
                senderName = DEMO_USER_NAMES[DEMO_USER_ID_OTHER_1],
                isRead = false, // Unread for "Me"
                timestamp = Date() // Current time
            )
            // Add the new message and ensure the list remains sorted by timestamp
            _allDemoMessagesFlow.value = (_allDemoMessagesFlow.value + newMessageFromCharlie ).sortedBy { it.timestamp }

            delay(10000) // Delay between Charlie 's message and Bob the Builder's message
            val bobConvId = generateConversationIdInternal(DEMO_USER_ID_ME, DEMO_USER_ID_OTHER_2)
            val newMessageFromBob = Message(
                id = UUID.randomUUID().toString(),
                conversationId = bobConvId,
                senderId = DEMO_USER_ID_OTHER_2, // Bob sends
                receiverId = DEMO_USER_ID_ME,   // To Me
                text = "Just checking in. Saw coding message.",
                senderName = DEMO_USER_NAMES[DEMO_USER_ID_OTHER_2],
                isRead = false, // Unread for "Me"
                timestamp = Date() // Current time
            )
            // Add the new message and ensure the list remains sorted
            _allDemoMessagesFlow.value = (_allDemoMessagesFlow.value + newMessageFromBob).sortedBy { it.timestamp }

            // can add more simulated messages here if desired
            // For example, a reply from "Me" after some delay:
            // delay(20000)
            // val myReplyToCharlie  = Message(
            //     id = UUID.randomUUID().toString(),
            //     conversationId = CharlieConvId,
            //     senderId = DEMO_USER_ID_ME,
            //     receiverId = DEMO_USER_ID_OTHER_1,
            //     text = "Hey Charlie ! Sorry for the late reply. Doing well!",
            //     senderName = DEMO_USER_NAMES[DEMO_USER_ID_ME],
            //     isRead = false, // isRead is from the receiver's perspective.
            //                     // Charlie  hasn't read this yet.
            //     timestamp = Date()
            // )
            // _allDemoMessagesFlow.value = (_allDemoMessagesFlow.value + myReplyToCharlie ).sortedBy { it.timestamp }
        }
    }

    private fun generateConversationIdInternal(userId1: String, userId2: String): String {
        // Consistent conversation ID regardless of user order
        return if (userId1 < userId2) "$userId1-$userId2" else "$userId2-$userId1"
    }

    override fun getDemoConversationSnippets(userId: String): Flow<List<ConversationSnippet>> {
        return _demoConversationsMapFlow.map { convMap ->
            convMap.mapNotNull { (convId, messagesInConv) ->
                val lastMessage = messagesInConv.lastOrNull() ?: return@mapNotNull null

                // Determine the other user in the conversation
                val otherUserIdInConv = if (lastMessage.senderId == userId) {
                    lastMessage.receiverId
                } else {
                    lastMessage.senderId
                }

                // This check is important if a conversation might not involve the current `userId`
                // (though for snippets, it usually does).
                if (otherUserIdInConv == userId && messagesInConv.size > 1) {
                    // This case handles a conversation "with oneself" or if logic is more complex.
                    // For a typical 2-party chat, this branch might not be hit if one user is always `userId`.
                    // Let's refine `otherUserIdInConv` to be more robust for typical use case:
                    // The actual "other user" is the one who is NOT the `userId` passed to this function.
                }


                val refinedOtherUserId = messagesInConv.firstOrNull { it.senderId != userId || it.receiverId != userId }
                    ?.let { if(it.senderId == userId) it.receiverId else it.senderId }
                    ?: otherUserIdInConv // Fallback, though ideally the above finds the distinct other user

                val unreadCount = messagesInConv.count { !it.isRead && it.receiverId == userId }

                ConversationSnippet(
                    id = convId,
                    otherUserId = refinedOtherUserId, // Use the more robustly determined other user
                    otherUserName = DEMO_USER_NAMES[refinedOtherUserId] ?: "Unknown Demo User",
                    lastMessageText = lastMessage.text,
                    lastMessageTimestamp = lastMessage.timestamp,
                    unreadCount = unreadCount
                )
            }.sortedByDescending { it.lastMessageTimestamp }
        }
    }

    override fun getDemoMessages(conversationId: String): Flow<List<Message>> {
        return _allDemoMessagesFlow.asStateFlow().map { allMessages ->
            allMessages.filter { it.conversationId == conversationId }.sortedBy { it.timestamp }
        }
    }

    override suspend fun sendDemoMessage(
        conversationId: String,
        senderId: String,
        receiverId: String,
        text: String
    ): Result<String> {
        delay(Random.nextLong(100, 500)) // Simulate network delay
        if (text.isBlank()) {
            return Result.failure(IllegalArgumentException("Message text cannot be blank."))
        }
        val newMessage = Message(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            senderId = senderId,
            receiverId = receiverId,
            text = text.trim(),
            senderName = DEMO_USER_NAMES[senderId],
            timestamp = Date(),
            isRead = false // isRead is from the perspective of the receiver
        )
        _allDemoMessagesFlow.value = (_allDemoMessagesFlow.value + newMessage).sortedBy { it.timestamp }
        return Result.success(newMessage.id)
    }

    override fun getOrCreateDemoConversationId(userId1: String, userId2: String): Flow<String> {
        return flow {
            emit(generateConversationIdInternal(userId1, userId2))
        }
    }

    override suspend fun markDemoMessagesAsRead(conversationId: String, readerUserId: String) {
        delay(100) // Simulate delay
        _allDemoMessagesFlow.value = _allDemoMessagesFlow.value.map { message ->
            if (message.conversationId == conversationId && message.receiverId == readerUserId && !message.isRead) {
                message.copy(isRead = true)
            } else {
                message
            }
        }
        // No need to re-sort if only isRead changes, as order by timestamp is preserved.
    }
}