package com.project.MessagingFromScratch.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.project.DOMAINLAYER.toUIlayer.ConversationSnippet
import com.project.DOMAINLAYER.usecase15.UIrepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// --- ConversationListUiState ---
sealed interface ConversationListUiState {
    object Loading : ConversationListUiState
    data class Success(val snippets: List<ConversationSnippet>) : ConversationListUiState
    data class Error(val message: String) : ConversationListUiState
}

// --- ConversationListViewModel ---
class ConversationListViewModel(
    private val UIrepository: UIrepository, private val currentUserId: String // Passed in constructor
) : ViewModel() {

    private val _uiState = MutableStateFlow<ConversationListUiState>(ConversationListUiState.Loading)
    val uiState: StateFlow<ConversationListUiState> = _uiState.asStateFlow()

    init {
        loadConversations()
    }

    private fun loadConversations() {
        viewModelScope.launch {
            UIrepository.getConversationSnippets(currentUserId)
                .catch { e ->
                    _uiState.value = ConversationListUiState.Error("Failed to load conversations: ${e.message}")
                }
                .collect { snippets ->
                    _uiState.value = ConversationListUiState.Success(snippets)
                }
        }
    }

    fun getOrCreateConversationWith(otherUserId: String, onConversationReady: (conversationId: String) -> Unit) {
        viewModelScope.launch {
            UIrepository.getOrCreateConversationId(currentUserId, otherUserId)
                .catch { e ->
                    // Handle error, maybe update UI state to show an error message
                    _uiState.update { currentState ->
                        if (currentState is ConversationListUiState.Error) {
                            currentState.copy(message = "Failed to create/get conversation: ${e.message}")
                        } else {
                            ConversationListUiState.Error("Failed to create/get conversation: ${e.message}")
                        }
                    }
                }
                .collect { conversationId ->
                    onConversationReady(conversationId)
                }
        }
    }

    fun markConversationAsRead(conversationId: String, readerUserId: String) {
        viewModelScope.launch {
            // This is an optimistic update on the ViewModel side.
            // The repository's markMessagesAsRead will handle the actual data change,
            // which should then flow back and update the snippet list.
            // No direct UI state manipulation here might be needed if the flow from repo is quick.
            UIrepository.markMessagesAsRead(conversationId, readerUserId)
            // Optionally, if need a more immediate reflection before the flow updates,
            // could manually update the specific snippet here, but it can get complex.
        }
    }


    companion object {
        fun Factory(repository: UIrepository, userId: String): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(ConversationListViewModel::class.java)) {
                        return ConversationListViewModel(repository, userId) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            }
    }
}