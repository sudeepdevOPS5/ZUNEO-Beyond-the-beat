package com.music.innertube.models

import com.music.innertube.models.WatchEndpoint.WatchEndpointMusicSupportedConfigs.WatchEndpointMusicConfig.Companion.MUSIC_VIDEO_TYPE_ATV

sealed class YTItem {
    abstract val id: String
    abstract val title: String
    abstract val thumbnail: String?
    abstract val explicit: Boolean
    abstract val shareLink: String
}

data class Artist(
    val name: String,
    val id: String?,
)

data class Album(
    val name: String,
    val id: String,
)

data class SongItem(
    override val id: String,
    override val title: String,
    val artists: List<Artist>,
    val album: Album? = null,
    val duration: Int? = null,
    val musicVideoType: String? = null,
    val chartPosition: Int? = null,
    val chartChange: String? = null,
    override val thumbnail: String,
    override val explicit: Boolean = false,
    val endpoint: WatchEndpoint? = null,
    val setVideoId: String? = null,
    val libraryAddToken: String? = null,
    val libraryRemoveToken: String? = null,
    val historyRemoveToken: String? = null
) : YTItem() {
    val isVideoSong: Boolean
        get() = musicVideoType != null && musicVideoType != MUSIC_VIDEO_TYPE_ATV

    override val shareLink: String
        get() = "https://music.youtube.com/watch?v=$id"
}

data class AlbumItem(
    val browseId: String,
    val playlistId: String,
    override val id: String = browseId,
    override val title: String,
    val artists: List<Artist>?,
    val year: Int? = null,
    override val thumbnail: String,
    override val explicit: Boolean = false,
    val description: String? = null,
) : YTItem() {
    override val shareLink: String
        get() = "https://music.youtube.com/playlist?list=$playlistId"
}

data class PlaylistItem(
    override val id: String,
    override val title: String,
    val author: Artist?,
    val songCountText: String?,
    override val thumbnail: String?,
    val playEndpoint: WatchEndpoint?,
    val shuffleEndpoint: WatchEndpoint?,
    val radioEndpoint: WatchEndpoint?,
    val isEditable: Boolean = false,
) : YTItem() {
    override val explicit: Boolean
        get() = false
    override val shareLink: String
        get() = "https://music.youtube.com/playlist?list=$id"
}

data class ArtistItem(
    override val id: String,
    override val title: String,
    override val thumbnail: String?,
    val channelId: String? = null,
    val playEndpoint: WatchEndpoint? = null,
    val shuffleEndpoint: WatchEndpoint?,
    val radioEndpoint: WatchEndpoint?,
) : YTItem() {
    override val explicit: Boolean
        get() = false
    override val shareLink: String
        get() = "https://music.youtube.com/channel/$id"
}

fun <T : YTItem> List<T>.filterExplicit(enabled: Boolean = true) =
    if (enabled) {
        filter { !it.explicit }
    } else {
        this
    }

fun <T : YTItem> List<T>.filterVideoSongs(disableVideos: Boolean = false) =
    if (disableVideos) {
        filterNot { it is SongItem && it.isVideoSong }
    } else {
        this
    }

fun <T : YTItem> List<T>.filterYoutubeShorts(enabled: Boolean = false) =
    if (enabled) {
        filterNot { it is PlaylistItem && it.id.startsWith("SS") }
    } else {
        this
    }

fun sanitizeSongTitle(raw: String?): String {
    if (raw.isNullOrBlank()) return raw.orEmpty()
    var t = raw.trim()
    // 1. Remove YouTube video banners: " - Video Song", " - Official Video", " | Full Video Song", etc.
    t = t.replace(Regex("""(?i)\s*[-|–—]\s*(video\s*song|official\s*(music\s*)?video|full\s*video(\s*song)?|lyric(s|\s*video)?|lyrical(\s*video)?|promo(\s*video)?|audio\s*song)\b.*"""), "")
    // 2. Remove bracketed / parenthetical video tags: (Video Song), (Official Video), [Official Music Video], etc.
    t = t.replace(Regex("""(?i)\s*[\(\[]\s*(official\s*(music\s*)?video|video\s*song|full\s*video(\s*song)?|lyric(s|\s*video)?|lyrical(\s*video)?|4k\s*video|hd\s*video|visualizer|audio\s*song)\s*[\)\]]"""), "")
    // 3. Remove trailing artist / star credits after | if it was a video title
    if (t.contains("|")) {
        t = t.substringBefore("|").trim()
    }
    // 4. Remove trailing dash/hyphen
    t = t.replace(Regex("""\s*[-|–—]\s*$"""), "").trim()
    return if (t.isNotBlank()) t else raw
}
