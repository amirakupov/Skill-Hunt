package com.project.skill_hunt.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.project.skill_hunt.R
import com.project.skill_hunt.data.TokenPreferences
import com.project.skill_hunt.ui.theme.lightBlue
import com.project.skill_hunt.ui.theme.lighterBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        delay(2000L)  // 2 seconds splash delay

        val prefs = TokenPreferences(context)
        val token = prefs.getToken()

        if (token.isNullOrBlank()) {
            navController.navigate("login") {               // TEST SCREENS HERE
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("home") {                // TEST HERE ALSO
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(lighterBlue)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "SkillHunt Logo",
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .clip(RoundedCornerShape(16.dp))
        )
    }
}
