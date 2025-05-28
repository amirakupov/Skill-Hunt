package com.project.skill_hunt.ui.messaging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.skill_hunt.data.model.ConversationSnippet
import com.project.skill_hunt.domainlayer_usecases.messaging.GetConversationSnippetsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ConversationListUiState {
    object Loading : ConversationListUiState()
    data class Success(val conversations: List<ConversationSnippet>) : ConversationListUiState()
    data class Error(val message: String) : ConversationListUiState()
}

class ConversationListViewModel(
    private val getConversationSnippetsUseCase: GetConversationSnippetsUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<ConversationListUiState>(ConversationListUiState.Loading)
    val uiState: StateFlow<ConversationListUiState> = _uiState.asStateFlow()

    init {
        loadConversations()
    }

    fun loadConversations() {
        viewModelScope.launch {
            _uiState.value = ConversationListUiState.Loading
            val result = getConversationSnippetsUseCase()
            _uiState.value = result.fold(
                onSuccess = { snippets -> ConversationListUiState.Success(snippets) },
                onFailure = { throwable ->
                    ConversationListUiState.Error(
                        throwable.message ?: "Unknown error"
                    )
                }
            )
        }
    }
}