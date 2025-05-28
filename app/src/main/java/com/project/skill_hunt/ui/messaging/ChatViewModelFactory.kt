package com.project.skill_hunt.ui.messaging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.project.skill_hunt.domainlayer_usecases.messaging.GetMessagesForConversationUseCase
import com.project.skill_hunt.domainlayer_usecases.messaging.SendMessageUseCase
// Removed unused imports for ConversationListViewModel and Message as they are not used in this factory

class ChatViewModelFactory(
    private val getMessagesForConversationUseCase: GetMessagesForConversationUseCase,
    private val sendMessageUseCase: SendMessageUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T { // Use CreationExtras
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            // Get SavedStateHandle from CreationExtras
            val savedStateHandle = extras.createSavedStateHandle()
            return ChatViewModel(
                getMessagesForConversationUseCase,
                sendMessageUseCase,
                savedStateHandle // Pass the SavedStateHandle
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}