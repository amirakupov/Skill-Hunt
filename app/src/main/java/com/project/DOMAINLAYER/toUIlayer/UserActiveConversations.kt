package com.project.DOMAINLAYER.toUIlayer

// File: Skill-Hunt/app/src/main/java/com/project/DOMAINLAYER/usecase15/UserActiveConversations.kt

import com.project.DOMAINLAYER.UserDataType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * FOR TEAM: Represents a summary of a conversation for the conversation list.
 * system will need to map its conversation data to this structure when calling
 * `UserActiveConversations.updateConversationsForUser`.
 */
data class ConversationData(
    val conversationId: String,
    val partnerId: String,
    val partnerDetails: UserDataType, // Instance of com.project.DOMAINLAYER.UserDataType
    val lastMessagePreview: String?,
    val lastMessageTimestamp: Long?,
    val unreadMessageCount: Int
)

/**
 * FOR TEAM: This object acts as a shared 'letterbox' for the list of active
 * conversations belonging to the currently logged-in user.
 * The module responsible for fetching conversation lists should update this.
 * The messaging UI (conversation list screen) will observe `conversationsFlow`.
 */
object UserActiveConversations {
    private val _conversationsDataSource = MutableStateFlow<List<ConversationData>>(emptyList())

    /**
     * MESSAGING MODULE USE: Observes this Flow to display the list of conversations
     * for the user identified by `CurrentLoggedInUser`.
     */
    val conversationsFlow: StateFlow<List<ConversationData>> =
        _conversationsDataSource.asStateFlow()

    private var currentDataOwnerId: String? = null // Tracks whose conversations these are

    /**
     * FOR TEAM: Call this method from module to provide the updated list
     * of conversation summaries for the specified user.
     * This typically happens when the user logs in, or when their conversation list changes
     * (e.g., new message in any chat, new chat started).
     *
     * @param forUserId The ID of the user these conversations belong to (should match `CurrentLoggedInUser`).
     * @param newConversationsList The complete, most recent list of `ConversationData` for that user.
     *
     * Example from module:
     *   val loggedInUserId = CurrentLoggedInUser.dataFlow.value?.id
     *   if (loggedInUserId != null) {
     *       val convSummaries = yourBackend.fetchConversationSummariesFor(loggedInUserId) // Returns List<ConversationData>
     *       UserActiveConversations.updateConversationsForUser(loggedInUserId, convSummaries)
     *   }
     */
    fun updateConversationsForUser(
        forUserId: String,
        newConversationsList: List<ConversationData>
    ) {
        _conversationsDataSource.value = newConversationsList
        currentDataOwnerId = forUserId
    }

    /**
     * FOR TEAM: Call this when the user logs out or when their conversation
     * context should be cleared.
     */
    fun clearAllConversations() {
        _conversationsDataSource.value = emptyList()
        currentDataOwnerId = null
    }

    /**
     * MESSAGING MODULE USE: (Optional) Can be used to check whose conversation data is currently loaded.
     */
    fun getOwnerUserId(): String? {
        return currentDataOwnerId
    }
}