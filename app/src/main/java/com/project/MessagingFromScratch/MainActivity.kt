// MainActivity.kt
package com.project.MessagingFromScratch // Or your actual main package

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.project.MessagingFromScratch.repository.InMemoryMessageRepository // Corrected import
import com.project.MessagingFromScratch.repository.MessageRepository       // Corrected import
import com.project.MessagingFromScratch.repository.USER_ID_ME              // Corrected import
import com.project.MessagingFromScratch.repository.DemoRepositoryImpl // Import the implementation
import com.project.MessagingFromScratch.repository.DemoRepository     // Import the interface
import com.project.MessagingFromScratch.ui.AppNavigation
import com.project.MessagingFromScratch.ui.theme.MessagingFromScratchTheme
import com.project.MessagingFromScratch.ui.viewmodel.ChatViewModel
import com.project.MessagingFromScratch.ui.viewmodel.ConversationListViewModel

class MainActivity : ComponentActivity() {

    // 1. Create an instance of DemoRepositoryImpl
    private val demoRepository: DemoRepository by lazy {
        DemoRepositoryImpl()
    }

    // 2. Pass demoRepository to InMemoryMessageRepository
    private val messageRepository: MessageRepository by lazy {
        InMemoryMessageRepository(demoRepository) // <<< PASS IT HERE
    }

    private val conversationListViewModelFactory: ViewModelProvider.Factory by lazy {
        ConversationListViewModel.Factory(messageRepository, USER_ID_ME)
    }

    private val chatViewModelFactory: ViewModelProvider.Factory by lazy {
        ChatViewModel.Factory(messageRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MessagingFromScratchTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        conversationListViewModelFactory = conversationListViewModelFactory,
                        chatViewModelFactory = chatViewModelFactory
                    )
                }
            }
        }
    }
}