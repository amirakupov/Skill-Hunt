package com.project.skill_hunt.domainlayer_usecases.messaging

// Remove: import com.project.skill_hunt.data.model.ConversationSnippet // Not used here
import com.project.skill_hunt.data.repository.MessageRepository
import com.project.skill_hunt.data.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.catch

class GetMessagesForConversationUseCase(private val messageRepository: MessageRepository) {

    // The UseCase will now also return a Flow of Result
    // It needs both conversationId and the current userId
    operator fun invoke(conversationId: String, userId: String): Flow<Result<List<Message>>> {
        if (conversationId.isBlank()) {
            // It's better to return a Flow that immediately emits a failure
            // rather than throwing an exception directly in a Flow-returning function.
            return kotlinx.coroutines.flow.flow {
                emit(Result.failure(IllegalArgumentException("Conversation ID cannot be blank.")))
            }
        }
        // Add a check for userId as well, if it's critical
        if (userId.isBlank()) {
            return kotlinx.coroutines.flow.flow {
                emit(Result.failure(IllegalArgumentException("User ID cannot be blank.")))
            }
        }

        return messageRepository.getMessagesForConversation(conversationId, userId) // Pass both IDs
            .map { messages ->
                Result.success(messages) // Wrap the successful emission in Result
            }
            .catch { e ->
                // If the flow throws an exception (e.g., network error in a real repo)
                // Log the exception e
                emit(Result.failure(e)) // Emit a failure Result
            }
    }

    // The commented-out observeMessages function was already Flow-based,
    // so its logic is similar to what we're implementing in invoke now.
    // If you prefer to keep a separate suspend fun for a one-shot fetch,
    // you would need to collect the flow, e.g., using .first()
    //
    // Example of a one-shot version (if you needed it, but Flow is preferred for UI):
    // suspend fun getSingleListOfMessages(conversationId: String, userId: String): Result<List<Message>> {
    //     if (conversationId.isBlank()) {
    //         return Result.failure(IllegalArgumentException("Conversation ID cannot be blank."))
    //     }
    //     if (userId.isBlank()) {
    //         return Result.failure(IllegalArgumentException("User ID cannot be blank."))
    //     }
    //     return try {
    //         // This collects only the first emission and then cancels the flow.
    //         val messages = messageRepository.getMessagesForConversation(conversationId, userId).first()
    //         Result.success(messages)
    //     } catch (e: Exception) {
    //         // Log the exception e
    //         Result.failure(e)
    //     }
    // }
}