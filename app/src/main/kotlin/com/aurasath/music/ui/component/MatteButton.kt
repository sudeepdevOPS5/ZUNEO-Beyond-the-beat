/**
 * AURASATH Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 *
 * OPPO ColorOS Aquamorphic Matte Button
 */

package com.aurasath.music.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aurasath.music.ui.theme.AppColors
import com.aurasath.music.ui.theme.AquamorphicDimensions
import com.aurasath.music.ui.theme.AquamorphicShapes
import com.aurasath.music.ui.utils.bounceClick

/**
 * Organic pill-shaped matte button with ColorOS water-drop spring feedback.
 */
@Composable
fun MatteButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = AquamorphicShapes.Pill,
    backgroundColor: Color = AppColors.accent,
    contentColor: Color = if (AppColors.isDark) Color(0xFF141619) else Color.White,
    elevation: Dp = 4.dp,
    contentPadding: PaddingValues = AquamorphicDimensions.PillPadding,
    content: @Composable () -> Unit,
) {
    val actualBg = if (enabled) backgroundColor else backgroundColor.copy(alpha = 0.4f)
    val actualContent = if (enabled) contentColor else contentColor.copy(alpha = 0.4f)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .aquamorphicShadow(
                elevation = if (enabled) elevation else 0.dp,
                shapeRadius = 24.dp,
                shadowColor = AppColors.ambientShadow
            )
            .clip(shape)
            .background(actualBg)
            .bounceClick(enabled = enabled, scaleDown = 0.95f, onClick = onClick)
            .padding(contentPadding)
    ) {
        CompositionLocalProvider(LocalContentColor provides actualContent) {
            ProvideTextStyle(
                MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    content()
                }
            }
        }
    }
}

/**
 * Secondary / Elevated Matte Button (Pill or Rounded)
 */
@Composable
fun MatteSecondaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = AquamorphicShapes.Pill,
    backgroundColor: Color = AppColors.elevatedSurface,
    contentColor: Color = AppColors.primaryText,
    elevation: Dp = 2.dp,
    contentPadding: PaddingValues = AquamorphicDimensions.PillPadding,
    content: @Composable () -> Unit,
) {
    MatteButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        elevation = elevation,
        contentPadding = contentPadding,
        content = content
    )
}
