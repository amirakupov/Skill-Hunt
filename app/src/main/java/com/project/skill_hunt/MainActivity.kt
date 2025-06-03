package com.project.skill_hunt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.project.skill_hunt.data.TokenPreferences
import com.project.skill_hunt.data.network.RetrofitInstance
import com.project.skill_hunt.data.repository.AuthRepository
import com.project.skill_hunt.data.repository.CourseRepository
import com.project.skill_hunt.ui.AppNavHost
import com.project.skill_hunt.ui.BrowseCoursesViewModelFactory
import com.project.skill_hunt.ui.CourseListViewModelFactory
import com.project.skill_hunt.ui.CourseViewModel
import com.project.skill_hunt.ui.CourseViewModelFactory
import com.project.skill_hunt.ui.login.AuthViewModelFactory
import com.project.skill_hunt.ui.theme.SkillHuntTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = TokenPreferences(this)
        val api   = RetrofitInstance.create { prefs.getToken() }
        val repo  = AuthRepository(api, prefs)
        val authVmFactory = AuthViewModelFactory(repo)
        val courseRepo      = CourseRepository(api)
        val courseVmFactory = CourseViewModelFactory(courseRepo)
        val listVmFactory    = CourseListViewModelFactory(courseRepo)
        val browseVmFactory      = BrowseCoursesViewModelFactory(courseRepo)


        setContent {
            SkillHuntTheme {
                AppNavHost(authVmFactory = authVmFactory,
                    courseListVmFactory = listVmFactory,
                    courseVmFactory = courseVmFactory,
                    browseVmFactory     = browseVmFactory,
                    courseRepo = courseRepo
                )

            }
        }
    }
}
