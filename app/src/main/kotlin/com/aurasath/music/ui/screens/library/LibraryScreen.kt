/**
 * ZUNEO Music Flagship (C) 2026
 * Licensed under GPL-3.0
 */

package com.aurasath.music.ui.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.aurasath.music.ui.theme.LocalAccentColor
import com.aurasath.music.R
import com.aurasath.music.constants.ChipSortTypeKey
import com.aurasath.music.constants.LibraryFilter
import com.aurasath.music.constants.PureBlackHeroBackgroundKey
import com.aurasath.music.ui.component.ChipsRow
import com.aurasath.music.ui.component.HomeImageBackground
import com.aurasath.music.ui.component.hasCustomHomeBackground
import com.aurasath.music.utils.rememberEnumPreference
import com.aurasath.music.utils.rememberPreference

@Composable
fun LibraryScreen(navController: NavController) {
    var filterType by rememberEnumPreference(ChipSortTypeKey, LibraryFilter.LIBRARY)
    val (pureBlack) = rememberPreference(PureBlackHeroBackgroundKey, false)
    val accentColor = LocalAccentColor.current

    val filterContent = @Composable {
        Row {
            ChipsRow(
                chips =
                listOf(
                    LibraryFilter.PLAYLISTS to stringResource(R.string.filter_playlists),
                    LibraryFilter.SONGS to stringResource(R.string.filter_songs),
                    LibraryFilter.ALBUMS to stringResource(R.string.filter_albums),
                ),
                currentValue = filterType,
                onValueUpdate = {
                    filterType =
                        if (filterType == it) {
                            LibraryFilter.LIBRARY
                        } else {
                            it
                        }
                },
                modifier = Modifier.weight(1f),
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (pureBlack) Color.Black else Color(0xFF080C14)),
    ) {
        if (hasCustomHomeBackground()) {
            HomeImageBackground(withGradient = true)
        } else if (!pureBlack) {
            // Aquamorphic Aurora Mesh Background for Library (eliminates muddy olive dynamic tints)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                accentColor.copy(alpha = 0.22f),
                                accentColor.copy(alpha = 0.06f),
                                Color.Transparent
                            ),
                            center = Offset(120f, 120f),
                            radius = 1200f
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF3B82F6).copy(alpha = 0.16f),
                                Color.Transparent
                            ),
                            center = Offset(900f, 420f),
                            radius = 950f
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0.0f to Color.Transparent,
                            0.35f to Color(0xFF080C14).copy(alpha = 0.45f),
                            0.70f to Color(0xFF080C14).copy(alpha = 0.90f),
                            1.0f to Color(0xFF080C14)
                        )
                    )
            )
        }

        when (filterType) {
            LibraryFilter.LIBRARY -> LibraryMixScreen(navController, filterContent)
            LibraryFilter.PLAYLISTS -> LibraryPlaylistsScreen(navController, filterContent)
            LibraryFilter.SONGS -> LibrarySongsScreen(
                navController,
                { filterType = LibraryFilter.LIBRARY })

            LibraryFilter.ALBUMS -> LibraryAlbumsScreen(
                navController,
                { filterType = LibraryFilter.LIBRARY })

            LibraryFilter.ARTISTS -> LibraryMixScreen(navController, filterContent)
        }
    }
}