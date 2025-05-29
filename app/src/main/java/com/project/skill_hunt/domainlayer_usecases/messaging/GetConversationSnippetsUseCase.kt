package com.project.skill_hunt.domainlayer_usecases.messaging

import com.project.skill_hunt.data.model.ConversationSnippet
import com.project.skill_hunt.data.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.catch // Import for catching exceptions in Flow

class GetConversationSnippetsUseCase(private val messageRepository: MessageRepository) {

    // The UseCase will now also return a Flow of Result
    operator fun invoke(userId: String): Flow<Result<List<ConversationSnippet>>> {
        return messageRepository.getConversationSnippets(userId) // Pass the userId
            .map { snippets ->
                Result.success(snippets) // Wrap the successful emission in Result
            }
            .catch { e ->
                // If the flow throws an exception (e.g., network error in a real repo)
                // Log the exception e
                emit(Result.failure(e)) // Emit a failure Result
            }
    }
}