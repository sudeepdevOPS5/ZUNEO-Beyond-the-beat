/**
 * AURASATH Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 *
 * OPPO ColorOS Aquamorphic Toggle Switch
 */

package com.aurasath.music.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SwitchColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.aurasath.music.ui.theme.AppColors

/**
 * OPPO ColorOS Aquamorphic Toggle Switch.
 * Clean, organic pill track with water-drop spring thumb physics and Aquamorphic accent tinting.
 */
@Composable
fun GlassSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val thumbProgress by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.78f,
            stiffness = 800f
        ),
        label = "aquamorphicSwitchThumb",
    )

    val trackShape = RoundedCornerShape(percent = 50)
    val trackWidth = 50.dp
    val trackHeight = 30.dp
    val thumbSize = 24.dp
    val thumbPadding = 3.dp
    val maxTravel = trackWidth - thumbSize - thumbPadding * 2

    val trackColor = when {
        !enabled -> AppColors.divider.copy(alpha = 0.5f)
        checked -> AppColors.accent
        else -> AppColors.divider
    }

    Box(
        modifier = modifier
            .width(trackWidth)
            .height(trackHeight)
            .clip(trackShape)
            .background(trackColor)
            .then(
                if (onCheckedChange != null) {
                    Modifier.clickable(enabled = enabled) { onCheckedChange(!checked) }
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset((thumbPadding + maxTravel * thumbProgress).roundToPx(), 0) }
                .aquamorphicShadow(
                    elevation = 3.dp,
                    shapeRadius = thumbSize / 2,
                    shadowColor = AppColors.ambientShadow
                )
                .size(thumbSize)
                .clip(CircleShape)
                .background(Color.White),
        )
    }
}

/**
 * Signature-compatible stand-in for Material3's `Switch`
 */
@Composable
fun GlassSwitchCompat(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    thumbContent: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    colors: SwitchColors? = null,
) {
    GlassSwitch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        modifier = modifier,
    )
}
