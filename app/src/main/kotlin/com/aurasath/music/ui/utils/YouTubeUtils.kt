/**
 * ZUNEO Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.aurasath.music.ui.utils

fun String.resize(
    width: Int? = null,
    height: Int? = null,
): String {
    if (width == null && height == null) return this

    // i.ytimg.com handling (YouTube video thumbnails)
    // On YouTube, hqdefault.jpg (480x360) is guaranteed on 100% of videos.
    // sddefault.jpg (640x480) exists on virtually all music uploads.
    // maxresdefault.jpg (1280x720) 404s on millions of music tracks.
    // Using sddefault.jpg for large player views avoids 404 blank boxes while rendering sharply;
    // hqdefault.jpg is used for grids and list items for instant zero-latency loading.
    if (this.contains("i.ytimg.com")) {
        val targetQuality = if (width != null && width >= 700) "sddefault.jpg" else "hqdefault.jpg"
        return this.replace(
            Regex("(default|mqdefault|hqdefault|sddefault|maxresdefault)\\.jpg"),
            targetQuality
        )
    }

    // googleusercontent.com and ggpht.com handling (includes lh3-lh6, yt3, etc.)
    if (this.contains("googleusercontent.com") || this.contains("ggpht.com")) {
        val w = width ?: height ?: 800
        val h = height ?: width ?: 800

        // Square album covers (=w)
        if (this.contains("=w")) {
            return this.replace(Regex("=w\\d+-h\\d+.*$"), "=w$w-h$h-l90-rj")
        }

        // Avatars & artist images (=s or -s)
        // Keep crop/format parameters (e.g. -c-k-c0x00ffffff-no-rj) intact to prevent 404 errors!
        if (this.contains("=s")) {
            return this.replace(Regex("=s\\d+"), "=s$w")
        }
        if (this.contains("-s")) {
            return this.replace(Regex("-s\\d+"), "-s$w")
        }

        // Fallback for googleusercontent URLs without dimensions
        if (this.contains("googleusercontent.com")) {
            val base = this.substringBefore("=")
            return "$base=w$w-h$h-l90-rj"
        }
    }

    return this
}
