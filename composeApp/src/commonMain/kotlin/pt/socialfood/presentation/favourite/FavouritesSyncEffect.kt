package pt.socialfood.presentation.favourite

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import pt.socialfood.data.network.ConnectivityObserver
import pt.socialfood.domain.use_case.favourite.SyncFavouriteRestaurantsUseCase
import pt.socialfood.domain.use_case.favourite.SyncFavouritesUseCase

/**
 * Triggers a (debounced, incremental) favourites sync — guides and restaurants — on
 * launch/foreground and on reconnect. Mount once at the app root.
 */
@Composable
fun FavouritesSyncEffect(
    syncFavourites: SyncFavouritesUseCase = koinInject(),
    syncFavouriteRestaurants: SyncFavouriteRestaurantsUseCase = koinInject(),
    connectivityObserver: ConnectivityObserver = koinInject(),
) {
    val scope = rememberCoroutineScope()

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        scope.launch { syncFavourites() }
        scope.launch { syncFavouriteRestaurants() }
    }

    LaunchedEffect(connectivityObserver) {
        var wasOnline: Boolean? = null
        connectivityObserver.isOnline.collect { isOnline ->
            if (wasOnline == false && isOnline) {
                syncFavourites()
                syncFavouriteRestaurants()
            }
            wasOnline = isOnline
        }
    }
}
