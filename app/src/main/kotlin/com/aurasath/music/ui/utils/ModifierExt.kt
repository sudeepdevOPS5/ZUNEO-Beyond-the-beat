package com.aurasath.music.ui.utils

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Springy "wobble" on press for an existing [interactionSource]: the target scales down
 * while held and overshoots back on release (low damping = bouncy). Use on components that
 * already own an interaction source — nav bar items, glass buttons/pills — where
 * [bounceClick] can't wrap the click. Icon/label only; doesn't affect layout.
 */
fun Modifier.pressWobble(
    interactionSource: InteractionSource,
    pressedScale: Float = 0.86f,
) = composed {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = spring(
            dampingRatio = 0.38f,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "pressWobble",
    )
    graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * OPPO ColorOS Aquamorphic natural water-drop-inspired press animation.
 * Scales down gently to ~0.95 on press, returning smoothly to 1.0 with natural spring physics.
 */
fun Modifier.aquamorphicPress(
    interactionSource: InteractionSource,
    pressedScale: Float = 0.95f,
): Modifier = composed {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = spring(
            dampingRatio = 0.75f,
            stiffness = 750f,
        ),
        label = "aquamorphicPress",
    )
    graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/** Clickable with water-drop spring scale animation (0.95f press) */
fun Modifier.bounceClick(
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
    scaleDown: Float = 0.95f,
    onClick: () -> Unit
): Modifier = composed {
    val actualSource = interactionSource ?: remember { MutableInteractionSource() }
    val pressed by actualSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) scaleDown else 1f,
        animationSpec = spring(
            dampingRatio = 0.75f,
            stiffness = 750f,
        ),
        label = "bounceClick",
    )
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = actualSource,
            indication = indication,
            enabled = enabled,
            onClick = onClick
        )
}

/** [combinedClickable] with water-drop spring scale animation */
fun Modifier.combinedBounceClick(
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
    scaleDown: Float = 0.95f,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    onClick: () -> Unit
): Modifier = composed {
    val actualSource = interactionSource ?: remember { MutableInteractionSource() }
    val pressed by actualSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) scaleDown else 1f,
        animationSpec = spring(
            dampingRatio = 0.75f,
            stiffness = 750f,
        ),
        label = "combinedBounceClick",
    )
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .combinedClickable(
            interactionSource = actualSource,
            indication = indication,
            enabled = enabled,
            onLongClick = onLongClick,
            onDoubleClick = onDoubleClick,
            onClick = onClick
        )
}
