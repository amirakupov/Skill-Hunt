package com.project.skill_hunt.ui
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.project.skill_hunt.ui.login.AuthViewModel

@Composable
fun RegisterScreen(
    vm: AuthViewModel,
    navToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    val err = vm.errorMessage

    Column(Modifier.padding(16.dp)) {
        Text("Sign Up", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(email, { email = it }, label = { Text("Email") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            pass, { pass = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { vm.register(email, pass) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
        err?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(16.dp))
        TextButton(onClick = navToLogin) {
            Text("Already have an account? Log in")
        }
    }
}

@Composable
fun LoginScreen(
    vm: AuthViewModel,
    navToHome: () -> Unit,
    navToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    val err = vm.errorMessage

    // when token arrives, navigate:
    LaunchedEffect(vm.authToken) {
        if (vm.authToken != null) navToHome()
    }

    Column(Modifier.padding(16.dp)) {
        Text("Login", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(email, { email = it }, label = { Text("Email") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            pass, { pass = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { vm.login(email, pass) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log In")
        }
        err?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(16.dp))
        TextButton(onClick = navToRegister) {
            Text("Don't have an account? Sign up")
        }
    }
}

@Composable
fun ProtectedHomeScreen(
    vm: AuthViewModel,
    navToLogin: () -> Unit
) {
    var message by remember { mutableStateOf("Loading...") }

    LaunchedEffect(Unit) {
        vm.fetchProtected { message = it }
    }

    Column(Modifier.padding(16.dp)) {
        Text(message, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(16.dp))
        Button(onClick = { vm.logout(navToLogin) }) {
            Text("Log Out")
        }
    }
}
