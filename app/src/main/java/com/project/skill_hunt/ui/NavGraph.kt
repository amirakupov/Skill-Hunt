package com.project.skill_hunt.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.skill_hunt.ui.login.AuthViewModel
import com.project.skill_hunt.ui.login.AuthViewModelFactory
import com.project.skill_hunt.ui.screens.CourseListScreen
import com.project.skill_hunt.ui.screens.SplashScreen

@Composable
fun AppNavHost(
    authVmFactory: AuthViewModelFactory,
    courseVmFactory: CourseViewModelFactory,
    courseListVmFactory: CourseListViewModelFactory
) {
    val navController = rememberNavController()
    val authVm: AuthViewModel = viewModel(factory = authVmFactory)

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(navController)
        }

        composable("register") {
            RegisterScreen(authVm) {
                navController.navigate("login") {
                    popUpTo("register") { inclusive = true }
                }
            }
        }

        composable("login") {
            LoginScreen(
                vm = authVm,
                navToHome = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                navToRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("home") {
            ProtectedHomeScreen(
                vm = authVm,
                navToLogin = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                navToAddCourse = {
                    navController.navigate("addCourse")
                },
                navToCourses = {
                    navController.navigate("courses")
                },
                navToListings = {
                    navController.navigate("browse")
                }
            )
        }

        composable("courses") {
            val listVm: CourseListViewModel = viewModel(factory = courseListVmFactory)
            CourseListScreen(
                vm = listVm,
                onAddCourse = {
                    navController.navigate("addCourse")
                }
            )
        }

        composable("addCourse") {
            val addVm: CourseViewModel = viewModel(factory = courseVmFactory)
            AddCourseScreen(addVm) {
                navController.popBackStack()
            }
        }

        // Placeholder for future implementation
        composable("browse") {
            // TODO: Replace with real BrowseCourseScreen when implemented
            PlaceholderScreen("Browse Courses")
        }
    }
}
