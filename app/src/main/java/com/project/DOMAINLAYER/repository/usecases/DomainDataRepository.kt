import androidx.paging.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface DomainDataRepository {
    // Fetches from network, saves to Room, Room then emits to app via Flows
    suspend fun refreshUsers(): Result<Unit>
    suspend fun refreshConversationsForUser(userId: String): Result<Unit>
    suspend fun refreshMessagesForConversation(conversationId: String): Result<Unit>
    suspend fun sendDomainMessage(
        conversationId: String,
        senderId: String,
        receiverId: String, // Or whatever backend needs
        text: String
    ): Result<MessageDataType> // Returns the domain type of sent message

    // Flows that observe Room and emit Domain Types
    fun observeUserById(userId: String): Flow<UserDataType?>
    fun observeAllUsers(): Flow<List<UserDataType>>
    fun observeConversationsForUser(userId: String): Flow<List<ConversationData>>
    fun observeMessagesForConversation(conversationId: String): Flow<List<MessageDataType>>
}

@Singleton
class DomainDataRepositoryImpl @Inject constructor(
    private val backendApi: BackendApiRepository, // The "Catcher"
    private val userDao: UserDao,
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao,
    // To update the singletons if they are still needed for some immediate UI feedback
    // or if InMemoryMessageRepository directly observes them.
    // If InMemoryMessageRepository observes Room via this DomainDataRepository, then
    // direct update to singletons here might be redundant.
    private val currentLoggedInUserHolder: CurrentLoggedInUser, // Assuming this is your singleton
    private val allUsersHolder: AllUsersUnderTheSunMoonAndStars,
    private val userActiveConversationsHolder: UserActiveConversations,
    private val conversationMessagesStoreHolder: ConversationMessagesStore
) : DomainDataRepository {

    override suspend fun refreshUsers(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userDtosResult = backendApi.getAllUsers()
            userDtosResult.fold(
                onSuccess = { dtos ->
                    userDao.insertUsers(dtos.map { it.toUserEntity() })
                    // Optionally update AllUsersUnderTheSunMoonAndStars directly if needed
                    // allUsersHolder.setAllUsers(dtos.map { it.toUserEntity().toUserDataType() })
                    Result.success(Unit)
                },
                onFailure = { Result.failure(it) }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    // ... similar refresh methods for conversations and messages ...

    override suspend fun sendDomainMessage(...): Result<MessageDataType> =
        withContext(Dispatchers.IO) {
        val sendResult = backendApi.sendMessage(...)
        sendResult.fold(
            onSuccess = { messageDto ->
                messageDao.insertMessage(messageDto.toMessageEntity()) // Save to Room
                // Potentially update ConversationMessagesStoreHolder immediately
                // conversationMessagesStoreHolder.addMessage(messageDto.toMessageEntity().toMessageDataType())
                Result.success(messageDto.toMessageEntity().toMessageDataType())
            },
            onFailure = { Result.failure(it) }
        )
    }

    override fun observeAllUsers(): Flow<List<UserDataType>> {
        return userDao.getAllUsers().map { entities ->
            entities.map { it.toUserDataType() }
        }
    }
    // ... similar observe methods for other data, transforming Entity to DomainType ...
}