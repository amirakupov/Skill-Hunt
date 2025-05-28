package com.project.skill_hunt.ui // Or your actual package

// In AppNavHost.kt or a separate AppDestinations.kt file

object AppDestinations {
    const val REGISTER_ROUTE = "register"
    const val LOGIN_ROUTE = "login"
    const val HOME_ROUTE = "home"
    const val CREATE_COURSE_ROUTE = "create_course"
    const val BROWSE_COURSES_ROUTE = "browse_courses"

    // Messaging Routes
    const val CONVERSATION_LIST_ROUTE = "conversations"
    const val CHAT_ROUTE_BASE = "chat"

    // Argument names MUST be declared BEFORE they are used in CHAT_ROUTE_WITH_ARGS
    const val CHAT_ARG_CONVERSATION_ID = "conversationId" // Defined in ChatViewModel
    const val CHAT_ARG_OTHER_USER_ID = "otherUserId"   // Defined in ChatViewModel

    // Route for navigating to a specific chat
    const val CHAT_ROUTE_WITH_ARGS =
        "$CHAT_ROUTE_BASE?$CHAT_ARG_CONVERSATION_ID={$CHAT_ARG_CONVERSATION_ID}&$CHAT_ARG_OTHER_USER_ID={$CHAT_ARG_OTHER_USER_ID}"
    // Note: For optional arguments in Jetpack Navigation, the route definition often looks like:
    // "chat?conversationId={conversationId}&otherUserId={otherUserId}"
    // And when building arguments:
    // arguments = listOf(
    // navArgument("conversationId") { type = NavType.StringType; nullable = true },
    // navArgument("otherUserId") { type = NavType.StringType; nullable = true }
    // )
    // Your CHAT_ROUTE_WITH_ARGS string template is correct for how Jetpack Navigation composes these.


    // Helper to build the chat route
    fun chatRoute(conversationId: String? = null, otherUserId: String? = null): String {
        val route = CHAT_ROUTE_BASE
        val args = mutableListOf<String>()
        // When constructing the query parameters, we use the argument name directly.
        conversationId?.let { args.add("$CHAT_ARG_CONVERSATION_ID=$it") }
        otherUserId?.let { args.add("$CHAT_ARG_OTHER_USER_ID=$it") }
        return if (args.isNotEmpty()) "$route?${args.joinToString("&")}" else route
    }
}