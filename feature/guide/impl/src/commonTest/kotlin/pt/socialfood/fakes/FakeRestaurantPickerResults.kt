package pt.socialfood.fakes

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.presentation.restaurant.navigation.RestaurantPickerResults

/** Buffers results per key like the real implementation, so a result can be published before collection. */
class FakeRestaurantPickerResults : RestaurantPickerResults {
    private val channels = mutableMapOf<String, Channel<Restaurant>>()

    override fun results(requestKey: String): Flow<Restaurant> = channel(requestKey).receiveAsFlow()

    override fun publish(requestKey: String, restaurant: Restaurant) {
        channel(requestKey).trySend(restaurant)
    }

    private fun channel(requestKey: String): Channel<Restaurant> =
        channels.getOrPut(requestKey) { Channel(Channel.UNLIMITED) }
}
