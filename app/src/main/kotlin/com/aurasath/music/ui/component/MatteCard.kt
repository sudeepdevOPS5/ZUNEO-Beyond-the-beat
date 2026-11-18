/**
 * AURASATH Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 *
 * OPPO ColorOS Aquamorphic Matte Card
 */

package com.aurasath.music.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aurasath.music.ui.theme.AppColors
import com.aurasath.music.ui.theme.AquamorphicShapes
import com.aurasath.music.ui.utils.bounceClick

/**
 * Clean, lightweight, solid matte card component.
 * Replaces frosted/glass surfaces with ColorOS Aquamorphic solid matte card containers.
 */
@Composable
fun MatteCard(
    modifier: Modifier = Modifier,
    shape: Shape = AquamorphicShapes.Card,
    cornerRadius: Dp = AquamorphicShapes.CornerCard,
    backgroundColor: Color = AppColors.cardSurface,
    elevation: Dp = 6.dp,
    padding: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val clickModifier = if (onClick != null) {
        Modifier.bounceClick(scaleDown = 0.96f, onClick = onClick)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .aquamorphicShadow(
                elevation = elevation,
                shapeRadius = cornerRadius,
                shadowColor = AppColors.ambientShadow
            )
            .clip(shape)
            .background(backgroundColor)
            .then(clickModifier)
            .padding(padding),
        content = content
    )
}
