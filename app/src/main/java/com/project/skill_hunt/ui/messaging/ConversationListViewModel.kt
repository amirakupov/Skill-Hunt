package com.project.skill_hunt.ui.messaging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.skill_hunt.data.model.ConversationSnippet
import com.project.skill_hunt.domainlayer_usecases.messaging.GetConversationSnippetsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch // Add this import
import kotlinx.coroutines.launch

sealed class ConversationListUiState {
    object Loading : ConversationListUiState()
    data class Success(val conversations: List<ConversationSnippet>) : ConversationListUiState()
    data class Error(val message: String) : ConversationListUiState()
}

class ConversationListViewModel(
    private val getConversationSnippetsUseCase: GetConversationSnippetsUseCase,
    // You'll need the current user's ID to fetch their conversations
    private val currentUserId: String // Add this if you don't have another way to get it
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<ConversationListUiState>(ConversationListUiState.Loading)
    val uiState: StateFlow<ConversationListUiState> = _uiState.asStateFlow()

    init {
        loadConversations()
    }

    fun loadConversations() {
        viewModelScope.launch {
            _uiState.value = ConversationListUiState.Loading // Set to loading state

            // GetConversationSnippetsUseCase now takes userId and returns a Flow<Result<List<ConversationSnippet>>>
            getConversationSnippetsUseCase(currentUserId) // Pass the currentUserId
                .catch { e ->
                    // Handle exceptions from the Flow itself (e.g., network issues upstream)
                    _uiState.value = ConversationListUiState.Error(e.message ?: "An unexpected error occurred in flow")
                }
                .collect { result -> // Collect the emissions from the Flow
                    result.fold(
                        onSuccess = { snippets ->
                            _uiState.value = ConversationListUiState.Success(snippets)
                        },
                        onFailure = { throwable ->
                            _uiState.value =
                                ConversationListUiState.Error(throwable.message ?: "Unknown error loading conversations")
                        }
                    )
                }
        }
    }

}