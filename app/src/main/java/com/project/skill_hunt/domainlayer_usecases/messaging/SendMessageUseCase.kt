package com.project.skill_hunt.domainlayer_usecases.messaging

// Remove: import com.project.skill_hunt.data.model.ConversationSnippet // Not used
import com.project.skill_hunt.data.repository.MessageRepository
import com.project.skill_hunt.data.model.Message
import com.project.skill_hunt.data.model.SendMessageRequest // Import SendMessageRequest

class SendMessageUseCase(private val messageRepository: MessageRepository) {

    // The UseCase should now accept SendMessageRequest as its primary parameter,
    // or construct it internally if you prefer to keep the existing signature.
    // For closer alignment with the repository, let's change the signature.
    // If you need to call it with individual params, the ViewModel can create the request object.

    suspend operator fun invoke(request: SendMessageRequest): Result<Message> {
        // Validation can be done here on the request object's properties
        if (request.receiverUserId.isBlank()) {
            return Result.failure(IllegalArgumentException("Receiver User ID cannot be blank."))
        }
        if (request.content.isBlank()) {
            return Result.failure(IllegalArgumentException("Message content cannot be blank."))
        }
        // If you had a currentUserId to validate against request.senderId (if it's part of SendMessageRequest)
        // you could do that here too.

        return try {
            // Directly call the repository and return its Result
            messageRepository.sendMessage(request)
        } catch (e: Exception) {
            // Log the exception e
            // This catch block might be redundant if messageRepository.sendMessage
            // already returns Result.failure for its own exceptions.
            // However, it can catch other unexpected exceptions during the call itself.
            Result.failure(e)
        }
    }

    // If you absolutely MUST keep the old signature (receiverUserId: String, content: String)
    // for some reason (e.g., existing ViewModel calls), you would do this:
    /*
    suspend operator fun invoke(
        senderUserId: String, // Assuming you can get this
        receiverUserId: String,
        content: String,
        conversationId: String? = null // Optional: if starting a new chat or continuing one
    ): Result<Message> {
        if (receiverUserId.isBlank()) {
            return Result.failure(IllegalArgumentException("Receiver User ID cannot be blank."))
        }
        if (content.isBlank()) {
            return Result.failure(IllegalArgumentException("Message content cannot be blank."))
        }
        if (senderUserId.isBlank()) { // Example: Validate senderId
             return Result.failure(IllegalArgumentException("Sender User ID cannot be blank."))
        }

        val request = SendMessageRequest(
            senderId = senderUserId,
            receiverUserId = receiverUserId,
            content = content,
            conversationId = conversationId
        )

        return try {
            messageRepository.sendMessage(request)
        } catch (e: Exception) {
            // Log the exception e
            Result.failure(e)
        }
    }
    */
}