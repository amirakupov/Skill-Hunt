package com.project.skill_hunt.data.network.fake

// Remove unused Compose imports if not needed here:
// import androidx.compose.ui.semantics.error // Likely unused
// import androidx.compose.ui.semantics.password // Likely unused

import androidx.compose.animation.core.copy
import androidx.compose.foundation.layout.add
import androidx.compose.ui.geometry.isEmpty
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.password
import com.project.skill_hunt.ApiService // The REAL interface from  main project structure

// Import  data models
import com.project.skill_hunt.data.model.CourseResponse
import com.project.skill_hunt.data.model.CourseCreateRequest // Assuming  need this for createCourse
import com.project.skill_hunt.data.model.RegisterRequest
import com.project.skill_hunt.data.model.RegisterResponse
import com.project.skill_hunt.data.model.LoginRequest
import com.project.skill_hunt.data.model.LoginResponseWithToken

import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import kotlin.collections.find
import kotlin.collections.getOrPut

class FakeApiService : ApiService { // Implements the original ApiService interface

    private var nextCourseId = 3 // To generate unique IDs for new fake courses

    private val dummyCourses = mutableListOf(
        CourseResponse(id = "1", title = "Kotlin for Beginners (Fake)", description = "Learn the basics of Kotlin programming.", location = "Online", mentorId = "mentorA"),
        CourseResponse(id = "2", title = "Advanced Jetpack Compose (Fake)", description = "Deep dive into Compose UI.", location = "Online", mentorId = "mentorB")
        // Add more fake courses if desired
    )

    // --- Auth Endpoints ---
    override suspend fun register(req: RegisterRequest): RegisterResponse {
        delay(300) // Simulate network delay
        // Simulate successful registration
        return RegisterResponse(id = "fake_user_${req.email.hashCode()}", email = req.email)
    }

    override suspend fun login(req: LoginRequest): LoginResponseWithToken {
        delay(300) // Simulate network delay
        // Simulate a successful login for a test user
        if (req.email == "test@example.com" && req.password == "password") {
            return LoginResponseWithToken(token = "fake-jwt-token-${System.currentTimeMillis()}", email = req.email)
        }
        // Simulate an error for incorrect login
        throw HttpException(
            Response.error<LoginResponseWithToken>(
                401, // Unauthorized
                "{\"error\":\"Invalid credentials\"}".toResponseBody("application/json".toMediaTypeOrNull())
            )
        )
    }

    override suspend fun getProtected(): ResponseBody {
        delay(100)
        return "Access granted to fake protected data. Welcome!".toResponseBody("text/plain".toMediaTypeOrNull())
    }

    // --- Course/Listing Endpoints ---
    override suspend fun createCourse(courseData: CourseCreateRequest): CourseResponse {
        delay(500) // Simulate network delay
        val newCourse = CourseResponse(
            id = "course_${nextCourseId++}",
            title = courseData.title,
            description = courseData.description,
            location = courseData.location,
            mentorId = "fake_mentor_id_for_new_course" // Or derive from a fake logged-in user
        )
        dummyCourses.add(newCourse)
        return newCourse
    }

    override suspend fun getAllCourses(): List<CourseResponse> {
        delay(800) // Simulate network delay
        return dummyCourses.toList() // Return a copy to prevent external modification
    }

    override suspend fun getCourseDetails(courseId: String): CourseResponse {
        delay(400) // Simulate network delay
        return dummyCourses.find { it.id == courseId }
            ?: throw HttpException(
                Response.error<CourseResponse>(
                    404, // Not Found
                    "{\"error\":\"Fake course with id '$courseId' not found.\"}".toResponseBody("application/json".toMediaTypeOrNull())
                )
            )
    }

        private val fakeConversations = mutableListOf<ConversationSnippet>()
        private val fakeMessages = mutableMapOf<String, MutableList<androidx.datastore.core.Message>>() // conversationId to List<Message>
        private var nextMessageId = 1
        private var nextConversationId = 1

        // Helper to get or create a conversation ID between two users
        private fun getOrCreateConversationId(userId1: String, userId2: String): String {
            // Simple consistent ID generation (order user IDs alphabetically)
            val sortedUsers = listOf(userId1, userId2).sorted()
            val potentialConvId = "conv_${sortedUsers[0]}_${sortedUsers[1]}"

            if (!fakeConversations.any { it.conversationId == potentialConvId }) {
                // Assume "currentUser" is the one initiating for now for snippet generation
                // In a real app, the backend would know the current user from the auth token
                val otherUserIdForSnippet = if (userId1 == "currentUser_fake_id") userId2 else userId1
                val newConv = ConversationSnippet(
                    conversationId = potentialConvId,
                    otherUserId = otherUserIdForSnippet,
                    otherUserName = "User $otherUserIdForSnippet", // Fake name
                    otherUserAvatarUrl = null,
                    lastMessage = "No messages yet.",
                    lastMessageTimestamp = System.currentTimeMillis(),
                    unreadCount = 0
                )
                fakeConversations.add(newConv)
                fakeMessages[potentialConvId] = mutableListOf()
            }
            return potentialConvId
        }


        // --- Fake Messaging Endpoint Implementations ---

