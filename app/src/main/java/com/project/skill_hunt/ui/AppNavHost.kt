package com.project.skill_hunt.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.skill_hunt.ui.createcourse.CreateCourseScreen
import com.project.skill_hunt.ui.createcourse.CreateCourseViewModelFactory
import com.project.skill_hunt.ui.login.AuthViewModel
import com.project.skill_hunt.ui.login.AuthViewModelFactory
import com.project.skill_hunt.ui.login.LoginScreen
import com.project.skill_hunt.ui.login.ProtectedHomeScreen
import com.project.skill_hunt.ui.login.RegisterScreen

// Define routes in a shared object for consistency and type safety
object AppDestinations {
    const val REGISTER_ROUTE = "register"
    const val LOGIN_ROUTE = "login"
    const val HOME_ROUTE = "home" // This is your ProtectedHomeScreen
    const val CREATE_COURSE_ROUTE = "create_course"
    const val BROWSE_COURSES_ROUTE = "browse_courses" // For the next feature
    // Add other top-level destinations here
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(), // Allow providing controller for testing/previews
    authViewModelFactory: AuthViewModelFactory,
    createCourseViewModelFactory: CreateCourseViewModelFactory
    // listingsViewModelFactory: ListingsViewModelFactory // Add this when ready for browse listings
) {
    // AuthViewModel is likely needed to determine the start destination
    // and might be used by multiple screens (Login, Register, Home).
    val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)

    // Determine start destination based on whether a token exists
    // This logic runs once when AppNavHost is composed.
    val startDestination = if (authViewModel.authToken != null) {
        AppDestinations.HOME_ROUTE // Or BROWSE_COURSES_ROUTE if that's the primary logged-in screen
    } else {
        AppDestinations.LOGIN_ROUTE
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(AppDestinations.REGISTER_ROUTE) {
            RegisterScreen(
                vm = authViewModel, // Pass AuthViewModel
                navToLogin = {
                    navController.navigate(AppDestinations.LOGIN_ROUTE) {
                        popUpTo(AppDestinations.REGISTER_ROUTE) { inclusive = true }
                    }
                }
            )
        }

        composable(AppDestinations.LOGIN_ROUTE) {
            LoginScreen(
                vm = authViewModel, // Pass AuthViewModel
                navToHome = {
                    navController.navigate(AppDestinations.HOME_ROUTE) { // Or BROWSE_COURSES_ROUTE
                        popUpTo(AppDestinations.LOGIN_ROUTE) { inclusive = true }
                    }
                },
                navToRegister = {
                    navController.navigate(AppDestinations.REGISTER_ROUTE)
                }
            )
        }

        composable(AppDestinations.HOME_ROUTE) {
            // ProtectedHomeScreen might just show a greeting and a logout button.
            // It could also be a dashboard with navigation to other features.
            ProtectedHomeScreen(
                vm = authViewModel, // Pass AuthViewModel
                navToLogin = {
                    navController.navigate(AppDestinations.LOGIN_ROUTE) {
                        popUpTo(AppDestinations.HOME_ROUTE) { inclusive = true }
                    }
                }
            )
            // Example: Add a button in ProtectedHomeScreen to navigate to Create Course
            // Button(onClick = { navController.navigate(AppDestinations.CREATE_COURSE_ROUTE) }) {
            //     Text("Create a New Course")
            // }
        }

        composable(AppDestinations.CREATE_COURSE_ROUTE) {
            // Ensure you have an instance of CreateCourseViewModelFactory from MainActivity
            CreateCourseScreen(
                viewModelFactory = createCourseViewModelFactory,
                onCourseCreatedSuccessfully = { courseId ->
                    // Decide where to navigate after successful course creation
                    // Option 1: Go to a "My Courses" screen (if you have one)
                    // Option 2: Go to the "Browse Listings" screen to see it
                    // Option 3: Go back to the "Home" screen
                    // For now, let's navigate to the home screen or a browse screen if it exists
                    // Or, you could navigate to the detail page of the newly created course:
                    // navController.navigate("${AppDestinations.COURSE_DETAIL_ROUTE}/$courseId") {
                    //    popUpTo(AppDestinations.CREATE_COURSE_ROUTE) { inclusive = true } // Or popUpTo home
                    // }
                    navController.navigate(AppDestinations.HOME_ROUTE) { // Placeholder: Navigate home
                        popUpTo(AppDestinations.CREATE_COURSE_ROUTE) { inclusive = true }
                        // Alternatively, if you want to clear back stack up to a certain point:
                        // popUpTo(AppDestinations.HOME_ROUTE) { inclusive = false }
                    }
                    // Consider showing a Snackbar or Toast message here too.
                }
            )
        }

        // composable(AppDestinations.BROWSE_COURSES_ROUTE) {
        //     BrowseCoursesScreen(
        //         viewModelFactory = listingsViewModelFactory, // You'll create this
        //         onCourseSelected = { courseId ->
        //             navController.navigate("${AppDestinations.COURSE_DETAIL_ROUTE}/$courseId")
        //         },
        //         navToCreateCourse = {
        //              navController.navigate(AppDestinations.CREATE_COURSE_ROUTE)
        //         }
        //     )
        // }

        // Example for a course detail screen (you'll need a new ViewModel and UseCase for this)
        // composable("${AppDestinations.COURSE_DETAIL_ROUTE}/{courseId}") { backStackEntry ->
        //     val courseId = backStackEntry.arguments?.getString("courseId")
        //     if (courseId != null) {
        //         CourseDetailScreen(
        //              viewModelFactory = courseDetailViewModelFactory, // New factory needed
        //              courseId = courseId,
        //              navUp = { navController.popBackStack() }
        //         )
        //     } else {
        //         // Handle error: courseId not found, navigate back or show error
        //     }
        // }
    }
}