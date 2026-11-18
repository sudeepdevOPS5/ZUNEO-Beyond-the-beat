/**
 * ZUNEO Music Flagship (C) 2026
 * Aquamorphic Lossless Bento Hub Component
 */

package com.aurasath.music.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aurasath.music.R
import com.aurasath.music.ui.theme.AppleTokens
import com.aurasath.music.ui.utils.combinedBounceClick

@Composable
fun LibraryBentoHeroRow(
    likedCount: Int,
    downloadCount: Int,
    onLikedClick: () -> Unit,
    onLikedLongClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onDownloadLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AppleTokens.Gutter),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Liked Songs Bento Hero
        Box(
            modifier = Modifier
                .weight(1f)
                .height(118.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF8A2BE2),
                            Color(0xFFFF007A)
                        )
                    )
                )
                .border(
                    BorderStroke(1.2.dp, Color.White.copy(alpha = 0.28f)),
                    RoundedCornerShape(20.dp)
                )
                .combinedBounceClick(
                    onClick = onLikedClick,
                    onLongClick = onLikedLongClick
                )
        ) {
            // Specular radial reflection
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.24f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White.copy(alpha = 0.22f), CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.38f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.favorite),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(19.dp),
                    )
                }

                Column {
                    Text(
                        text = stringResource(R.string.liked),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = pluralStringResource(R.plurals.n_song, likedCount, likedCount),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.85f),
                        maxLines = 1,
                    )
                }
            }
        }

        // Downloaded / Offline Bento Hero
        Box(
            modifier = Modifier
                .weight(1f)
                .height(118.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0072FF),
                            Color(0xFF00C9A7)
                        )
                    )
                )
                .border(
                    BorderStroke(1.2.dp, Color.White.copy(alpha = 0.28f)),
                    RoundedCornerShape(20.dp)
                )
                .combinedBounceClick(
                    onClick = onDownloadClick,
                    onLongClick = onDownloadLongClick
                )
        ) {
            // Specular radial reflection
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.24f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White.copy(alpha = 0.22f), CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.38f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.download),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(19.dp),
                    )
                }

                Column {
                    Text(
                        text = stringResource(R.string.offline),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = if (downloadCount > 0) "$downloadCount songs • Ready" else "Ready for offline",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.85f),
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Composable
fun LibrarySmartHubCard(
    title: String,
    subtitle: String,
    @DrawableRes icon: Int,
    accentColor: Color,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(78.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF131A26).copy(alpha = 0.82f))
            .border(
                BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                RoundedCornerShape(18.dp)
            )
            .combinedBounceClick(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.16f))
                    .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp),
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = AppleTokens.Metadata,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}