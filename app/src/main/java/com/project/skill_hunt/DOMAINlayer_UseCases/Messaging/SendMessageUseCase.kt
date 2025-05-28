package com.project.skill_hunt.DOMAINlayer_UseCases.Messaging

import com.project.skill_hunt.data.model.Message
import com.project.skill_hunt.data.repository.MessageRepository

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