package com.project.MessagingFromScratch.ui // Corrected package if it was .viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier // Added Modifier import if you use it in AppNavigation
import androidx.compose.runtime.remember // <--- ADD THIS LINE
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
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    conversationListViewModelFactory: ViewModelProvider.Factory,
    chatViewModelFactory: ViewModelProvider.Factory
) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.CONVERSATION_LIST_ROUTE,
        modifier = modifier
    ) {
        composable(AppDestinations.CONVERSATION_LIST_ROUTE) {
            // This 'it' (NavBackStackEntry) is available here
            val conversationListViewModel: ConversationListViewModel = viewModel(factory = conversationListViewModelFactory)
            ConversationListScreen(
                viewModel = conversationListViewModel,
                onNavigateToChat = { convId, otherId, otherName ->
                    navController.navigate(
                        AppDestinations.buildChatRoute(convId, otherId, otherName)
                    )
                },
                onNavigateToNewChat = { otherId, otherName ->
                    val tempNewConversationId = "NEW_${otherId}"
                    navController.navigate(
                        AppDestinations.buildChatRoute(tempNewConversationId, otherId, otherName)
                    )
                }
            )
        }
        composable(
            route = AppDestinations.CHAT_ROUTE_PATTERN,
            arguments = listOf(
                navArgument(AppDestinations.ARG_CONVERSATION_ID) { type = NavType.StringType },
                navArgument(AppDestinations.ARG_OTHER_USER_ID) { type = NavType.StringType },
                navArgument(AppDestinations.ARG_OTHER_USER_NAME) { type = NavType.StringType }
            )
        ) { backStackEntry -> // This is the NavBackStackEntry

            // --- Move ViewModel creation logic inside the @Composable scope ---
            // --- by calling it where ChatScreen is used, or ensure ---
            // --- the viewModel() call handles remember internally. ---

            // The standard androidx.lifecycle.viewmodel.compose.viewModel() function
            // IS composable and handles remembering the ViewModel correctly.
            // The issue might be with how the custom factory is constructed IF
            // you're trying to remember the factory itself.

            // Option 1: ViewModel directly using the passed chatViewModelFactory
            // The `viewModel()` composable function from `androidx.lifecycle.viewmodel.compose.viewModel`
            // is designed to be called directly within a @Composable scope.
            // If your chatViewModelFactory is correctly set up (e.g., as an
            // AbstractSavedStateViewModelFactory or a Hilt factory), this is usually enough.

            val chatViewModel: ChatViewModel = viewModel(
                viewModelStoreOwner = backStackEntry, // Scopes to this navigation destination
                factory = chatViewModelFactory       // Use the factory passed into AppNavigation
            )

            // The arguments are typically accessed by the ViewModel itself via SavedStateHandle.
            // The ChatViewModel's factory should be capable of injecting SavedStateHandle,
            // or the ChatViewModel should take SavedStateHandle as a constructor parameter.
            // If you still need to pass them explicitly to a method:
            val conversationId = backStackEntry.arguments?.getString(AppDestinations.ARG_CONVERSATION_ID) ?: "ERROR_NO_CONV_ID"
            val otherUserId = backStackEntry.arguments?.getString(AppDestinations.ARG_OTHER_USER_ID) ?: "ERROR_NO_USER_ID"
            val otherUserName = backStackEntry.arguments?.getString(AppDestinations.ARG_OTHER_USER_NAME) ?: "Unknown"

            // This call might be redundant if ChatViewModel uses SavedStateHandle to get args
            chatViewModel.loadConversation(conversationId, otherUserId, otherUserName)

            ChatScreen(
                viewModel = chatViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}