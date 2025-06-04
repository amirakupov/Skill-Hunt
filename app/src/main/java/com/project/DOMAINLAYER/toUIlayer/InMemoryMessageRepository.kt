// File: /MessagingFromScratch/repository/InMemoryMessageRepository.kt
package com.project.DOMAINLAYER.toUIlayer

// Kotlin & Coroutines
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
// import kotlinx.coroutines.flow.map // Only if use .map directly on a Flow somewhere not shown

// Java Standard Library
import java.util.UUID

// Javax Inject (for Hilt/Dagger)

// Project's Local/Custom Types
import com.project.DOMAINLAYER.fromDataLayer.model.ConversationSnippet
import com.project.DOMAINLAYER.fromDataLayer.model.Message
import com.project.DOMAINLAYER.usecase15.DemoRepository
import com.project.DOMAINLAYER.fromDataLayer.DemoRepositoryImpl
import com.project.DOMAINLAYER.usecase15.MessageRepository

// TODO: Import REAL repository interfaces from DOMAINLAYER if/when integrate them
// For example:
// import com.project.DOMAINLAYER.repository.UserSessionRepository
// import com.project.DOMAINLAYER.repository.RealMessageRepository

// --- Constants for Demo Mode ---
const val USER_ID_ME: String = DemoRepositoryImpl.DEMO_USER_ID_ME
val USER_NAMES: Map<String, String> = DemoRepositoryImpl.DEMO_USER_NAMES

// InMemoryMessageRepository.kt
// @Inject // Make sure this is commented out or removed for manual DI test
class InMemoryMessageRepository constructor(
    private val demoRepository: DemoRepository
    // ...
) : MessageRepository {  // Ensure MessageRepository interface is correctly defined and its methods match

    init {
        // If demoRepository is always DemoRepositoryImpl and want to start its simulation
        if (demoRepository is DemoRepositoryImpl) {
            demoRepository.startFullDemoSimulation()
        }
    }

    private suspend fun getCurrentActualUserId(): String? {
        // TODO: Replace with actual logic from UserSessionRepository or equivalent
        // return userSessionRepository.getCurrentUserId() // Example if using UserSessionRepository
        return null // Placeholder
    }

    override fun getConversationSnippets(userId: String): Flow<List<ConversationSnippet>> {
        return flow { emit(getCurrentActualUserId()) }.flatMapLatest { currentActualUserIdValue ->
            if (userId == USER_ID_ME) {
                demoRepository.getDemoConversationSnippets(userId).flatMapLatest { demoSnippets ->
                    if (demoSnippets.isNotEmpty()) {
                        flowOf(demoSnippets)
                    } else {
                        flowOf(emptyList())
                    }
                }
            } else if (currentActualUserIdValue != null && userId == currentActualUserIdValue) {
                flowOf(emptyList<ConversationSnippet>()) // Placeholder for real user's snippets
            } else {
                if (USER_NAMES.containsKey(userId)) {
                    demoRepository.getDemoConversationSnippets(userId)
                } else {
                    flowOf(emptyList())
                }
            }
        }
    }

    override fun getMessages(conversationId: String): Flow<List<Message>> {
        val knownDemoUserIds = USER_NAMES.keys
        val isPotentiallyDemoConversation = knownDemoUserIds.any { demoUserId -> conversationId.contains(demoUserId) }

        if (isPotentiallyDemoConversation) {
            return demoRepository.getDemoMessages(conversationId).flatMapLatest { demoMessages ->
                if (demoMessages.isNotEmpty()) {
                    flowOf(demoMessages)
                } else {
                    flowOf(emptyList<Message>()) // Placeholder
                }
            }
        } else {
            return flowOf(emptyList<Message>()) // Placeholder for real messages
        }
    }

    override suspend fun sendMessage(
        conversationId: String,
        senderId: String,
        receiverId: String,
        text: String
    ): Result<String> {
        val currentActualUserIdValue = getCurrentActualUserId()

        val isSenderDemoOnly = USER_NAMES.containsKey(senderId) && senderId != USER_ID_ME
        val isReceiverDemoOnly = USER_NAMES.containsKey(receiverId) && receiverId != USER_ID_ME

        val useDemoRepositoryForSend = isSenderDemoOnly || isReceiverDemoOnly ||
                (senderId == USER_ID_ME && currentActualUserIdValue == null) ||
                (senderId == USER_ID_ME && USER_NAMES.containsKey(receiverId))

        if (useDemoRepositoryForSend) {
            return demoRepository.sendDemoMessage(conversationId, senderId, receiverId, text)
        } else {
            val actualSenderId: String
            if (senderId == USER_ID_ME && currentActualUserIdValue != null) {
                actualSenderId = currentActualUserIdValue
            } else if (currentActualUserIdValue != null && senderId == currentActualUserIdValue) {
                actualSenderId = currentActualUserIdValue
            } else {
                if (currentActualUserIdValue != senderId && senderId != USER_ID_ME) {
                    return Result.failure(IllegalStateException("Sender ID ($senderId) does not match logged in user ($currentActualUserIdValue) and is not a demo user."))
                }
                if (currentActualUserIdValue == null && senderId != USER_ID_ME) {
                    return Result.failure(IllegalStateException("Cannot send as specific user ($senderId) without being logged in or it being a demo user."))
                }
                actualSenderId = senderId
            }
            // TODO: Replace with actual repository call
            // return realMessageRepository.sendMessage(conversationId, actualSenderId, receiverId, text)
            return Result.success("mockRealMessageId-${UUID.randomUUID()}")
        }
    }

    override fun getOrCreateConversationId(userId1: String, userId2: String): Flow<String> {
        val isUserId1Demo = USER_NAMES.containsKey(userId1)
        val isUserId2Demo = USER_NAMES.containsKey(userId2)

        if (isUserId1Demo || isUserId2Demo) {
            return demoRepository.getOrCreateDemoConversationId(userId1, userId2)
        } else {
            // TODO: Replace with actual repository call
            // return realMessageRepository.getOrCreateConversationId(userId1, userId2)
            return flowOf(if (userId1 < userId2) "real-$userId1-$userId2" else "real-$userId2-$userId1")
        }
    }

    override suspend fun markMessagesAsRead(conversationId: String, readerUserId: String) {
        val knownDemoUserIds = USER_NAMES.keys
        val isPotentiallyDemoConversation = knownDemoUserIds.any { demoUserId -> conversationId.contains(demoUserId) }

        if (isPotentiallyDemoConversation) {
            // It's possible the reader is DEMO_USER_ID_ME or another demo user,
            // or a real user reading a conversation involving demo users.
            demoRepository.markDemoMessagesAsRead(conversationId, readerUserId)
        }

        // Now, consider the real repository, regardless of demo status,
        // if the readerUserId matches an actual logged-in user.
        // This handles cases where a real user is reading messages that might also exist in demo,
        // or a purely real conversation.
        val currentActualUserIdValue = getCurrentActualUserId()
        if (readerUserId == currentActualUserIdValue) {
            // The reader is the currently logged-in real user.
            // TODO: Replace with actual repository call to mark messages as read for the real user.
            // realMessageRepository.markMessagesAsRead(conversationId, readerUserId)
            // For placeholder, no operation needed for suspend function if it just makes a call.
            // Log or print if want to see this path taken:
            // println("Placeholder: Real user $readerUserId marking messages as read for conversation $conversationId")
        }
        // No explicit 'else' needed here if the only action for non-matching readerId
        // or non-real user is to do nothing further in the real repository.
        // The demo repository handling is separate and already done if applicable.
    }

} // End of InMemoryMessageRepository class