package com.project.skill_hunt.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.project.skill_hunt.data.repository.CourseRepository
import com.project.skill_hunt.ui.login.AuthViewModel
import com.project.skill_hunt.ui.login.AuthViewModelFactory
import com.project.skill_hunt.ui.screens.AddCourseScreen
import com.project.skill_hunt.ui.screens.BrowseCoursesScreen
import com.project.skill_hunt.ui.screens.CourseDetailScreen
import com.project.skill_hunt.ui.screens.CourseListScreen
import com.project.skill_hunt.ui.screens.SplashScreen

@Composable
fun AppNavHost(
    authVmFactory: AuthViewModelFactory,
    courseVmFactory: CourseViewModelFactory,
    courseListVmFactory: CourseListViewModelFactory,
    browseVmFactory: BrowseCoursesViewModelFactory,
    courseRepo: CourseRepository
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
            Spacer(Modifier.height(8.dp))
            // “Browse Courses” for unauthenticated
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
            Spacer(Modifier.height(8.dp))
            // “Browse Courses” for unauthenticated
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
        composable("browse") {
            val browseVm: BrowseCoursesViewModel = viewModel(factory = browseVmFactory)
            BrowseCoursesScreen(
                vm = browseVm,
                navToDetail = { courseId ->
                    navController.navigate("courseDetail/$courseId")
                }
            )
        }
        composable(
            route = "courseDetail/{courseId}",
            arguments = listOf(navArgument("courseId") { type = NavType.LongType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getLong("courseId") ?: 0L
            CourseDetailScreen(
                courseId = courseId,
                repo = courseRepo,
                navUp = { navController.popBackStack() }
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
    }
}
