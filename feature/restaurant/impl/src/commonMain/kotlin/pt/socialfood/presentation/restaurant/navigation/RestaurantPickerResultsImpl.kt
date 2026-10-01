package pt.socialfood.presentation.restaurant.navigation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import pt.socialfood.domain.model.Restaurant

/**
 * One unbounded channel per request key, so a result waits for its caller and is delivered once.
 * Only used from the main thread (ViewModel scopes), so the map needs no locking.
 */
internal class RestaurantPickerResultsImpl : RestaurantPickerResults {
    private val channels = mutableMapOf<String, Channel<Restaurant>>()

    override fun results(requestKey: String): Flow<Restaurant> = channel(requestKey).receiveAsFlow()

    override fun publish(requestKey: String, restaurant: Restaurant) {
        channel(requestKey).trySend(restaurant)
    }

    private fun channel(requestKey: String): Channel<Restaurant> =
        channels.getOrPut(requestKey) { Channel(Channel.UNLIMITED) }
}
