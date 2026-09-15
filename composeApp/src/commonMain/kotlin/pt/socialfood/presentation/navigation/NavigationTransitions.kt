package pt.socialfood.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.navigation3.ui.NavDisplay

internal const val NAVIGATION_TRANSITION_DURATION_MILLIS = 300
val defaultAnimationMetadata: Map<String, Any> =
    NavDisplay.transitionSpec {
        (
            scaleIn(
                initialScale = 0.9f,
                animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS),
            ) + fadeIn(animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS))
            ) togetherWith fadeOut(animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS))
    } + NavDisplay.popTransitionSpec {
        fadeIn(animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS)) togetherWith
            (
                scaleOut(
                    targetScale = 0.9f,
                    animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS),
                ) + fadeOut(animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS))
                )
    } + NavDisplay.predictivePopTransitionSpec {
        fadeIn(animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS)) togetherWith
            (
                scaleOut(
                    targetScale = 0.9f,
                    animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS),
                ) + fadeOut(animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS))
                )
    }

val slideUpAnimationMetadata: Map<String, Any> =
    NavDisplay.transitionSpec {
        // Slide new content up, keeping the old content in place underneath
        slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS),
        ) togetherWith ExitTransition.KeepUntilTransitionsFinished
    } + NavDisplay.popTransitionSpec {
        // Slide old content down, revealing the new content in place underneath
        EnterTransition.None togetherWith
            slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS),
            )
    } + NavDisplay.predictivePopTransitionSpec {
        // Slide old content down, revealing the new content in place underneath
        EnterTransition.None togetherWith
            slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS),
            )
    }

val slideHorizontalAnimationMetadata: Map<String, Any> =
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
    } + NavDisplay.predictivePopTransitionSpec {
        slideInHorizontally(
            initialOffsetX = { fullWidth -> -fullWidth / 4 },
            animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS),
        ) togetherWith
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS),
            )
    }
