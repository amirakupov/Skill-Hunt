package com.project.skill_hunt.domainlayer_usecases.messaging

import com.project.skill_hunt.data.model.ConversationSnippet
import com.project.skill_hunt.data.repository.MessageRepository
import com.project.skill_hunt.data.model.Message
import kotlinx.coroutines.flow.Flow

class GetConversationSnippetsUseCase(private val messageRepository: MessageRepository) {
    suspend operator fun invoke(): Result<List<ConversationSnippet>> {
        return try {
            val snippets = messageRepository.getConversationSnippets()
            Result.success(snippets)
        } catch (e: Exception) {
            // Log the exception e
            Result.failure(e)
        }
    }

    // Optional: If want to make this observable as a Flow for real-time updates (even with fakes)
    // fun observeConversations(): Flow<Result<List<ConversationSnippet>>> = flow {
    //     try {
    //         // In a real app with websockets or other real-time mechanisms,
    //         // messageRepository.observeConversationSnippets() would return a Flow.
    //         // For now, we emit once, but this structure allows for future enhancements.
    //         emit(Result.success(messageRepository.getConversationSnippets()))
    //     } catch (e: Exception) {
    //         emit(Result.failure(e))
    //     }
    // }
}