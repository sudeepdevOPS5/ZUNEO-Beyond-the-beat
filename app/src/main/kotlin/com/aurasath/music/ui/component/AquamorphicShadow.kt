/**
 * AURASATH Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 *
 * OPPO ColorOS Aquamorphic Subtle Ambient Shadow
 */

package com.aurasath.music.ui.component

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Draws a subtle, organic, diffuse ambient shadow without sharp borders.
 * Fits OPPO ColorOS Aquamorphic specifications: soft, light dispersion, zero harsh edges.
 */
fun Modifier.aquamorphicShadow(
    elevation: Dp = 8.dp,
    shapeRadius: Dp = 24.dp,
    shadowColor: Color = Color.Black.copy(alpha = 0.06f),
    offsetY: Dp = 3.dp,
): Modifier = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        val blurPx = elevation.toPx()
        val radiusPx = shapeRadius.toPx()
        val offsetYPx = offsetY.toPx()

        if (blurPx > 0f) {
            frameworkPaint.color = shadowColor.toArgb()
            frameworkPaint.maskFilter = android.graphics.BlurMaskFilter(
                blurPx,
                android.graphics.BlurMaskFilter.Blur.NORMAL
            )
            canvas.drawRoundRect(
                left = 0f,
                top = offsetYPx,
                right = size.width,
                bottom = size.height + offsetYPx,
                radiusX = radiusPx,
                radiusY = radiusPx,
                paint = paint
            )
        }
    }
}
