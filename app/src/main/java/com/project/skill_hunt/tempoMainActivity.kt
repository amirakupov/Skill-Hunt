package com.project.skill_hunt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
// ... other imports ...
import com.project.skill_hunt.data.TokenPreferences
import com.project.skill_hunt.data.network.RetrofitInstance
import com.project.skill_hunt.data.repository.AuthRepository
import com.project.skill_hunt.data.repository.CourseRepository // Import CourseRepository
import com.project.skill_hunt.ui.AppNavHost
import com.project.skill_hunt.ui.login.AuthViewModelFactory
import com.project.skill_hunt.ui.createcourse.CreateCourseViewModelFactory // Import Factory
import com.project.skill_hunt.ui.theme.SkillHuntTheme


class tempoMainActivity : ComponentActivity() {
    // Keep factories as properties if they need to be accessed by multiple composables directly
    // or pass them down through AppNavHost constructor if that's cleaner for  setup.
    private lateinit var authVmFactory: AuthViewModelFactory
    private lateinit var createCourseVmFactory: CreateCourseViewModelFactory
    // Add factory for BrowseListingsViewModel later

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = TokenPreferences(this)
        val api = RetrofitInstance.create { prefs.getToken() } // Shared API service

        // Auth related
        val authRepo = AuthRepository(api, prefs)
        authVmFactory = AuthViewModelFactory(authRepo)

        // Course related
        val courseRepo = CourseRepository(api) // Uses the same ApiService instance
        createCourseVmFactory = CreateCourseViewModelFactory(courseRepo)

        setContent {
            SkillHuntTheme {
                // Pass all necessary factories to AppNavHost
                AppNavHost(
                    authViewModelFactory = authVmFactory,
                    createCourseViewModelFactory = createCourseVmFactory
                    // Add listingsViewModelFactory later
                )
            }
        }
    }
}