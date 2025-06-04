package com.project.DOMAINLAYER.fromDataLayer // Or where your data layer repositories reside

import com.project.DOMAINLAYER.fromDataLayer.Message // Assuming correct import
import com.project.DOMAINLAYER.usecase14.LocalSentMessageNotifier
import kotlinx.coroutines.flow.Flow

/**
 * A repository that provides a stream of messages newly sent locally
 * by the current logged-in user.
 *
 * It observes the central notifier for such events.
 */
interface LocallySentMessageDataSource { // Renamed to DataSource for clarity
    fun observeNewMessagesSentLocally(): Flow<Message>
}

// You would likely provide this via DI (Hilt)
// For simplicity as an object, but an interface and class is better for testing/DI
object LocalSentMessageObserverRepository : LocallySentMessageDataSource {

    /**
     * Provides a Flow that emits messages as they are sent locally by the
     * current logged-in user.
     *
     * This Flow is sourced from the LocalSentMessageNotifier.
     */
    override fun observeNewMessagesSentLocally(): Flow<Message> {
        return LocalSentMessageNotifier.locallySentMessageFlow
    }
}