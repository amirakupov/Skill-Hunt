package com.project.MessagingFromScratch.ui // Corrected package if it was .viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier // Added Modifier import if you use it in AppNavigation
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.project.MessagingFromScratch.repository.USER_NAMES // Import user names, if still needed here
import com.project.MessagingFromScratch.ui.screens.ChatScreen
import com.project.MessagingFromScratch.ui.screens.ConversationListScreen
import com.project.MessagingFromScratch.ui.viewmodel.ChatViewModel
import com.project.MessagingFromScratch.ui.viewmodel.ConversationListViewModel
// Removed unused URLEncoder and StandardCharsets if navToChat helper is removed

object AppDestinations {
    const val CONVERSATION_LIST_ROUTE = "conversationList"
    const val CHAT_ROUTE_PREFIX = "chat"

    // Route for ChatScreen: chat/{conversationId}/{otherUserId}/{otherUserName}
    const val CHAT_ROUTE_PATTERN = "$CHAT_ROUTE_PREFIX/{conversationId}/{otherUserId}/{otherUserName}"

    // Argument names (must match placeholders in CHAT_ROUTE_PATTERN and navArgument names)
    const val ARG_CONVERSATION_ID = "conversationId"
    const val ARG_OTHER_USER_ID = "otherUserId"
    const val ARG_OTHER_USER_NAME = "otherUserName"


    fun buildChatRoute(conversationId: String, otherUserId: String, otherUserName: String): String {
        // Simple string concatenation, assuming no special characters in these IDs/names that need encoding for path segments.
        // If names can have '/' or '?', URL encoding would be needed for the otherUserName path segment.
        // For simplicity, direct concatenation is used as per your AppDestinations.chatRoute example.
        return "$CHAT_ROUTE_PREFIX/$conversationId/$otherUserId/$otherUserName"
    }
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier, // Added modifier parameter
    navController: NavHostController = rememberNavController(),
    conversationListViewModelFactory: ViewModelProvider.Factory,
    chatViewModelFactory: ViewModelProvider.Factory
) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.CONVERSATION_LIST_ROUTE,
        modifier = modifier // Apply the modifier
    ) {
        composable(AppDestinations.CONVERSATION_LIST_ROUTE) {
            val conversationListViewModel: ConversationListViewModel = viewModel(factory = conversationListViewModelFactory)
            ConversationListScreen(
                viewModel = conversationListViewModel,
                // This lambda now matches the expected (String, String, String) -> Unit
                onNavigateToChat = { convId, otherId, otherName ->
                    navController.navigate(
                        AppDestinations.buildChatRoute(
                            conversationId = convId,
                            otherUserId = otherId,
                            otherUserName = otherName
                        )
                    )
                },
                // This lambda now matches the expected (String, String) -> Unit
                // It will call the ViewModel to prepare/get a conversationId first
                onNavigateToNewChat = { otherId, otherName ->
                    // This is a placeholder action.
                    // In a real app, you'd likely:
                    // 1. Call a method on conversationListViewModel to get or create a conversationId.
                    // 2. The ViewModel callback would then provide the conversationId to navigate.
                    // For now, to make it compile, we'll navigate with a "temporary" or "new" ID
                    // and expect ChatViewModel to handle it. THIS NEEDS PROPER IMPLEMENTATION.
                    val tempNewConversationId = "NEW_${otherId}" // Placeholder
                    navController.navigate(
                        AppDestinations.buildChatRoute(
                            conversationId = tempNewConversationId, // Or a real ID if ViewModel provides it
                            otherUserId = otherId,
                            otherUserName = otherName
                        )
                    )
                }
            )
        }
        composable(
            route = AppDestinations.CHAT_ROUTE_PATTERN, // Use the pattern from AppDestinations
            arguments = listOf(
                navArgument(AppDestinations.ARG_CONVERSATION_ID) { type = NavType.StringType },
                navArgument(AppDestinations.ARG_OTHER_USER_ID) { type = NavType.StringType },
                navArgument(AppDestinations.ARG_OTHER_USER_NAME) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // Arguments are non-null in the path, but good practice to handle potential issues if route constructed manually
            val conversationId = backStackEntry.arguments?.getString(AppDestinations.ARG_CONVERSATION_ID) ?: "ERROR_NO_CONV_ID"
            val otherUserId = backStackEntry.arguments?.getString(AppDestinations.ARG_OTHER_USER_ID) ?: "ERROR_NO_USER_ID"
            val otherUserName = backStackEntry.arguments?.getString(AppDestinations.ARG_OTHER_USER_NAME) ?: "Unknown"

            // ViewModelProvider.Factory for ChatViewModel's SavedStateHandle arguments
            val factory = remember(backStackEntry) {
                object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        // This assumes your ChatViewModel takes SavedStateHandle and then uses the factory provided
                        // If ChatViewModel is complex, use the chatViewModelFactory and ensure it can create
                        // ChatViewModel with SavedStateHandle correctly (e.g., HiltViewModel).
                        // For a simple SavedStateHandle access, this direct way is okay.
                        // However, to use your provided chatViewModelFactory, we'd need ChatViewModel to
                        // be @AssistedInject or similar for SavedStateHandle if factory doesn't handle it.

                        // Simplest approach if chatViewModelFactory IS a SavedStateViewModelFactory:
                        // return ViewModelProvider(backStackEntry, chatViewModelFactory).get(ChatViewModel::class.java) as T

                        // Given you pass chatViewModelFactory, it SHOULD be able to create ChatViewModel
                        // and inject SavedStateHandle. The standard way is using AbstractSavedStateViewModelFactory
                        // or Hilt. For this example, I'll use the passed factory, assuming it's set up for this.
                        return chatViewModelFactory.create(ChatViewModel::class.java) as T
                        // ChatViewModel then accesses args via its own SavedStateHandle
                    }
                }
            }
            // If chatViewModelFactory is an AbstractSavedStateViewModelFactory or from Hilt, this is simpler:
            // val chatViewModel: ChatViewModel = viewModel(viewModelStoreOwner = backStackEntry, factory = chatViewModelFactory)

            // Let's assume chatViewModelFactory can provide ChatViewModel and it uses SavedStateHandle internally
            val chatViewModel: ChatViewModel = viewModel(viewModelStoreOwner = backStackEntry, factory = chatViewModelFactory)


            // Load conversation using the arguments.
            // This assumes ChatViewModel has a method to take these or uses SavedStateHandle directly.
            // If ChatViewModel constructor uses SavedStateHandle, this explicit call might not be needed
            // if it observes the SavedStateHandle values.
            // For now, let's keep it if your ChatViewModel expects this.
            chatViewModel.loadConversation(conversationId, otherUserId, otherUserName)


            ChatScreen(
                viewModel = chatViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}