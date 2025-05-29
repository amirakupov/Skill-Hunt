package com.project.skill_hunt.ui.messaging

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.project.skill_hunt.domainlayer_usecases.messaging.GetMessagesForConversationUseCase
import com.project.skill_hunt.domainlayer_usecases.messaging.SendMessageUseCase

class ChatViewModelFactory(
    private val getMessagesForConversationUseCase: GetMessagesForConversationUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    owner: SavedStateRegistryOwner,
    private val currentUserId: String // Factory receives currentUserId
    // defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, /* defaultArgs */ null) {

    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel( // Ensure all parameters are passed to ChatViewModel
                getMessagesUseCase = getMessagesForConversationUseCase, // Corrected name
                sendMessageUseCase = sendMessageUseCase,
                savedStateHandle = handle,
                currentUserId = currentUserId // <<<< THIS IS THE CRITICAL LINE
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}