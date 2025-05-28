package com.project.skill_hunt.domainlayer_usecases.messaging

import com.project.skill_hunt.data.model.ConversationSnippet
import com.project.skill_hunt.data.repository.MessageRepository
import com.project.skill_hunt.data.model.Message


class GetMessagesForConversationUseCase(private val messageRepository: MessageRepository) {
    suspend operator fun invoke(conversationId: String): Result<List<Message>> {
        if (conversationId.isBlank()) {
            return Result.failure(IllegalArgumentException("Conversation ID cannot be blank."))
        }
        return try {
            val messages = messageRepository.getMessagesForConversation(conversationId)
            Result.success(messages)
        } catch (e: Exception) {
            // Log the exception e
            Result.failure(e)
        }
    }

    // fun observeMessages(conversationId: String): Flow<Result<List<Message>>> = flow {
    //     if (conversationId.isBlank()) {
    //         emit(Result.failure(IllegalArgumentException("Conversation ID cannot be blank.")))
    //         return@flow
    //     }
    //     try {
    //         // Similar to conversations, a real repo might provide a Flow here.
    //         emit(Result.success(messageRepository.getMessagesForConversation(conversationId)))
    //     } catch (e: Exception) {
    //         emit(Result.failure(e))
    //     }
    // }
}