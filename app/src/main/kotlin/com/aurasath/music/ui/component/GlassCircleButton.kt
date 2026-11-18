/**
 * AURASATH Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 *
 * OPPO ColorOS Aquamorphic Circular Matte Button
 */

package com.aurasath.music.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aurasath.music.ui.theme.AppColors
import com.aurasath.music.ui.utils.aquamorphicPress

/**
 * Solid matte circular button for Aquamorphic UI.
 * Replaces old frosted glass buttons with solid matte elevated surfaces, subtle ambient shadow,
 * and natural water-drop spring feedback.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GlassCircleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    size: Dp = 44.dp,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val contentColor = AppColors.primaryText
    val backgroundColor = AppColors.elevatedSurface

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .aquamorphicShadow(
                elevation = 4.dp,
                shapeRadius = size / 2,
                shadowColor = AppColors.ambientShadow
            )
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor)
            .aquamorphicPress(interactionSource, pressedScale = 0.94f)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                role = Role.Button,
                indication = ripple(bounded = false, radius = size / 2),
                interactionSource = interactionSource,
            )
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            content()
        }
    }
}
