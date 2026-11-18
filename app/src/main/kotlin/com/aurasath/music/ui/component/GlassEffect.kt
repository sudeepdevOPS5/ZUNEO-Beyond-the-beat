/**
 * AURASATH Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.aurasath.music.ui.component

import android.app.ActivityManager
import android.os.Build
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import com.aurasath.music.ui.component.backdrop.BackdropEffectScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.aurasath.music.ui.component.backdrop.Backdrop
import com.aurasath.music.ui.component.backdrop.drawBackdrop
import com.aurasath.music.ui.component.backdrop.isRenderEffectSupported
import com.aurasath.music.ui.component.backdrop.effects.blur
import com.aurasath.music.ui.component.backdrop.effects.colorControls
import com.aurasath.music.ui.component.backdrop.effects.lens
import com.aurasath.music.ui.component.backdrop.highlight.Highlight
import com.aurasath.music.ui.component.backdrop.highlight.HighlightStyle
import com.aurasath.music.ui.component.backdrop.shadow.Shadow

/**
 * User-configurable parameters of the liquid glass effect, sourced from DataStore
 * preferences in [com.aurasath.music.MainActivity] and distributed through
 * [LocalGlassEffectConfig].
 */
@Stable
data class GlassEffectConfig(
    val globalEnabled: Boolean = false,
    val vibrancy: Float = 1.0f,
    val blurRadius: Float = 0f,
    val lensHeight: Float = 0f,
    val lensAmount: Float = 0f,
    val chromaticAberration: Boolean = false,
    val depthEffect: Boolean = false,
    val surfaceTintColor: Color = Color.Unspecified,
    val highlightColor: Color = Color.Unspecified,
    val highlightOpacity: Float = 0f,
    val style: GlassStyle = GlassStyle.TRANSPARENT,
    val puckColor: Color = Color.Unspecified,
    val puckOpacity: Float = 1.0f,
    val surfaceOpacity: Float = 1.0f,
    val textColor: Color = Color.Unspecified,
    val playerEnabled: Boolean = false,
    val miniPlayerEnabled: Boolean = false,
    val navBarEnabled: Boolean = false,
    val sidePanelEnabled: Boolean = false,
    val sidePanelVibrancy: Float = 1.0f,
    val sidePanelBlurRadius: Float = 0f,
    val sidePanelLensHeight: Float = 0f,
    val sidePanelLensAmount: Float = 0f,
    val sidePanelColor: Color = Color.Unspecified,
    val sidePanelSurfaceOpacity: Float = 1.0f,
    val sidePanelTextColor: Color = Color.Unspecified,
) {
    /** A copy of this config with the shared effect fields swapped for the
     *  side panel's own — pass this to Modifier.liquidGlass for the side
     *  panel instead of the raw config, since liquidGlass always reads the
     *  shared fields generically. */
    fun forSidePanel(): GlassEffectConfig = copy(
        vibrancy = sidePanelVibrancy,
        blurRadius = sidePanelBlurRadius,
        lensHeight = sidePanelLensHeight,
        lensAmount = sidePanelLensAmount,
        surfaceTintColor = sidePanelColor,
        surfaceOpacity = sidePanelSurfaceOpacity,
        textColor = sidePanelTextColor,
    )
    /**
     * Whether the glass effect should be rendered for [component], taking the master
     * switch and the per-component switch into account.
     */
    fun isEnabledFor(component: GlassComponent): Boolean =
        globalEnabled && when (component) {
            GlassComponent.PLAYER -> playerEnabled
            GlassComponent.MINI_PLAYER -> miniPlayerEnabled
            GlassComponent.NAV_BAR -> navBarEnabled
            GlassComponent.SIDE_PANEL -> sidePanelEnabled
        }

    /**
     * True when at least one surface would render glass. Used to decide whether
     * capturing the app backdrop is worth anything at all — with every component
     * off, nothing samples it and recording it is pure cost.
     */
    val anyComponentEnabled: Boolean
        get() = globalEnabled &&
            (playerEnabled || miniPlayerEnabled || navBarEnabled || sidePanelEnabled)
}

/** UI surfaces that can individually opt in or out of the liquid glass effect. */
/**
 * How a glass surface is rendered.
 *
 * The full liquid treatment costs a screen capture plus a RenderEffect chain per
 * surface. The cheaper styles exist both as a performance escape hatch and as a
 * look in their own right — and because with glass off entirely the chrome fell
 * back to an opaque fill, which reads as a black blob over content.
 */
enum class GlassStyle {
    /** Blur + saturation + lens refraction + specular rim. The default. */
    LIQUID,

    /** Blur and saturation only: no lens, no rim, no chromatic aberration.
     *  Still captures the backdrop, so it is frosted, just not refractive. */
    BLUR,

