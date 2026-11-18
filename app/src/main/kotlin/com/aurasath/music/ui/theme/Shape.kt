/**
 * AURASATH Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 *
 * OPPO ColorOS Aquamorphic Design System - Centralized Shapes & Corner Geometry
 */

package com.aurasath.music.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * OPPO ColorOS Aquamorphic Shape Specifications:
 * - Standard cards: 24dp
 * - Large cards: 28dp–32dp
 * - Buttons: Fully rounded pill shapes (50%)
 * - Artwork: 20dp–28dp (24dp default)
 * - Bottom navigation: 28dp
 * - Dialogs: 28dp
 * - Mini player: 28dp–32dp
 */
object AquamorphicShapes {
    val CornerArtwork = 24.dp
    val CornerCard = 24.dp
    val CornerCardLarge = 28.dp
    val CornerCardXLarge = 32.dp
    val CornerDialog = 28.dp
    val CornerNavBar = 28.dp
    val CornerMiniPlayer = 28.dp
    val CornerPill = 100.dp

    // Pre-allocated Shape objects for maximum Compose efficiency
    val Artwork = RoundedCornerShape(CornerArtwork)
    val Card = RoundedCornerShape(CornerCard)
    val CardLarge = RoundedCornerShape(CornerCardLarge)
    val CardXLarge = RoundedCornerShape(CornerCardXLarge)
    val Dialog = RoundedCornerShape(CornerDialog)
    val NavBar = RoundedCornerShape(CornerNavBar)
    val MiniPlayer = RoundedCornerShape(CornerMiniPlayer)
    val Pill = RoundedCornerShape(percent = 50)
    val SmallControl = RoundedCornerShape(12.dp)
}

/**
 * Material 3 Shapes bound directly to Aquamorphic specifications
 */
val AquamorphicM3Shapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(AquamorphicShapes.CornerArtwork),
    medium = RoundedCornerShape(AquamorphicShapes.CornerCard),
    large = RoundedCornerShape(AquamorphicShapes.CornerCardLarge),
    extraLarge = RoundedCornerShape(AquamorphicShapes.CornerDialog),
)
