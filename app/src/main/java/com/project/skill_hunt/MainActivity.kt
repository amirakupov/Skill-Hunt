package com.project.skill_hunt // Package should likely be this, not data.model

// In MainActivity.kt
import android.os.Bundle
import androidx.activity.ComponentActivity // Import ComponentActivity
import androidx.activity.compose.setContent
import com.project.skill_hunt.data.network.fake.FakeApiService
import com.project.skill_hunt.ui.login.AuthViewModelFactory // Assuming this path is correct
import com.project.skill_hunt.ui.course.CreateCourseViewModelFactory // Assuming this path
// import com.project.skill_hunt.ui.browse.BrowseCoursesViewModelFactory // Future
import com.project.skill_hunt.data.repository.AuthRepository // Assuming this path
import com.project.skill_hunt.data.repository.CourseRepository // Assuming this path
import com.project.skill_hunt.data.TokenPreferences // Assuming this path
import com.project.skill_hunt.ApiService // Import  actual ApiService interface
import com.project.skill_hunt.ui.theme.SkillHuntTheme // Assuming this path for  theme
import com.project.skill_hunt.ui.AppNavHost // Assuming this path

// Messaging related imports
import com.project.skill_hunt.data.repository.MessageRepository
import com.project.skill_hunt.domainlayer_usecases.messaging.GetConversationSnippetsUseCase
import com.project.skill_hunt.domainlayer_usecases.messaging.GetMessagesForConversationUseCase
import com.project.skill_hunt.domainlayer_usecases.messaging.SendMessageUseCase
import com.project.skill_hunt.ui.messaging.ConversationListViewModelFactory
import com.project.skill_hunt.ui.messaging.ChatViewModelFactory

class MainActivity : ComponentActivity() { // Extends ComponentActivity
    private lateinit var authVmFactory: AuthViewModelFactory
    private lateinit var createCourseVmFactory: CreateCourseViewModelFactory
    // private lateinit var browseCoursesVmFactory: BrowseCoursesViewModelFactory // Assuming add this

    // Messaging ViewModel Factories
    private lateinit var conversationListVmFactory: ConversationListViewModelFactory
    private lateinit var chatVmFactory: ChatViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = TokenPreferences(this)

        // Use  actual ApiService interface type here
        val api: ApiService = FakeApiService() // Instantiate the fake one
        // val api = RetrofitInstance.create { prefs.getToken() } // This is the real one

        // Auth related
        val authRepo = AuthRepository(api, prefs)
        authVmFactory = AuthViewModelFactory(authRepo)

        // Course related
        val courseRepo = CourseRepository(api)
        createCourseVmFactory = CreateCourseViewModelFactory(courseRepo)

        // For Browse Listings ( create this ViewModel and Factory when ready)
        // val getAvailableListingsUseCase = GetAvailableListingsUseCase(courseRepo)
        // val getListingDetailsUseCase = GetListingDetailsUseCase(courseRepo)
        // browseCoursesVmFactory = BrowseCoursesViewModelFactory(getAvailableListingsUseCase, getListingDetailsUseCase)

        // Messaging related initializations
        val messageRepository = MessageRepository(api)

        val getConversationSnippetsUseCase = GetConversationSnippetsUseCase(messageRepository)
        conversationListVmFactory = ConversationListViewModelFactory(getConversationSnippetsUseCase)

        val getMessagesForConversationUseCase = GetMessagesForConversationUseCase(messageRepository)
        val sendMessageUseCase = SendMessageUseCase(messageRepository)
        chatVmFactory = ChatViewModelFactory(getMessagesForConversationUseCase, sendMessageUseCase)

        setContent {
            SkillHuntTheme { // Ensure SkillHuntTheme is correctly defined and imported
                AppNavHost(
                    // navController will be remembered by default in AppNavHost
                    authViewModelFactory = authVmFactory,
                    createCourseViewModelFactory = createCourseVmFactory,
                    // browseCoursesViewModelFactory = browseCoursesVmFactory, // When ready
                    conversationListViewModelFactory = conversationListVmFactory,
                    chatViewModelFactory = chatVmFactory
                )
            }
        }
    }
}