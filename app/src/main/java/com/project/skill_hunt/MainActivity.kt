package com.project.skill_hunt // Your main package

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.project.skill_hunt.data.repository.FakeMessageRepositoryImpl // Ensure correct import
import com.project.skill_hunt.domainlayer_usecases.messaging.GetConversationSnippetsUseCase // Ensure correct import
import com.project.skill_hunt.domainlayer_usecases.messaging.GetMessagesForConversationUseCase // Ensure correct import
import com.project.skill_hunt.domainlayer_usecases.messaging.SendMessageUseCase // Ensure correct import
import com.project.skill_hunt.ui.AppNavHost // Your NavHost composable
import com.project.skill_hunt.ui.messaging.ChatViewModelFactory // Ensure correct import
import com.project.skill_hunt.ui.messaging.ConversationListViewModelFactory // Ensure correct import
import com.project.skill_hunt.ui.theme.SkillHuntTheme // Your theme

class MainActivity : ComponentActivity() {

    // Instantiate dependencies here or use a DI framework like Hilt later
    private val messageRepository by lazy { FakeMessageRepositoryImpl() }

    // TODO: Replace "currentUserPlaceholderId" with your actual way of getting the current user's ID
    // This could be from SharedPreferences, a login manager, Firebase Auth, etc.
    // For now, it's a placeholder. In a real app, this MUST be the actual logged-in user's ID.
    private val currentLoggedInUserId: String by lazy {
        // Example: If you were using FakeMessageRepositoryImpl's internal ID
        // messageRepository.getCurrentUserId()
        // Or retrieve from another source:
        "user1" // Placeholder - Ensure this matches a senderId in your FakeMessageRepositoryImpl
    }

    private val getConversationSnippetsUseCase by lazy {
        GetConversationSnippetsUseCase(messageRepository)
    }
    private val getMessagesForConversationUseCase by lazy {
        GetMessagesForConversationUseCase(messageRepository)
    }
    private val sendMessageUseCase by lazy {
        SendMessageUseCase(messageRepository)
    }

    // Create ViewModel Factories
    // For ChatViewModelFactory, 'this' (the Activity) is the SavedStateRegistryOwner
    private val chatViewModelFactory by lazy {
        ChatViewModelFactory(
            getMessagesForConversationUseCase = getMessagesForConversationUseCase,
            sendMessageUseCase = sendMessageUseCase,
            owner = this, // Activity is the SavedStateRegistryOwner
            // ChatViewModel will also likely need the currentLoggedInUserId
            // and potentially the conversationId (often from SavedStateHandle)
            // We'll address this when we get to ChatViewModelFactory/ChatViewModel
            currentUserId = currentLoggedInUserId // Anticipating ChatViewModel needs this
        )
    }

    private val conversationListViewModelFactory by lazy {
        ConversationListViewModelFactory(
            getConversationSnippetsUseCase = getConversationSnippetsUseCase,
            currentUserId = currentLoggedInUserId // Provide the current user ID here
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkillHuntTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost(
                        conversationListViewModelFactory = conversationListViewModelFactory,
                        chatViewModelFactory = chatViewModelFactory
                    )
                }
            }
        }
    }
}