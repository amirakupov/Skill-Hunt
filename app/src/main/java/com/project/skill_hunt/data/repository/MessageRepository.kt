import com.project.skill_hunt.ApiService
import com.project.skill_hunt.data.model.ConversationSnippet
import com.project.skill_hunt.data.model.Message
import com.project.skill_hunt.data.model.SendMessageRequest

class MessageRepository(private val api: ApiService) {

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
    // suspend fun markConversationAsRead(conversationId: String) {
    //     api.markConversationAsRead(conversationId)
    // }
}