    /** No capture at all — a translucent tinted fill at the configured opacity.
     *  Cheapest by a wide margin: nothing is sampled, nothing is blurred. */
    TRANSPARENT,
}

/**
 * True when [Modifier.liquidGlass] should take its cheap translucent-tint path
 * (no backdrop capture, no RenderEffect) instead of the full liquid treatment:
 * either the user explicitly chose [GlassStyle.TRANSPARENT], or the platform
 * can't do the real blur at all ([renderEffectSupported] false, below Android
 * 12). The unsupported-platform case used to fall through to a hardcoded
 * opaque-black surface instead — the "solid black instead of any
 * transparency" bug reported on older/low-end devices. Pulled out as a plain
 * function so the fallback decision is unit-testable without a Composition.
 */
fun shouldUseTranslucentGlassFallback(style: GlassStyle, renderEffectSupported: Boolean): Boolean =
    style == GlassStyle.TRANSPARENT || !renderEffectSupported

enum class GlassComponent {
    PLAYER,
    MINI_PLAYER,
    NAV_BAR,
    SIDE_PANEL,
}

/**
 * Maximum lens refraction in dp when the 0..1 preference sliders are at 1. The
 * defaults (0.5) land on the 24dp height/amount used by the library author's
 * Apple-matched LiquidBottomTabs recipe.
 */
internal const val LENS_MAX_DP = 48f

/**
 * The full screen player uses a much heavier blur than the glass pills, matching
 * Apple Music where the now playing background is a deep-blurred material while
 * only the small controls are clear liquid glass.
 */
internal const val PLAYER_BLUR_MULTIPLIER = 4f

/**
 * Lowest resolution fraction glass surfaces are rendered at (heavy blur hides it).
 *
 * Measured empirically: flattening this across all surfaces and pushing it to
 * 0.05 was tried and reverted. Two findings worth not re-deriving. First, the
 * scale is only safe in proportion to the blur — pixel-sized effect params are
 * pre-multiplied by it, so at 0.05 the default 2dp blur collapsed to ~0.3px on a
 * 3x screen and the 20x upscale had nothing smoothing it, which reads as
 * pixelation, not frost. Second, applying it at zero blur softens surfaces whose
 * whole look is clear content, for no gain: the surfaces where compression
 * actually pays (the full screen player, 12x the nav bar's area at 4x the blur)
 * already reach this floor through the ramp.
 *
 * Keep the ramp in [glassResolutionScale] rather than a flat value.
 */
internal const val MIN_GLASS_RESOLUTION_SCALE = 0.30f

/** Blur radius (dp) at or above which the minimum resolution scale is safe to use. */
internal const val FULL_QUALITY_BLUR_DP = 8f

/**
 * Resolution fraction at which a glass surface records and processes its backdrop.
 * Blur masks the upscaling, so the more blur, the lower the resolution can go: at
 * [FULL_QUALITY_BLUR_DP]+ dp of blur the surface renders at
 * [MIN_GLASS_RESOLUTION_SCALE]; with no blur it stays at full resolution so the
 * clear glass center remains crisp.
 */
fun glassResolutionScale(blurRadiusDp: Float): Float {
    val t = (blurRadiusDp / FULL_QUALITY_BLUR_DP).coerceIn(0f, 1f)
    return 1f - t * (1f - MIN_GLASS_RESOLUTION_SCALE)
}

/**
 * The backdrop blur pipeline requires [android.graphics.RenderEffect] on a
 * [android.graphics.RenderNode], which is available from Android 12 (API 31).
 */
fun isGlassSupported(sdkInt: Int = Build.VERSION.SDK_INT): Boolean = false

/**
 * Hardware performance tier for adaptive UI rendering.
 */
enum class DevicePerformanceTier {
    LOW,
    MEDIUM,
    HIGH
}

@Composable
fun rememberDevicePerformanceTier(): DevicePerformanceTier {
    return DevicePerformanceTier.HIGH
}

/** Check if device is low-RAM; returns true for LOW tier */
@Composable
fun isLowRamDevice(): Boolean = false

/**
 * Disabled in Aquamorphic design: returns false to ensure zero GPU-intensive glass/blur passes occur.
 */
@Composable
fun isGlassAllowed(): Boolean = false

/**
 * Maps vibrancy
 */
fun glassSaturation(vibrancy: Float): Float = 1f

/**
 * High contrast Aquamorphic text color
 */
fun glassContentColorFor(behind: Color, tint: Color, opacity: Float): Color {
    val effective = if (tint.isSpecified && tint != Color.Transparent) tint else behind
    return if (effective.luminance() > 0.5f) Color(0xFF141619) else Color(0xFFFFFFFF)
}

