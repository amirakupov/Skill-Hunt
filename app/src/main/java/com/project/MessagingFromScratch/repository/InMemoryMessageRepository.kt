@file:OptIn(kotlinx.coroutines.ExpworkierimentalCoroutinesApi::class) // For flatMapLatest etc.

package com.project.MessagingFromScratch.repository

// Kotlin & Coroutines
import androidx.compose.foundation.layout.size
import androidx.compose.ui.geometry.isEmpty
import androidx.compose.ui.test.filter
import androidx.paging.map
import androidx.preference.isNotEmpty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch // For repositoryScope tasks

// Java Standard Library
import java.util.Date
import java.util.UUID

// Javax Inject (for Hilt/Dagger - keep if you plan to use it)
import javax.inject.Inject
import javax.inject.Singleton // Add if making this a Hilt Singleton

// Project's App-Specific Local/Custom Types from data.model
import com.project.MessagingFromScratch.data.model.ConversationSnippet
import com.project.MessagingFromScratch.data.model.Message // Your defined Message class

// Demo Repository
import com.project.MessagingFromScratch.repository.DemoRepository
import com.project.MessagingFromScratch.repository.DemoRepositoryImpl // For constants & init check

// DOMAINLAYER Imports
import com.project.DOMAINLAYER.MessageDataType
import com.project.DOMAINLAYER.UserDataType
import com.project.DOMAINLAYER.usecase15.AllUsersUnderTheSunMoonAndStars
import com.project.DOMAINLAYER.usecase15.ConversationData // Domain representation of a conversation
import com.project.DOMAINLAYER.usecase15.ConversationMessagesStore
import com.project.DOMAINLAYER.usecase15.CurrentLoggedInUser
import com.project.DOMAINLAYER.usecase15.UserActiveConversations


// --- Constants for Demo Mode (using values from DemoRepositoryImpl as before) ---
const val USER_ID_ME: String = DemoRepositoryImpl.DEMO_USER_ID_ME
val USER_NAMES: Map<String, String> = DemoRepositoryImpl.DEMO_USER_NAMES

