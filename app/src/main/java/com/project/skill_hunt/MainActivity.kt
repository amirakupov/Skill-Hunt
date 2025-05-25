// In MainActivity.kt
import android.os.Bundle
import androidx.activity.compose.setContent
import com.project.skill_hunt.data.network.fake.FakeApiService //tempo Import your fake service

class MainActivity : ComponentActivity() {
    private lateinit var authVmFactory: AuthViewModelFactory
    private lateinit var createCourseVmFactory: CreateCourseViewModelFactory
    private lateinit var browseCoursesVmFactory: BrowseCoursesViewModelFactory // Assuming you add this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = TokenPreferences(this)

        //tempo --- TEMPORARY SWITCH TO FAKE API SERVICE ---
        val api: com.google.firebase.appdistribution.gradle.ApiService = FakeApiService() //tempo Instantiate the fake one
        //tempo val api = RetrofitInstance.create { prefs.getToken() } //tempo This is the real one

        // Auth related (will use the fake API if needed)
        val authRepo = AuthRepository(api, prefs) // AuthRepository stays the same
        authVmFactory = AuthViewModelFactory(authRepo)

        // Course related
        val courseRepo = CourseRepository(api) // CourseRepository stays the same, uses the provided 'api'
        createCourseVmFactory = CreateCourseViewModelFactory(courseRepo) // Your existing factory

        // For Browse Listings (you'll create this ViewModel and Factory)
        // Example:
        // val getAvailableListingsUseCase = GetAvailableListingsUseCase(courseRepo)
        // val getListingDetailsUseCase = GetListingDetailsUseCase(courseRepo)
        // browseCoursesVmFactory = BrowseCoursesViewModelFactory(getAvailableListingsUseCase, getListingDetailsUseCase)

        setContent {
            SkillHuntTheme {
                AppNavHost(
                    authViewModelFactory = authVmFactory,
                    createCourseViewModelFactory = createCourseVmFactory
                    // browseCoursesViewModelFactory = browseCoursesVmFactory
                )
            }
        }
    }
}