/**
 * Apple's floating pills have a thin, subtle specular line along the top edge — a
 * hint of light, not a bold ring. The library's [Highlight.Default] (0.5dp, white @
 * 0.5 alpha) reads as near-invisible at typical density, but a wide bright rim
 * overshoots in the other direction, so this lands narrower and dimmer than that.
 */
private val EdgeHighlightWidth = 0.8f.dp
private const val EdgeHighlightAlpha = 0.55f

/**
 * Real Liquid Glass isn't a highlight painted on at a fixed angle: Apple describes
 * the material as dynamically bending and shaping light "in real time, every single
 * frame," and community shader recreations implement this as one or more soft light
 * sources drifting across the surface over time (e.g. animated via sin/cos of the
 * frame clock). A perfectly static 45° rim reads as flat and synthetic by
 * comparison, so the highlight's light angle slowly drifts instead of sitting still.
 */
private const val HighlightAngleMin = 25f
private const val HighlightAngleMax = 65f
private const val HighlightDriftMillis = 7000

/** Midpoint of the [HighlightAngleMin]..[HighlightAngleMax] sweep — see the use site. */
private const val HighlightAngleFrozen = (HighlightAngleMin + HighlightAngleMax) / 2f

val LocalGlassEffectConfig = staticCompositionLocalOf { GlassEffectConfig() }

/** The backdrop content (app UI) that glass surfaces sample from. */
val LocalAppBackdrop = staticCompositionLocalOf<Backdrop?> { null }

/**
 * When set, glass surfaces in scope pool one recorded+effect-processed layer
 * per bucket this returns instead of a single reused layer — see
 * `drawBackdrop`'s `loopBucket` parameter. Meant for surfaces sampling a
 * backdrop whose source loops (a looping canvas video): a bucket's visual
 * output is identical every repeat, so it only needs computing once, ever.
 * Null (default) keeps every surface's existing single-layer behavior.
 */
val LocalBackdropLoopBucket = staticCompositionLocalOf<(() -> Int)?> { null }

/**
 * Whether the Apple Music-styled UI (iOS 26/27 liquid glass look, SF-style tab icons,
 * denser glass) is active. Read by [com.aurasath.music.ui.screens.Screens] consumers to pick
 * between the classic and iOS icon sets.
 */
val LocalAppleMusicUi = staticCompositionLocalOf { false }

/**
 * Renders this composable as a liquid glass surface sampling [LocalAppBackdrop].
 *
 * Applies the configured vibrancy, blur and lens refraction effects, then draws the
 * surface tint (theme-adaptive unless the user picked a color). Effects whose
 * parameters make them a no-op are skipped entirely to keep the RenderEffect chain
 * as short as possible, and the backdrop is processed at [glassResolutionScale] of
 * the surface resolution. Returns the receiver unchanged on devices without
 * RenderEffect support.
 *
 * [applyEdgeEffects] controls the edge treatment that makes small pills read as
 * physical glass: lens refraction, the specular highlight rim and the drop shadow.
 * It should be false for large surfaces such as the full screen player, where the
 * rim renders as a stray band of light.
 *
 * [blurRadiusDp] overrides the configured blur; the full screen player passes a
 * heavier value ([PLAYER_BLUR_MULTIPLIER]x) than the clear glass pills.
 *
 * [shape] is restricted to [CornerBasedShape] because the backdrop lens effect throws
 * [UnsupportedOperationException] for any other shape type.
 */
@Composable
fun Modifier.liquidGlass(
    config: GlassEffectConfig,
    shape: CornerBasedShape = RoundedCornerShape(0.dp),
    applyEdgeEffects: Boolean = true,
    blurRadiusDp: Float = config.blurRadius,
    highlightAlpha: Float = EdgeHighlightAlpha,
    backdropScale: Float = 1f,
    frozen: () -> Boolean = { false },
    loopBucket: (() -> Int)? = LocalBackdropLoopBucket.current,
): Modifier {
    val isLight = MaterialTheme.colorScheme.surface.luminance() > 0.5f
    val solidSurface = if (config.surfaceTintColor.isSpecified && config.surfaceTintColor != Color.Transparent) {
        config.surfaceTintColor
    } else if (isLight) {
        AquamorphicColors.LightCardSurface
    } else {
        AquamorphicColors.DarkCardSurface
    }

    return this
        .aquamorphicShadow(
            elevation = if (applyEdgeEffects) 6.dp else 0.dp,
            shapeRadius = 24.dp,
            shadowColor = if (isLight) AquamorphicColors.LightAmbientShadow else AquamorphicColors.DarkAmbientShadow,
        )
        .clip(shape)
        .background(solidSurface)
}
