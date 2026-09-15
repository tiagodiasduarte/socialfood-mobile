package pt.socialfood.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.defaultPopTransitionSpec
import androidx.navigation3.ui.defaultPredictivePopTransitionSpec
import androidx.navigation3.ui.defaultTransitionSpec

internal const val NAVIGATION_TRANSITION_DURATION_MILLIS = 300

@Suppress("UNCHECKED_CAST")
internal val defaultAnimationMetadata: Map<String, Any> = run {
    val transition = defaultTransitionSpec<Route>()
    val popTransition = defaultPopTransitionSpec<Route>()
    val predictivePopTransition = defaultPredictivePopTransitionSpec<Route>()

    NavDisplay.transitionSpec {
        (this as AnimatedContentTransitionScope<Scene<Route>>).transition()
    } + NavDisplay.popTransitionSpec {
        (this as AnimatedContentTransitionScope<Scene<Route>>).popTransition()
    } + NavDisplay.predictivePopTransitionSpec { edge ->
        (this as AnimatedContentTransitionScope<Scene<Route>>).predictivePopTransition(edge)
    }
}

internal val slideUpAnimationMetadata: Map<String, Any> =
    NavDisplay.transitionSpec {
        slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS),
        ) togetherWith ExitTransition.None
    } + NavDisplay.popTransitionSpec {
        EnterTransition.None togetherWith
            slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS),
            )
    }

internal val slideHorizontalAnimationMetadata: Map<String, Any> =
    NavDisplay.transitionSpec {
        slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS),
        ) togetherWith
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth / 4 },
                animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS),
            )
    } + NavDisplay.popTransitionSpec {
        slideInHorizontally(
            initialOffsetX = { fullWidth -> -fullWidth / 4 },
            animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS),
        ) togetherWith
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS),
            )
    }