        override suspend fun getConversationSnippets(): List<ConversationSnippet> {
            delay(400)
            // Simulate some initial conversations if empty
            if (fakeConversations.isEmpty()) {
                val conv1Id = getOrCreateConversationId("currentUser_fake_id", "userB_fake_id")
                val conv2Id = getOrCreateConversationId("currentUser_fake_id", "userC_fake_id")

                // Add some fake messages to populate snippets
                val msg1 = androidx.datastore.core.Message("msg_${nextMessageId++}", conv1Id, "userB_fake_id", "currentUser_fake_id", "Hey there! (Fake)", System.currentTimeMillis() - 100000, false)
                val msg2 = androidx.datastore.core.Message("msg_${nextMessageId++}", conv1Id, "currentUser_fake_id", "userB_fake_id", "Hi! How are you? (Fake)", System.currentTimeMillis() - 50000, true) // Current user sent, so read by them
                val msg3 = androidx.datastore.core.Message("msg_${nextMessageId++}", conv2Id, "userC_fake_id", "currentUser_fake_id", "Interested in  course. (Fake)", System.currentTimeMillis() - 20000, false)

                fakeMessages[conv1Id]?.addAll(listOf(msg1, msg2))
                fakeMessages[conv2Id]?.add(msg3)

                // Update snippets based on these messages
                fakeConversations.find { it.conversationId == conv1Id }?.let {
                    it.copy(lastMessage = msg2.content, lastMessageTimestamp = msg2.timestamp, unreadCount = if (!msg1.isRead) 1 else 0 ) // Simplified unread
                }?.also { updatedSnippet ->
                    fakeConversations.removeIf { it.conversationId == conv1Id }
                    fakeConversations.add(updatedSnippet)
                }
                fakeConversations.find { it.conversationId == conv2Id }?.let {
                    it.copy(lastMessage = msg3.content, lastMessageTimestamp = msg3.timestamp, unreadCount = 1 )
                }?.also { updatedSnippet ->
                    fakeConversations.removeIf { it.conversationId == conv2Id }
                    fakeConversations.add(updatedSnippet)
                }
            }
            // Sort by last message time, newest first
            return fakeConversations.sortedByDescending { it.lastMessageTimestamp }
        }

        override suspend fun getMessagesForConversation(conversationId: String): List<androidx.datastore.core.Message> {
            delay(300)
            // Mark messages as read for the current user when they open a conversation
            // Assuming "currentUser_fake_id" is the one fetching these messages
            fakeMessages[conversationId]?.forEach { message ->
                if (message.receiverId == "currentUser_fake_id") {
                    // In a real scenario, update the message object itself.
                    // For simplicity here, we're just returning them as is, but a real fake might update isRead.
                }
            }
            return fakeMessages[conversationId]?.toList() ?: emptyList()
        }

        override suspend fun sendMessage(request: SendMessageRequest): androidx.datastore.core.Message {
            delay(200)
            // Assume "currentUser_fake_id" is the sender for this fake implementation.
            // In a real app, the backend derives senderId from the auth token.
            val senderId = "currentUser_fake_id"
            val receiverId = request.receiverUserId

            // Determine the conversation ID. The helper ensures a consistent ID for any pair of users.
            val conversationId = getOrCreateConversationId(senderId, receiverId)

            val newMessage = androidx.datastore.core.Message(
                messageId = "msg_${nextMessageId++}",
                conversationId = conversationId,
                senderId = senderId,
                receiverId = receiverId,
                content = request.content,
                timestamp = System.currentTimeMillis(),
                isRead = false // New messages are initially unread by the recipient
            )

            // Add the message to our fake messages store
            val messagesInConversation = fakeMessages.getOrPut(conversationId) { mutableListOf() }
            messagesInConversation.add(newMessage)

            // Update the conversation snippet for this conversation
            val conversationSnippet = fakeConversations.find { it.conversationId == conversationId }
            if (conversationSnippet != null) {
                val updatedSnippet = conversationSnippet.copy(
                    lastMessage = newMessage.content,
                    lastMessageTimestamp = newMessage.timestamp,
                    // If the current fake user is the recipient, increment unread, otherwise it's 0 for sender's view.
                    // This is a simplification; real unread count is per user.
                    unreadCount = if (conversationSnippet.otherUserId == senderId) 0 else conversationSnippet.unreadCount + 1
                )
                fakeConversations.removeIf { it.conversationId == conversationId }
                fakeConversations.add(updatedSnippet)
            } else {
                // This case should ideally be handled by getOrCreateConversationId ensuring snippet exists
                // But as a fallback, create a new snippet if somehow missed
                val newSnippet = ConversationSnippet(
                    conversationId = conversationId,
                    otherUserId = receiverId, // The other user in this new conversation from sender's perspective
                    otherUserName = "User $receiverId", // Fake name
                    otherUserAvatarUrl = null,
                    lastMessage = newMessage.content,
                    lastMessageTimestamp = newMessage.timestamp,
                    unreadCount = 1 // 1 unread message for the recipient
                )
                fakeConversations.add(newSnippet)
            }

            return newMessage // Return the created message as the API contract suggests
        }
    }
