package com.project.skill_hunt.ui // Or your actual package

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel // ViewModel composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.project.skill_hunt.ui.messaging.ChatScreen
import com.project.skill_hunt.ui.messaging.ChatViewModel // ViewModel class
import com.project.skill_hunt.ui.messaging.ChatViewModelFactory
import com.project.skill_hunt.ui.messaging.ConversationListScreen
import com.project.skill_hunt.ui.messaging.ConversationListViewModel // ViewModel class
import com.project.skill_hunt.ui.messaging.ConversationListViewModelFactory

object Screen {
    const val ConversationList = "conversationList"
    // Ensure chatRoute matches the pattern and arguments
    fun chatRoute(conversationId: String, userName: String) = "chat/$conversationId/$userName"
    const val ChatScreenPattern = "chat/{conversationId}/{userName}" // Must match navArgument names
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    conversationListViewModelFactory: ConversationListViewModelFactory,
    chatViewModelFactory: ChatViewModelFactory // Pass the factory
) {
    NavHost(
        navController = navController,
        startDestination = Screen.ConversationList,
        modifier = modifier
    ) {
        composable(Screen.ConversationList) {
            val conversationListViewModel: ConversationListViewModel = viewModel(
                factory = conversationListViewModelFactory
            )
            ConversationListScreen(
                viewModel = conversationListViewModel,
                onNavigateToChat = { conversationId, userName ->
                    navController.navigate(Screen.chatRoute(conversationId, userName))
                }
            )
        }

        composable(
            route = Screen.ChatScreenPattern,
            arguments = listOf(
                navArgument("conversationId") {
                    type = NavType.StringType
                    nullable = false // Expecting non-nullable from route
                },
                navArgument("userName") {
                    type = NavType.StringType
                    nullable = false // Expecting non-nullable from route
                }
            )
        ) { backStackEntry ->
            // Extract arguments. They are guaranteed non-nullable here due to nullable = false
            val conversationId = backStackEntry.arguments?.getString("conversationId")!!
            val userName = backStackEntry.arguments?.getString("userName")!!

            // Instantiate ChatViewModel using its factory
            val chatViewModel: ChatViewModel = viewModel(
                key = "chat_args_${conversationId}_${userName}", // Key ensures correct ViewModel instance
                factory = chatViewModelFactory
            )
            // SavedStateHandle in ChatViewModel will automatically get "conversationId" and "userName"
            // if they are part of the route and defined in navArguments.

            ChatScreen(
                viewModel = chatViewModel,
                conversationId = conversationId, // Pass the non-nullable String
                userName = userName,             // Pass the non-nullable String
                onNavUp = { navController.navigateUp() }
            )
        }
    }
}