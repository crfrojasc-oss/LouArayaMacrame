package com.example.louarayamacrame.ui.theme

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally

object AnimationSpecs {
    // Standard Durations (200ms - 400ms)
    const val DURATION_FAST = 200
    const val DURATION_MEDIUM = 300
    const val DURATION_SLOW = 400

    // Standard Easings
    val StandardEasing: Easing = FastOutSlowInEasing
    val DecelerateEasing: Easing = LinearOutSlowInEasing

    // Navigation Transitions
    val NavEnterTransition: EnterTransition = fadeIn(tween(DURATION_MEDIUM, easing = StandardEasing)) +
            slideInHorizontally(initialOffsetX = { 180 }, animationSpec = tween(DURATION_MEDIUM, easing = StandardEasing))

    val NavExitTransition: ExitTransition = fadeOut(tween(DURATION_MEDIUM, easing = StandardEasing)) +
            slideOutHorizontally(targetOffsetX = { -180 }, animationSpec = tween(DURATION_MEDIUM, easing = StandardEasing))

    val NavPopEnterTransition: EnterTransition = fadeIn(tween(DURATION_MEDIUM, easing = StandardEasing)) +
            slideInHorizontally(initialOffsetX = { -180 }, animationSpec = tween(DURATION_MEDIUM, easing = StandardEasing))

    val NavPopExitTransition: ExitTransition = fadeOut(tween(DURATION_MEDIUM, easing = StandardEasing)) +
            slideOutHorizontally(targetOffsetX = { 180 }, animationSpec = tween(DURATION_MEDIUM, easing = StandardEasing))

    // Form Entrance Transition
    val FormEntryTransition: EnterTransition = fadeIn(tween(DURATION_MEDIUM, easing = DecelerateEasing)) +
            slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(DURATION_MEDIUM, easing = DecelerateEasing))
}
