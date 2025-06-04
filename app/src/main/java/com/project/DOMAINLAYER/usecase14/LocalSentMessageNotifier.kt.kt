package com.project.DOMAINLAYER.usecase14

import com.project.DOMAINLAYER.fromDataLayer.Message // Assuming correct import
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.GlobalScope // Or a more specific scope if using DI
import kotlinx.coroutines.launch
import java.util.Date // For constructing a dummy message
import java.util.UUID // For constructing a dummy message

/**
 * Notifies interested observers about messages sent locally by the current logged-in user.
 * This acts as a dedicated event bus for locally sent messages.
 */
object LocalSentMessageNotifier {

    private val _locallySentMessageFlow = MutableSharedFlow<Message>(replay = 0, extraBufferCapacity = 64)
    val locallySentMessageFlow: SharedFlow<Message> = _locallySentMessageFlow.asSharedFlow()

    /**
     * Call this function when a message has been successfully sent by the current logged-in user.
     *
     * @param message The Message object that was just sent.
     */
    fun notifyMessageSentLocally(message: Message) {
        GlobalScope.launch { // Consider using a more structured scope
            _locallySentMessageFlow.emit(message)
        }
    }

    // --- Dummy function for demonstration and design discussion ---
    /**
     * THIS IS A DUMMY FUNCTION - NOT CALLED IN PRODUCTION.
     * It demonstrates how a component (like a ViewModel) would construct a Message
     * object after a successful send operation and then notify the system.
     */
    @Suppress("unused", "UNUSED_PARAMETER") // To avoid IDE warnings for an uncalled function
    fun conceptualExampleOfNotifying(
        sentMessageId: String,
        conversationId: String,
        senderId: String,
        senderName: String?, // Name of the sender
        receiverId: String,
        text: String
    ) {
        val conceptualTimestamp = Date() // Or ideally, a timestamp from the actual send operation

        // In a real scenario, if UIrepository.sendMessage returned more details or a Message object,
        // we'd use that. Here, we reconstruct it.
        val conceptuallySentMessage = Message(
            id = sentMessageId, // ID from the successful send operation
            conversationId = conversationId,
            senderId = senderId,
            receiverId = receiverId,
            text = text,
            timestamp = conceptualTimestamp,
            isRead = false, // A newly sent message isn't read by the receiver yet
            senderName = senderName
        )
        // This is the actual notification call that would happen
        notifyMessageSentLocally(conceptuallySentMessage)
        println("[DUMMY NOTIFIER] Conceptually notified sent message: ${conceptuallySentMessage.text}")
    }
}