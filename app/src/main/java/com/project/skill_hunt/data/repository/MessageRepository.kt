package com.project.skill_hunt.data.repository // Its own package

import com.project.skill_hunt.ApiService
import com.project.skill_hunt.data.model.ConversationSnippet
import com.project.skill_hunt.data.model.Message
import com.project.skill_hunt.data.model.SendMessageRequest
// import okhttp3.ResponseBody // If you use it for markConversationAsRead

class MessageRepository(private val api: ApiService) { // ApiService should resolve correctly

    suspend fun getConversationSnippets(): List<ConversationSnippet> {
        return api.getConversationSnippets()
    }

    suspend fun getMessagesForConversation(conversationId: String): List<Message> {
        return api.getMessagesForConversation(conversationId)
    }

    suspend fun sendMessage(receiverUserId: String, content: String): Message {
        val request = SendMessageRequest(receiverUserId = receiverUserId, content = content)
        return api.sendMessage(request)
    }

    // Optional:
    // suspend fun markConversationAsRead(conversationId: String): ResponseBody {
    //     return api.markConversationAsRead(conversationId) // Ensure ResponseBody is imported if used
    // }
}