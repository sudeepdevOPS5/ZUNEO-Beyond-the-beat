package com.aurasath.music.ui.theme

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Design tokens mapped directly to OPPO ColorOS Aquamorphic Design System.
 * Maintained as AppleTokens for drop-in compatibility across existing codebase components.
 */
object AppleTokens {
    // Primary Aquamorphic Accent
    val AccentRed = AquamorphicColors.LightAccent // #55B8A9 (Aquamorphic light)
    val AccentDark = AquamorphicColors.DarkAccent  // #75D6C4 (Aquamorphic dark)

    // Dark Mode Surfaces - Matte Aquamorphic (no blur/glass)
    val Bg = AquamorphicColors.DarkPrimaryBackground       // #141619
    val BgElevated = AquamorphicColors.DarkSecondaryBackground // #1B1F24
    val Card = AquamorphicColors.DarkCardSurface           // #20252B
    val CardSecondary = AquamorphicColors.DarkElevatedSurface // #282E35

    // Light Mode Surfaces - Clean Aquamorphic
    val LightBg = AquamorphicColors.LightPrimaryBackground       // #F7F8FA
    val LightBgElevated = AquamorphicColors.LightSecondaryBackground // #FFFFFF
    val LightCard = AquamorphicColors.LightCardSurface           // #FFFFFF
    val LightCardSecondary = AquamorphicColors.LightElevatedSurface // #F0F2F5

    /**
     * Hairline rule / divider on whatever surface the caller sits on.
     */
    val divider: Color
        @Composable get() = if (LocalContentColor.current.luminance() > 0.5f) {
            AquamorphicColors.DarkDivider
        } else {
            AquamorphicColors.LightDivider
        }

    /**
     * Metadata text — artist names, subtitles, durations.
     */
    val Metadata = AquamorphicColors.DarkSecondaryText // #A0A7B0

    // Spacing
    val Gutter = 20.dp
    val ItemGap = 16.dp
    val SectionGap = 24.dp
    val TextGap = 4.dp

    // Typography Scale - Bold & clean sans-serif hierarchy
    val TitleLarge = 28.sp
    val TitleLargeLineHeight = 36.sp
    val SectionHeader = 20.sp
    val SectionHeaderLineHeight = 26.sp
    val ItemTitle = 16.sp
    val ItemTitleLineHeight = 22.sp
    val ItemSubtitle = 14.sp
    val ItemSubtitleLineHeight = 18.sp
    val Caption = 12.sp
    val CaptionLineHeight = 16.sp

    // Generous Aquamorphic rounded corners
    val Artwork = AquamorphicShapes.CornerArtwork       // 24dp
    val Control = AquamorphicShapes.CornerPill          // 100dp fully rounded pill
    val CardCorner = AquamorphicShapes.CornerCard       // 24dp standard cards
    val CardCornerLarge = AquamorphicShapes.CornerCardLarge // 28dp large cards

    // Natural water-drop-inspired spring physics
    object Motion {
        const val Stiffness = 750f

        /** ColorOS water-drop smooth spring */
        fun <T> standard() = spring<T>(
            dampingRatio = 0.82f,
            stiffness = Stiffness
        )

        /** ColorOS snappy interaction feedback */
        fun <T> emphasis() = spring<T>(
            dampingRatio = 0.75f,
            stiffness = 850f
        )

        /** Expressive entrance */
        fun <T> expressive() = spring<T>(
            dampingRatio = 0.70f,
            stiffness = 650f
        )

        /** Clean, lightweight fade */
        fun <T> fade() = tween<T>(durationMillis = 180)
    }

    // Adaptive contrast helpers
    fun onColor(bg: Color): Color = bg.shiftedForContrast(saturation = 0.16f, lightness = 0.96f to 0.10f)

    fun onColorSecondary(bg: Color): Color =
        bg.shiftedForContrast(saturation = 0.42f, lightness = 0.76f to 0.34f)

    fun onColorHeading(bg: Color): Color =
        bg.shiftedForContrast(saturation = 0.55f, lightness = 0.88f to 0.18f)

    private fun Color.shiftedForContrast(saturation: Float, lightness: Pair<Float, Float>): Color {
        val hsl = FloatArray(3)
        androidx.core.graphics.ColorUtils.colorToHSL(toArgb(), hsl)
        val onDark = luminance() <= 0.5f
        val target = if (onDark) lightness.first else lightness.second
        if (hsl[1] < 0.08f) {
            hsl[1] = 0f
            hsl[2] = if (target > 0.9f) 1f else if (target < 0.12f) 0.04f else target
        } else {
            hsl[1] = minOf(hsl[1], saturation)
            hsl[2] = target
        }
        return Color(androidx.core.graphics.ColorUtils.HSLToColor(hsl))
    }
}

/**
 * Material 3 Shapes bound to Aquamorphic generous rounded corner geometry
 */
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(AppleTokens.Artwork),       // 24dp
    medium = RoundedCornerShape(AppleTokens.CardCorner),   // 24dp
    large = RoundedCornerShape(AppleTokens.CardCornerLarge),// 28dp
    extraLarge = RoundedCornerShape(32.dp),
)
