package com.project.skill_hunt.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.skill_hunt.R
import com.project.skill_hunt.ui.login.AuthViewModel
import kotlinx.coroutines.launch

private val lightBlue = Color(0xFFD1EBF9)

@Composable
fun LoginScreen(
    vm: AuthViewModel,
    navToHome: () -> Unit,
    navToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    val err = vm.errorMessage

    LaunchedEffect(vm.authToken) {
        if (vm.authToken != null) navToHome()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(16.dp))
        )

        Spacer(Modifier.height(16.dp))

        Text(
            "LOGIN",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            email,
            { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            pass,
            { pass = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { vm.login(email, pass) },
            colors = ButtonDefaults.buttonColors(containerColor = lightBlue),
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
            Text("Don't have an account? Sign up", color = lightBlue)
        }
    }
}

@Composable
fun RegisterScreen(
    vm: AuthViewModel,
    navToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    val err = vm.errorMessage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(16.dp))
        )

        Spacer(Modifier.height(16.dp))

        Text(
            "SIGN UP",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            email,
            { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            pass,
            { pass = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { vm.register(email, pass) },
            colors = ButtonDefaults.buttonColors(containerColor = lightBlue),
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
            Text("Already have an account? Log in", color = lightBlue)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProtectedHomeScreen(
    vm: AuthViewModel,
    navToLogin: () -> Unit,
    navToAddCourse: () -> Unit,
    navToCourses: () -> Unit,
    navToListings: () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.homedrawerplaceholder),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Username Placeholder", fontWeight = FontWeight.Bold)
                        Text("user@email.com", fontSize = 12.sp)
                    }
                }
                Divider()
                NavigationDrawerItem(
                    label = { Text("Home") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } }
                )
                NavigationDrawerItem(
                    label = { Text("Browse Courses") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navToListings()
                        }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("My Profile") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navToCourses()
                        }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Sign Out") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            vm.logout(navToLogin)
                        }
                    }
                )
            }
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {},
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Menu")
                            }
                        }
                    )
                },
                content = { padding ->
                    Column(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(16.dp))
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(32.dp))
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(24.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { navToListings() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF546E91))
                            ) {
                                Text("Browse Skill Courses", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Button(
                                onClick = { navToAddCourse() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF546E91))
                            ) {
                                Text("Offer a Skill Course", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            )
        }
    )
}

@Composable
fun PlaceholderScreen(name: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("TODO: $name screen", fontSize = 20.sp)
    }
}