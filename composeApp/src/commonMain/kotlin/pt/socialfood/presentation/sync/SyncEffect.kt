package pt.socialfood.presentation.sync

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import org.koin.compose.viewmodel.koinViewModel

/**
 * Triggers a (debounced, incremental) favourites and restaurant-visit (wish/visited) sync —
 * guides and restaurants — on launch/foreground and on reconnect. Mount once at the app root.
 */
@Composable
fun SyncEffect(viewModel: SyncViewModel = koinViewModel()) {
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        viewModel.onStart()
    }
}
