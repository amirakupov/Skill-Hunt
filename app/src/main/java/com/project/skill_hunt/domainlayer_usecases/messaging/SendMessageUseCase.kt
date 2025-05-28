package com.project.skill_hunt.domainlayer_usecases.messaging

import com.project.skill_hunt.data.model.ConversationSnippet
import com.project.skill_hunt.data.repository.MessageRepository
import com.project.skill_hunt.data.model.Message

class SendMessageUseCase(private val messageRepository: MessageRepository) {
    suspend operator fun invoke(receiverUserId: String, content: String): Result<Message> {
        if (receiverUserId.isBlank()) {
            return Result.failure(IllegalArgumentException("Receiver User ID cannot be blank."))
        }
        if (content.isBlank()) {
            return Result.failure(IllegalArgumentException("Message content cannot be blank."))
        }
        return try {
            val sentMessage = messageRepository.sendMessage(receiverUserId, content)
            Result.success(sentMessage)
        } catch (e: Exception) {
            // Log the exception e
            Result.failure(e)
        }
    }
}