@Singleton // Assuming you'll use Hilt. Remove if manual DI.
class InMemoryMessageRepository @Inject constructor( // Hilt needs @Inject constructor
    private val demoRepository: DemoRepository,
    // The DOMAINLAYER objects are singletons, so no need to inject them if accessing directly.
    // However, for better testability, you *could* inject them. For now, direct access.
    private val applicationScope: CoroutineScope = CoroutineScope(Dispatchers.Default) // For general tasks
) : MessageRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.IO) // For IO-bound fallback ops

    init {
        if (demoRepository is DemoRepositoryImpl) {
            // demoRepository.startFullDemoSimulation() // Call this if needed for demo setup
        }
        // Example: Prime the CurrentLoggedInUser from demo if it's empty on startup
        // This simulates an auth module's responsibility.
        applicationScope.launch {
            if (CurrentLoggedInUser.dataFlow.firstOrNull() == null) {
                // Attempt to get a user from demo to represent "Me"
                val demoMeUser = demoRepository.getDemoUserById(USER_ID_ME) // Needs this method in DemoRepository
                if (demoMeUser != null) { // demoMeUser should be UserDataType
                    CurrentLoggedInUser.update(demoMeUser)
                }
            }
            // Prime AllUsers if empty
            if (AllUsersUnderTheSunMoonAndStars.allUsersDataFlow.firstOrNull().isNullOrEmpty()) {
                val allDemoUsers = demoRepository.getAllDemoUsers() // Needs this method, returns List<UserDataType>
                if (allDemoUsers.isNotEmpty()) {
                    AllUsersUnderTheSunMoonAndStars.setAllUsers(allDemoUsers)
                }
            }
        }
    }

    // --- Transformation Helpers ---

    private fun UserDataType.toConversationParticipantName(): String {
        return this.displayName ?: "Unknown User"
    }

    // From Domain ConversationData to App ConversationSnippet
    private fun ConversationData.toConversationSnippet(currentUserId: String): ConversationSnippet {
        // partnerDetails in ConversationData is UserDataType
        val partner = this.partnerDetails
        return ConversationSnippet(
            conversationId = this.conversationId,
            otherUserId = partner.id,
            otherUserName = partner.toConversationParticipantName(),
            lastMessageText = this.lastMessagePreview, // lastMessagePreview might need "You: " stripping if it's from current user.
            lastMessageTimestamp = this.lastMessageTimestamp?.let { Date(it) }, // Long to Date
            unreadCount = if (this.partnerId != currentUserId) this.unreadMessageCount else 0, // Unread count is for messages *from* partner
            otherUserProfileImageUrl = partner.profileImageUrl
        )
    }

    // From Domain MessageDataType to App Message
    // Needs access to all users to get senderName if MessageDataType only has senderId
    private fun MessageDataType.toAppMessage(
        loggedInUserId: String,
        allUsers: List<UserDataType> // To find sender name
    ): Message {
        val sender = allUsers.find { it.id == this.senderId }
        val receiverId = if (this.senderId == loggedInUserId) {
            // If sender is loggedInUser, receiver is the other participant in conversation
            this.conversationId.split("_").find { it != loggedInUserId } ?: ""
        } else {
            // If sender is not loggedInUser, then loggedInUser is the receiver
            loggedInUserId
        }

        return Message(
            id = this.messageId,
            conversationId = this.conversationId,
            senderId = this.senderId,
            receiverId = receiverId, // Crucial: determine this correctly
            text = this.text,
            timestamp = Date(this.timestamp), // Long to Date
            // isRead: MessageDataType doesn't have this. isRead in App model is UI-driven.
            // When converting *from* domain, assume false unless domain layer gets read status.
            isRead = false, // Default to false; actual read status is UI-driven or needs domain support
            senderName = sender?.displayName ?: "Unknown Sender"
        )
    }

    // From App Message to Domain MessageDataType (Needed for sendMessage)
    private fun Message.toDomainMessageDataType(viewingUserId: String): MessageDataType {
        return MessageDataType(
            messageId = this.id,
            conversationId = this.conversationId,
            senderId = this.senderId, // App Message's senderId
            text = this.text,
            timestamp = this.timestamp.time, // Convert Date to Long
            // isSentByViewingUser: if the app message's senderId is the current viewing user
            isSentByViewingUser = (this.senderId == viewingUserId)
            // receiverId, isRead, senderName from app.Message are not part of MessageDataType
        )
    }

    // This replaces your previous getCurrentActualUserId()
    // It directly observes the CurrentLoggedInUser domain object.
    private fun getActualCurrentUserIdFlow(): Flow<String?> {
        return CurrentLoggedInUser.dataFlow.map { it?.id }
    }


    override fun getConversationSnippets(userId: String): Flow<List<ConversationSnippet>> {
        return getActualCurrentUserIdFlow().flatMapLatest { actualLoggedInUserId ->
            // Combine with UserActiveConversations and AllUsers for transformations
            UserActiveConversations.conversationsFlow
                .combine(AllUsersUnderTheSunMoonAndStars.allUsersDataFlow) { conversations, allUsers ->
                    Pair(conversations, allUsers)
                }
                .map { (domainConversations, allUsers) ->
                    val ownerOfDomainConversations = UserActiveConversations.getOwnerUserId()

                    if (actualLoggedInUserId == userId) {
                        // User is logged in and requesting their own snippets
                        if (ownerOfDomainConversations == userId && domainConversations.isNotEmpty()) {
                            domainConversations.map { convData ->
                                convData.toConversationSnippet(userId)
                            }
                        } else if (ownerOfDomainConversations != userId || domainConversations.isEmpty()) {
                            // Domain layer is empty for this user or for someone else, try demo
                            // This part needs to call demoRepository.getDemoConversationSnippets
                            // and then adapt its output if it's not already List<ConversationSnippet>
                            // For now, assuming demoRepository.getDemoConversationSnippets(userId) returns Flow<List<ConversationSnippet>>
                            // This needs to be handled carefully if demo returns different types.
                            // For simplicity, let's assume we'll use a separate flow for demo if domain is empty.
                            emptyList<ConversationSnippet>() // Placeholder for demo fallback logic
                        } else {
                            emptyList<ConversationSnippet>()
                        }
                    } else if (USER_NAMES.containsKey(userId)) {
                        // Requesting snippets for a known demo user (not the logged-in user)
                        // This path will predominantly use demoRepository.
                        // Similar to above, careful handling of demo output type is needed.
                        emptyList<ConversationSnippet>() // Placeholder for demo fallback logic
                    } else {
                        // Unknown user and not logged in as this user
                        emptyList<ConversationSnippet>()
                    }
                }
                .flatMapLatest { snippets ->
                    // Fallback logic if snippets from domain are empty
                    if (snippets.isEmpty()) {
                        if (actualLoggedInUserId == userId || USER_NAMES.containsKey(userId)) {
                            // If it was for the logged-in user OR a known demo user and domain was empty, try demo.
                            demoRepository.getDemoConversationSnippets(userId)
                                .catch { emit(emptyList()) } // Ensure flow continues on demo error
                        } else {
                            flowOf(emptyList<ConversationSnippet>())
                        }
                    } else {
                        flowOf(snippets)
                    }
                }
                .onStart {
                    // Prime UserActiveConversations from demo if it's empty for the requested userId
                    // (and they are the logged-in user or a known demo user)
                    val currentDomainOwner = UserActiveConversations.getOwnerUserId()
                    val currentDomainConversations = UserActiveConversations.conversationsFlow.value
                    if ((actualLoggedInUserId == userId || USER_NAMES.containsKey(userId)) &&
                        (currentDomainOwner != userId || currentDomainConversations.isEmpty())
                    ) {
                        applicationScope.launch { // Use applicationScope for priming
                            val demoSnips = demoRepository.getDemoConversationSnippets(userId).firstOrNull()
                            if (!demoSnips.isNullOrEmpty()) {
                                // We need to convert Demo Snippets to Domain ConversationData
                                // This requires more info about Demo Snippets structure or a conversion path.
                                // This part is complex if Demo Snippet doesn't map easily to ConversationData.
                                // For now, we'll assume a direct priming isn't straightforward without more info.
                                // A proper solution might involve demoRepository providing ConversationData directly.
                                println("Priming UserActiveConversations from demo for $userId: Conversion needed from DemoSnippet to ConversationData")

                                // Ideal: demoRepository.getDemoConversationData(userId): Flow<List<ConversationData>>
                                // If demoRepository.getDemoConversationData(userId) existed:
                                // val demoDomainConvs = demoRepository.getDemoConversationData(userId).firstOrNull()
                                // if (!demoDomainConvs.isNullOrEmpty()) {
                                //     val partnerIds = demoDomainConvs.map { it.partnerId }
                                //     val partnersFromAllUsers = AllUsersUnderTheSunMoonAndStars.allUsersDataFlow.value
                                //     partnerIds.forEach { pId ->
                                //          if (partnersFromAllUsers.none { it.id == pId}) {
                                //              demoRepository.getDemoUserById(pId)?.let { demoPartner ->
                                //                  AllUsersUnderTheSunMoonAndStars.addUser(demoPartner) // hypothetical
                                //              }
                                //          }
                                //     }
                                //     UserActiveConversations.updateConversationsForUser(userId, demoDomainConvs)
                                // }
                            }
                        }
                    }
                }
        }
    }


    override fun getMessages(conversationId: String): Flow<List<Message>> {
        return getActualCurrentUserIdFlow().flatMapLatest { actualLoggedInUserId ->
            // Combine with ConversationMessagesStore and AllUsers for transformations
            ConversationMessagesStore.messagesFlow
                .combine(AllUsersUnderTheSunMoonAndStars.allUsersDataFlow) { messages, allUsers ->
                    Pair(messages, allUsers)
                }
                .combine(ConversationMessagesStore.activeConversationIdFlow) { (messagesAndUsers, activeConvId), ->
                    Triple(messagesAndUsers.first, messagesAndUsers.second, activeConvId)
                }
                .map { (domainMessages, allUsers, activeDomainConvId) ->
                    val loggedInUserIdToUse = actualLoggedInUserId ?: USER_ID_ME // Fallback for context

                    if (activeDomainConvId == conversationId && domainMessages.isNotEmpty()) {
                        domainMessages
                            .filter { it.conversationId == conversationId } // Ensure messages are for THIS conversation
                            .map { msgData -> msgData.toAppMessage(loggedInUserIdToUse, allUsers) }
                    } else {
                        // Domain layer is empty for this conversation or for a different one
                        emptyList<Message>() // Placeholder for demo fallback logic
                    }
                }
                .flatMapLatest { messages ->
                    if (messages.isEmpty()) {
                        // If domain provided no messages (or wrong ones), try demo.
                        // This logic needs to align with your original conditions for using demo.
                        val knownDemoUserIds = USER_NAMES.keys
                        val isPotentiallyDemoConversation = knownDemoUserIds.any { demoUserId -> conversationId.contains(demoUserId) }
                        if (isPotentiallyDemoConversation || (actualLoggedInUserId == null && conversationId.contains(USER_ID_ME))) {
                            demoRepository.getDemoMessages(conversationId) // This is Flow<List<Message>>
                                .catch { emit(emptyList()) }
                        } else {
                            flowOf(emptyList<Message>())
                        }
                    } else {
                        flowOf(messages)
                    }
                }
                .onStart {
// Prime ConversationMessagesStore from demo if needed
                    val currentMessagesInStore = ConversationMessagesStore.messagesFlow.value
                    val activeConvInStore = ConversationMessagesStore.activeConversationIdFlow.value

                    // Check if the store is not set for this conversationId, or if it is, but it's empty.
                    if (activeConvInStore != conversationId || currentMessagesInStore.filter { it.conversationId == conversationId }.isEmpty()) {
                        applicationScope.launch {
                            val demoMsgsApp = demoRepository.getDemoMessages(conversationId).firstOrNull() // Returns app.Message
                            if (!demoMsgsApp.isNullOrEmpty()) {
                                val loggedInUserIdToUseForDomainConversion = actualLoggedInUserId ?: USER_ID_ME
                                val domainMsgsToStore = demoMsgsApp.map { appMsg ->
                                    // We need to convert app.Message (from demo) to Domain MessageDataType
                                    // The existing toDomainMessageDataType helper is suitable here.
                                    appMsg.toDomainMessageDataType(loggedInUserIdToUseForDomainConversion)
                                }

                                // Ensure all users involved in these demo messages are known to AllUsersUnderTheSunMoonAndStars
                                // This is crucial because MessageDataType refers to users by ID.
                                val involvedUserIds = domainMsgsToStore.flatMap { listOf(it.senderId) }.distinct()
                                val currentKnownUsers = AllUsersUnderTheSunMoonAndStars.allUsersDataFlow.value.associateBy { it.id }
                                val usersToAdd = mutableListOf<UserDataType>()

                                involvedUserIds.forEach { userId ->
                                    if (!currentKnownUsers.containsKey(userId)) {
                                        // User not in AllUsers, try to get from demo
                                        demoRepository.getDemoUserById(userId)?.let { demoUser ->
                                            // demoUser is UserDataType, add it to our list for AllUsers
                                            usersToAdd.add(demoUser)
                                        }
                                    }
                                }
                                if (usersToAdd.isNotEmpty()) {
                                    // Add all newly found demo users to AllUsersUnderTheSunMoonAndStars
                                    // A method like `addUsersBatch` or repeated `addUser` would be needed in AllUsers.
                                    // For simulation:
                                    AllUsersUnderTheSunMoonAndStars.setAllUsers(
                                        AllUsersUnderTheSunMoonAndStars.allUsersDataFlow.value + usersToAdd
                                    )
                                    println("Primed AllUsers with ${usersToAdd.size} users from demo messages for $conversationId.")
                                }

                                // Now, set the messages for the conversation in the domain store
                                ConversationMessagesStore.setMessagesForConversation(conversationId, domainMsgsToStore)
                                println("Primed ConversationMessagesStore for $conversationId with ${domainMsgsToStore.size} demo messages.")
                            }
                        }
                    }
                }
        }
    }

    // This aligns with your original sendMessage signature
    override suspend fun sendMessage(
        conversationId: String,
        senderId: String, // This is the ID of who is trying to send
        receiverId: String, // This is the ID of the recipient
        text: String
    ): Result<String> { // Result<String> where String is new message ID or error.

        val actualLoggedInUser = CurrentLoggedInUser.dataFlow.firstOrNull() // Get current real user from Domain

        // Determine if this operation should use the demo repository
        val isSenderMePlaceholder = senderId == USER_ID_ME // "userMe" can mean the logged-in user OR a demo context
        val isSenderExplicitDemo = USER_NAMES.containsKey(senderId) && senderId != USER_ID_ME
        val isReceiverExplicitDemo = USER_NAMES.containsKey(receiverId) && receiverId != USER_ID_ME

        // Conditions for using demo:
        // 1. Sender is an explicit demo user (not "userMe").
        // 2. Receiver is an explicit demo user.
        // 3. Sender is the "userMe" placeholder AND no actual user is logged in.
        // 4. Sender is the "userMe" placeholder AND an actual user IS logged in, BUT the receiver is an explicit demo user.
        val useDemoForSend = isSenderExplicitDemo ||
                isReceiverExplicitDemo ||
                (isSenderMePlaceholder && actualLoggedInUser == null) ||
                (isSenderMePlaceholder && actualLoggedInUser != null && isReceiverExplicitDemo)


        if (useDemoForSend) {
            // Delegate to DemoRepository as per your original logic
            return demoRepository.sendDemoMessage(conversationId, senderId, receiverId, text)
        } else {
            // This is for a "real" send operation, involving the actual logged-in user
            if (actualLoggedInUser == null) {
                return Result.failure(IllegalStateException("Cannot send message: No user is logged in for a non-demo send."))
            }

// If senderId is USER_ID_ME, it implies the sender is the actualLoggedInUser.
            // If senderId is different, it must match the actualLoggedInUser.id.
            val finalSenderId = if (isSenderMePlaceholder) {
                actualLoggedInUser.id
            } else if (senderId == actualLoggedInUser.id) {
                actualLoggedInUser.id
            } else {
                return Result.failure(IllegalStateException("Sender ID ($senderId) does not match logged in user (${actualLoggedInUser.id}) for a real send."))
            }

            val newMessageId = UUID.randomUUID().toString()
            val newMessageTimestamp = System.currentTimeMillis()

            // Create the app-level Message (as if it was created by UI before calling repo)
            val appMessage = com.project.MessagingFromScratch.data.model.Message(
                id = newMessageId,
                conversationId = conversationId,
                senderId = finalSenderId,
                receiverId = receiverId,
                text = text,
                timestamp = Date(newMessageTimestamp),
                isRead = false, // New messages are unread by default
                senderName = actualLoggedInUser.displayName // Name of the logged-in user
            )

            // Convert to Domain MessageDataType to update domain stores
            val domainMessage = appMessage.toDomainMessageDataType(finalSenderId) // viewingUserId is finalSenderId

            // "Send" it - this involves updating the domain layer stores
            repositoryScope.launch {
                // 1. Update ConversationMessagesStore
                if (ConversationMessagesStore.activeConversationIdFlow.value == conversationId) {
                    val currentMessages = ConversationMessagesStore.messagesFlow.value.toMutableList()
                    currentMessages.add(domainMessage)
                    ConversationMessagesStore.setMessagesForConversation(conversationId, currentMessages.sortedBy { it.timestamp })
                } else {
                    // If not active, the message is still "sent". It will appear when the conversation is loaded.
                    // For a robust system, you might want to ensure the conversation and its messages are loaded here.
                    // For now, assume it's stored and will be picked up.
                    println("Message ($newMessageId) sent to $conversationId (not active in store). Will show when loaded.")
                    // To be more proactive, you could load and then add:
                    // val existingMessages = ConversationMessagesStore.getMessagesForConversation(conversationId) // hypothetical direct access
                    // ConversationMessagesStore.setMessagesForConversation(conversationId, existingMessages + domainMessage)
                }

                // 2. Update UserActiveConversations for the sender (actualLoggedInUser)
                val activeConversations = UserActiveConversations.conversationsFlow.value.toMutableList()
                val targetConvIndex = activeConversations.indexOfFirst { it.conversationId == conversationId }

                val partnerDetails = AllUsersUnderTheSunMoonAndStars.findUserById(receiverId)
                    ?: demoRepository.getDemoUserById(receiverId) // Fallback to get partner details for ConversationData

                if (partnerDetails == null && !isReceiverExplicitDemo) { // If receiver isn't a known demo user, we need them in AllUsers
                    println("Warning: Could not find partner details for $receiverId to update/create conversation snippet. Message sent, but snippet may be affected.")
                    // The message is "sent" to ConversationMessagesStore, but snippet might be missing/stale.
                }

                if (targetConvIndex != -1) { // Existing conversation
                    val oldConvData = activeConversations[targetConvIndex]
                    // Ensure partnerDetails in existing conv data is up-to-date if possible
                    // This is important if partner details might have changed in AllUsersUnderTheSunMoonAndStars
                    val finalPartnerDetails = AllUsersUnderTheSunMoonAndStars.findUserById(oldConvData.partnerId)
                        ?: oldConvData.partnerDetails // Use existing if not found or no change

                    val updatedConvData = oldConvData.copy(
                        partnerDetails = finalPartnerDetails, // Update partner details
                        lastMessagePreview = "You: $text", // Assuming sender is "You"
                        lastMessageTimestamp = newMessageTimestamp,
                        // Unread count for the sender's perspective of this conversation is 0.
                        // The recipient's unread count would be handled on their end.
                        unreadMessageCount = 0
                    )
                    activeConversations[targetConvIndex] = updatedConvData
                } else if (partnerDetails != null) {
                    // Create new ConversationData because this conversation wasn't in UserActiveConversations for the sender
                    val newConvData = ConversationData(
                        conversationId = conversationId,
                        partnerId = receiverId,
                        partnerDetails = partnerDetails,
                        lastMessagePreview = "You: $text",
                        lastMessageTimestamp = newMessageTimestamp,
                        unreadMessageCount = 0 // New conversation for this sender, no unread messages.
                    )
                    activeConversations.add(newConvData)
                }
                // Only update UserActiveConversations if we successfully found/created data.
                // And ensure the owner of these conversations is the sender.
                if ((partnerDetails != null || targetConvIndex != -1) && UserActiveConversations.getOwnerUserId() == finalSenderId) {
                    UserActiveConversations.updateConversationsForUser(
                        finalSenderId, // Update for the actual sender
                        activeConversations.sortedByDescending { it.lastMessageTimestamp }
                    )
                } else if ((partnerDetails != null || targetConvIndex != -1) && UserActiveConversations.getOwnerUserId() != finalSenderId) {
                    // This case means UserActiveConversations is currently holding data for someone else.
                    // We should still update the sender's conversations, effectively replacing the store's content.
                    // This scenario should ideally be handled carefully, perhaps by ensuring UserActiveConversations
                    // is always for the logged-in user or has a mechanism to switch contexts.
                    // For now, we proceed to update for finalSenderId.
                    UserActiveConversations.updateConversationsForUser(
                        finalSenderId,
                        activeConversations.filter { it.conversationId == conversationId || targetConvIndex != -1 } // Only relevant conversations
                            .sortedByDescending { it.lastMessageTimestamp }
                    )
                    println("UserActiveConversations store was for a different user. Updated for sender $finalSenderId.")
                }


            }.join() // Ensure the launched coroutine completes before returning the Result

            return Result.success(newMessageId)
        }
    }

    override fun getOrCreateConversationId(userId1: String, userId2: String): Flow<String> {
        return flow {
            val actualLoggedInUser = CurrentLoggedInUser.dataFlow.firstOrNull()
            // Resolve USER_ID_ME to actual logged-in user's ID if applicable
            val u1Resolved = if (userId1 == USER_ID_ME && actualLoggedInUser != null) actualLoggedInUser.id else userId1
            val u2Resolved = if (userId2 == USER_ID_ME && actualLoggedInUser != null) actualLoggedInUser.id else userId2

            // Determine if resolved IDs are demo users (excluding the case where resolved ID is the logged-in user)
            val isU1Demo = USER_NAMES.containsKey(u1Resolved) && u1Resolved != actualLoggedInUser?.id
            val isU2Demo = USER_NAMES.containsKey(u2Resolved) && u2Resolved != actualLoggedInUser?.id


            if (isU1Demo || isU2Demo || (actualLoggedInUser == null && (u1Resolved == USER_ID_ME || u2Resolved == USER_ID_ME))) {
                // Delegate to demo if:
                // 1. Either resolved user is an explicit demo user (and not the currently logged-in real user).
                // 2. No real user is logged in, and "userMe" is one of the participants (implying a demo context).
                emitAll(demoRepository.getOrCreateDemoConversationId(u1Resolved, u2Resolved))
            } else {
                // For real users (or a real user interacting with "userMe" now resolved to themselves),
                // construct the ID predictively.
                // A more robust system might check UserActiveConversations or a dedicated domain service.
                val sortedIds = listOf(u1Resolved, u2Resolved).sorted()
                val realConversationId = "${sortedIds[0]}_${sortedIds[1]}"
                emit(realConversationId)
            }
        }
    }

    Understood. We'll resume from line 425, which is updating `partnerDetails` in an existing conversation within the `sendMessage` method.


    ```kotlin
    partnerDetails = finalPartnerDetails, // Update partner details
    lastMessagePreview = "You: $text", // Assuming sender is "You"
    lastMessageTimestamp = newMessageTimestamp,
    // Unread count for the sender's perspective of this conversation is 0.
    // The recipient's unread count would be handled on their end.
    unreadMessageCount = 0
    )
    activeConversations[targetConvIndex] = updatedConvData
} else if (partnerDetails != null) {
    // Create new ConversationData because this conversation wasn't in UserActiveConversations for the sender
    val newConvData = ConversationData(
        conversationId = conversationId,
        partnerId = receiverId,
        partnerDetails = partnerDetails,
        lastMessagePreview = "You: $text",
        lastMessageTimestamp = newMessageTimestamp,
        unreadMessageCount = 0 // New conversation for this sender, no unread messages.
    )
    activeConversations.add(newConvData)
}
// Only update UserActiveConversations if we successfully found/created data.
if (partnerDetails != null || targetConvIndex != -1) {
    UserActiveConversations.updateConversationsForUser(
        finalSenderId, // Update for the actual sender
        activeConversations.sortedByDescending { it.lastMessageTimestamp }
    )
}
}.join() // Ensure the launched coroutine completes before returning the Result

return Result.success(newMessageId)
}
}

override fun getOrCreateConversationId(userId1: String, userId2: String): Flow<String> {
    // This method needs to align with how conversation IDs are managed,
    // especially considering the DOMAINLAYER objects.
    // A conversation ID is typically stable between two users.

    return flow {
        val actualLoggedInUser = CurrentLoggedInUser.dataFlow.firstOrNull()
        val u1 = if (userId1 == USER_ID_ME && actualLoggedInUser != null) actualLoggedInUser.id else userId1
        val u2 = if (userId2 == USER_ID_ME && actualLoggedInUser != null) actualLoggedInUser.id else userId2

        val isU1Demo = USER_NAMES.containsKey(u1) && u1 != (actualLoggedInUser?.id ?: "")
        val isU2Demo = USER_NAMES.containsKey(u2) && u2 != (actualLoggedInUser?.id ?: "")

        if (isU1Demo || isU2Demo || (actualLoggedInUser == null && (u1 == USER_ID_ME || u2 == USER_ID_ME))) {
            // If any user is explicitly demo or if "userMe" is involved without a real logged-in user,
            // delegate to demo repository.
            emitAll(demoRepository.getOrCreateDemoConversationId(u1, u2))
        } else {
            // For real users, the conversation ID is usually a sorted combination of their IDs.
            // The DOMAINLAYER (UserActiveConversations or ConversationMessagesStore) would be the source
            // of truth if a conversation already exists.
            // If UserActiveConversations holds ConversationData, it would have the ID.
            // For now, we construct it predictively.
            val sortedIds = listOf(u1, u2).sorted()
            val realConversationId = "${sortedIds[0]}_${sortedIds[1]}"

            // Optionally, check if this conversation already exists in UserActiveConversations
            // for the loggedInUser (if applicable and one of u1/u2 is the loggedInUser)
            // For simplicity here, we just return the constructed ID.
            // A more robust solution might involve querying UserActiveConversations.
            emit(realConversationId)
        }
    }
}

override suspend fun markMessagesAsRead(conversationId: String, readerUserId: String) {
    val actualLoggedInUserId = CurrentLoggedInUser.dataFlow.firstOrNull()?.id

    // First, handle demo repository marking if it's a demo conversation context
    // This logic is from your original snippet and seems reasonable for demo interaction.
    val knownDemoUserIds = USER_NAMES.keys
    val isPotentiallyDemoConversation = knownDemoUserIds.any { demoUserId -> conversationId.contains(demoUserId) }

    if (isPotentiallyDemoConversation || USER_NAMES.containsKey(readerUserId) || (readerUserId == USER_ID_ME && actualLoggedInUserId == null)) {
        demoRepository.markDemoMessagesAsRead(conversationId, readerUserId)
    }

    // Now, handle the DOMAINLAYER marking if the reader is the actual logged-in user.
    if (readerUserId == actualLoggedInUserId) {
        // The reader is the currently logged-in real user.
        // We need to update MessageDataType objects in ConversationMessagesStore.
        // MessageDataType does NOT have an `isRead` field.
        // This implies that "read" status in the domain might be managed differently,
        // perhaps by updating the `unreadMessageCount` in `ConversationData` for the *other* user,
        // or by an event system.

        // For UserActiveConversations:
        // If `readerUserId` has a conversation with `conversationId`,
        // their `unreadMessageCount` for *that specific conversation* should become 0.
        val currentConversations = UserActiveConversations.conversationsFlow.value.toMutableList()
        val targetConvIndex = currentConversations.indexOfFirst {
            it.conversationId == conversationId && UserActiveConversations.getOwnerUserId() == actualLoggedInUserId
        }

        if (targetConvIndex != -1) {
            val updatedConv = currentConversations[targetConvIndex].copy(unreadMessageCount = 0)
            currentConversations[targetConvIndex] = updatedConv
            UserActiveConversations.updateConversationsForUser(actualLoggedInUserId, currentConversations)
            println("Domain: Marked conversation $conversationId as read for user $actualLoggedInUserId by clearing unread count.")
        } else {
            println("Domain: Conversation $conversationId not found for user $actualLoggedInUserId to mark as read.")
        }

        // For ConversationMessagesStore:
        // Since MessageDataType doesn't have `isRead`, there's no direct field to update on messages themselves
        // in the domain layer store. The `isRead` flag in your app-level `Message` model
        // is primarily for UI display and would be set when messages are fetched and transformed.
        // The act of fetching messages for a conversation by the `readerUserId` could implicitly
        // mean they are "read" from a UI perspective.
        // If `MessageDataType` *were* to gain an `isReadBy[userId]` map or similar,
        // then we could update that here.
    }
    // No explicit 'else' for domain if reader is not the logged-in user, as they can't mark others' messages.
} // End of nice long InMemoryMessageRepository class
