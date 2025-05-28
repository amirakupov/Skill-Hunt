package com.project.skill_hunt.ui.messaging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.skill_hunt.domainlayer_usecases.messaging.GetConversationSnippetsUseCase
import com.project.skill_hunt.ui.messaging.ConversationListViewModel

class ConversationListViewModelFactory(
    private val getConversationSnippetsUseCase: GetConversationSnippetsUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConversationListViewModel::class.java)) {
            return ConversationListViewModel(getConversationSnippetsUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}