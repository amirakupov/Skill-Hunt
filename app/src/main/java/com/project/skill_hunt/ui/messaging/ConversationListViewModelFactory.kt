package com.project.skill_hunt.ui.messaging // Or your actual package

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.skill_hunt.domainlayer_usecases.messaging.GetConversationSnippetsUseCase
// You might get currentUserId from a data source/repository, or pass it from where the factory is created.
// For simplicity, let's assume it's passed to the factory.

class ConversationListViewModelFactory(
    private val getConversationSnippetsUseCase: GetConversationSnippetsUseCase,
    private val currentUserId: String // Add currentUserId here
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConversationListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // Pass currentUserId to the ViewModel constructor
            return ConversationListViewModel(getConversationSnippetsUseCase, currentUserId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}