package com.project.MessagingFromScratch.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.project.MessagingFromScratch.ui.screens.ChatScreen
import com.project.MessagingFromScratch.ui.screens.ConversationListScreen
// ViewModel types are still needed for instantiation
import com.project.MessagingFromScratch.ui.viewmodel.ChatViewModel
import com.project.MessagingFromScratch.ui.viewmodel.ConversationListViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

// --- Navigation Route Definitions ---
private const val ROUTE_CONVERSATION_LIST = "conversationList"
// Using plain strings for argument placeholders directly in the route
private const val ROUTE_CHAT = "chat/{otherUserId}?conversationId={conversationId}&userName={userName}"

// Helper to build the chat route safely
private fun NavHostController.navigateToChat(
    otherUserId: String,
    userName: String,
    conversationId: String? = null
) {
    val encodedUserName = URLEncoder.encode(userName, StandardCharsets.UTF_8.name())
    val route = if (conversationId != null) {
        "chat/$otherUserId?conversationId=$conversationId&userName=$encodedUserName"
    } else {
        "chat/$otherUserId?userName=$encodedUserName" // conversationId is omitted if null
    }
    this.navigate(route)
}


@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    // Assuming these factories are correctly provided from your Application class or Hilt/DI setup
    conversationListViewModelFactory: ViewModelProvider.Factory,
    chatViewModelFactory: ViewModelProvider.Factory
) {
    NavHost(
        navController = navController,
        startDestination = ROUTE_CONVERSATION_LIST,
        modifier = modifier
    ) {
        composable(ROUTE_CONVERSATION_LIST) {
            val conversationListViewModel: ConversationListViewModel = viewModel(
                factory = conversationListViewModelFactory
            )
            ConversationListScreen(
                viewModel = conversationListViewModel,
                // These lambdas in ConversationListScreen must match these parameters:
                // onNavigateToChat: (convId: String, otherUserId: String, userName: String) -> Unit
                // onNavigateToNewChat: (otherUserId: String, userName: String) -> Unit
                onNavigateToChat = { convId, otherUserId, userName ->
                    navController.navigateToChat(
                        otherUserId = otherUserId,
                        userName = userName,
                        conversationId = convId
                    )
                },
                onNavigateToNewChat = { otherUserId, userName ->
                    navController.navigateToChat(
                        otherUserId = otherUserId,
                        userName = userName
                        // conversationId will be null
                    )
                }
            )
        }

        composable(
            route = ROUTE_CHAT,
            arguments = listOf(
                navArgument("otherUserId") { type = NavType.StringType },
                navArgument("userName") { type = NavType.StringType }, // Assuming userName is for the other user
                navArgument("conversationId") {
                    type = NavType.StringType
                    nullable = true // Conversation ID can be null if starting a new chat
                }
            )
        ) { backStackEntry ->
            // ChatViewModel would use SavedStateHandle to get "otherUserId", "conversationId", "userName"
            val chatViewModel: ChatViewModel = viewModel(
                viewModelStoreOwner = backStackEntry, // Scopes ViewModel to this destination
                factory = chatViewModelFactory
            )
            ChatScreen(
                viewModel = chatViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}