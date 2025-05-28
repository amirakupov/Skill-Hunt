package com.project.skill_hunt.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.project.skill_hunt.ui.course.CreateCourseScreen
import com.project.skill_hunt.ui.course.CreateCourseViewModelFactory
import com.project.skill_hunt.ui.login.AuthViewModel
import com.project.skill_hunt.ui.login.AuthViewModelFactory
import com.project.skill_hunt.ui.messaging.ConversationListViewModelFactory
import com.project.skill_hunt.ui.messaging.ChatViewModelFactory
import com.project.skill_hunt.ui.AppDestinations // Make sure this import points to your AppDestinations.kt file
import com.project.skill_hunt.ui.login.RegisterScreen // Example import
import com.project.skill_hunt.ui.login.LoginScreen    // Example import
import com.project.skill_hunt.ui.home.ProtectedHomeScreen // Or ui.home.HomeScreen
import com.project.skill_hunt.ui.messaging.ConversationListScreen // Example import
import com.project.skill_hunt.ui.messaging.ChatScreen          // Example import

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    authViewModelFactory: AuthViewModelFactory,
    createCourseViewModelFactory: CreateCourseViewModelFactory,
    conversationListViewModelFactory: ConversationListViewModelFactory,
    chatViewModelFactory: ChatViewModelFactory
) {
    val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)

    val startDestination = if (authViewModel.authToken != null) {
        AppDestinations.HOME_ROUTE // This will now correctly refer to the imported AppDestinations
    } else {
        AppDestinations.LOGIN_ROUTE
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(AppDestinations.REGISTER_ROUTE) { /* ... */ }
        composable(AppDestinations.LOGIN_ROUTE) { /* ... */ }
        composable(AppDestinations.HOME_ROUTE) { /* ... */ }
        composable(AppDestinations.CREATE_COURSE_ROUTE) { /* ... */ }
        // ... other composables using AppDestinations ...

        // for Messaging
        composable(AppDestinations.CONVERSATION_LIST_ROUTE) {
            ConversationListScreen( /* ... */ )
        }
        composable(
            route = AppDestinations.CHAT_ROUTE_WITH_ARGS,
            arguments = listOf(
                navArgument(AppDestinations.CHAT_ARG_CONVERSATION_ID) { /* ... */ },
                navArgument(AppDestinations.CHAT_ARG_OTHER_USER_ID) { /* ... */ }
            )
        ) {
            ChatScreen( /* ... */ )
        }
    }
}

// Dummy composables for ConversationListScreen and ChatScreen if they don't exist yet, to make AppNavHost compile
@Composable
fun ConversationListScreen(
    viewModelFactory: ConversationListViewModelFactory,
    onNavigateToChat: (conversationId: String?, otherUserId: String?) -> Unit
) { /* TODO */
}

@Composable
fun ChatScreen(
    viewModelFactory: ChatViewModelFactory,
    navUp: () -> Unit
) { /* TODO */
}

// Dummy composables for other screens if needed for compilation
@Composable
fun RegisterScreen(vm: AuthViewModel, navToLogin: () -> Unit) { /* TODO */
}

@Composable
fun LoginScreen(vm: AuthViewModel, navToHome: () -> Unit, navToRegister: () -> Unit) { /* TODO */
}

@Composable
fun ProtectedHomeScreen(vm: AuthViewModel, navToLogin: () -> Unit) { /* TODO */
}