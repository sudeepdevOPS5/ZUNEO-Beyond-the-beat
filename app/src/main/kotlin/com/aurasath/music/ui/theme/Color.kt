/**
 * AURASATH Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 *
 * OPPO ColorOS Aquamorphic Design System - Centralized Color Palette
 */

package com.aurasath.music.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * OPPO ColorOS Aquamorphic Design Palette
 *
 * Light Mode:
 * - Primary Background:   #F7F8FA
 * - Secondary Background: #FFFFFF
 * - Card Surface:         #FFFFFF
 * - Elevated Surface:     #F0F2F5
 * - Primary Text:         #141619
 * - Secondary Text:       #777D86
 * - Divider:              #E7E9ED
 * - Accent:               #55B8A9
 *
 * Dark Mode:
 * - Primary Background:   #141619
 * - Secondary Background: #1B1F24
 * - Card Surface:         #20252B
 * - Elevated Surface:     #282E35
 * - Primary Text:         #FFFFFF
 * - Secondary Text:       #A0A7B0
 * - Divider:              #30363D
 * - Accent:               #75D6C4
 */
object AquamorphicColors {
    // Light Mode Palette
    val LightPrimaryBackground = Color(0xFFF7F8FA)
    val LightSecondaryBackground = Color(0xFFFFFFFF)
    val LightCardSurface = Color(0xFFFFFFFF)
    val LightElevatedSurface = Color(0xFFF0F2F5)
    val LightPrimaryText = Color(0xFF141619)
    val LightSecondaryText = Color(0xFF777D86)
    val LightDivider = Color(0xFFE7E9ED)
    val LightAccent = Color(0xFF55B8A9)

    // Dark Mode Palette
    val DarkPrimaryBackground = Color(0xFF141619)
    val DarkSecondaryBackground = Color(0xFF1B1F24)
    val DarkCardSurface = Color(0xFF20252B)
    val DarkElevatedSurface = Color(0xFF282E35)
    val DarkPrimaryText = Color(0xFFFFFFFF)
    val DarkSecondaryText = Color(0xFFA0A7B0)
    val DarkDivider = Color(0xFF30363D)
    val DarkAccent = Color(0xFF75D6C4)

    // Ambient Shadows (Subtle, diffuse, organic)
    val LightAmbientShadow = Color(0x0F141619) // 6% black
    val DarkAmbientShadow = Color(0x33000000)  // 20% black
}

/**
 * Unified Aquamorphic Color Token Set for runtime consumption
 */
data class AquamorphicColorTokens(
    val primaryBackground: Color,
    val secondaryBackground: Color,
    val cardSurface: Color,
    val elevatedSurface: Color,
    val primaryText: Color,
    val secondaryText: Color,
    val divider: Color,
    val accent: Color,
    val ambientShadow: Color,
    val isDark: Boolean,
)

val LocalAquamorphicColors = staticCompositionLocalOf {
    AquamorphicColorTokens(
        primaryBackground = AquamorphicColors.DarkPrimaryBackground,
        secondaryBackground = AquamorphicColors.DarkSecondaryBackground,
        cardSurface = AquamorphicColors.DarkCardSurface,
        elevatedSurface = AquamorphicColors.DarkElevatedSurface,
        primaryText = AquamorphicColors.DarkPrimaryText,
        secondaryText = AquamorphicColors.DarkSecondaryText,
        divider = AquamorphicColors.DarkDivider,
        accent = AquamorphicColors.DarkAccent,
        ambientShadow = AquamorphicColors.DarkAmbientShadow,
        isDark = true,
    )
}

val AppColors: AquamorphicColorTokens
    @Composable
    @ReadOnlyComposable
    get() = LocalAquamorphicColors.current
