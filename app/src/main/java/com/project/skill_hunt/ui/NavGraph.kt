package com.project.skill_hunt.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.skill_hunt.ui.login.AuthViewModel
import com.project.skill_hunt.ui.login.AuthViewModelFactory

@Composable
fun AppNavHost(
    vmFactory: AuthViewModelFactory
) {
    val nav = rememberNavController()
    val vm: AuthViewModel = viewModel(factory = vmFactory)

    NavHost(nav, startDestination = if (vm.authToken != null) "home" else "login") {
        composable("register") {
            RegisterScreen(vm) { nav.navigate("login"){ popUpTo("register") { inclusive=true } } }
        }
        composable("login") {
            LoginScreen(vm,
                { nav.navigate("home"){ popUpTo("login"){ inclusive=true } } },
                { nav.navigate("register") }
            )
        }
        composable("home") {
            ProtectedHomeScreen(vm) {
                nav.navigate("login"){ popUpTo("home"){ inclusive=true } }
            }
        }
    }
}
