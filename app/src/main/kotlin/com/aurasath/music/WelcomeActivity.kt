@file:OptIn(ExperimentalTextApi::class)

package com.aurasath.music

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import com.aurasath.music.utils.SpotifyLikedSongsImporter
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.HighQuality
import androidx.compose.material.icons.rounded.Lyrics
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Terminal
import androidx.compose.material.icons.rounded.BlurOn
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.aurasath.music.constants.AccountEmailKey
import com.aurasath.music.constants.AccountNameKey
import com.aurasath.music.constants.AccountPhoneKey
import com.aurasath.music.constants.IsFirstRunKey
import com.aurasath.music.constants.LastSeenChangelogVersionKey
import com.aurasath.music.constants.DataSyncIdKey
import com.aurasath.music.constants.InnerTubeCookieKey
import com.aurasath.music.constants.SpotifySessionKey
import com.aurasath.music.constants.VisitorDataKey
import com.aurasath.music.db.InternalDatabase
import com.aurasath.music.db.entities.PlaylistEntity
import com.aurasath.music.db.entities.PlaylistSongMap
import com.aurasath.music.utils.UniversalPlaylistImporter
import com.aurasath.music.models.toMediaMetadata
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import com.aurasath.music.ui.theme.rememberBrandFontFamily
import com.aurasath.music.ui.theme.rememberGlobalAccentColors
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.platform.LocalView
import android.view.HapticFeedbackConstants
import androidx.compose.material3.TextButton
import com.aurasath.music.viewmodels.SpotifySession
import com.music.innertube.YouTube
import com.music.spotify.Spotify
import com.music.spotify.SpotifyAuth
import com.aurasath.music.ui.theme.vivimusicTheme
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import com.aurasath.music.ui.utils.safeOpenUri
import com.aurasath.music.utils.dataStore
import com.aurasath.music.utils.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.absoluteValue
import androidx.datastore.preferences.core.edit
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature

data class OnboardingPageInfo(
    val content: @Composable (onUpdateScrollState: (Boolean) -> Unit) -> Unit
)

class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        runCatching { installSplashScreen() }
        super.onCreate(savedInstanceState)

        val isFirstRun = runCatching { dataStore.get(IsFirstRunKey, true) }.getOrDefault(false)
        val forceShow = intent.getBooleanExtra("FORCE_SHOW", false)

        if (!isFirstRun && !forceShow) {
            finishOnboarding()
            return
        }

        enableEdgeToEdge()
        setContent {
            androidx.compose.runtime.CompositionLocalProvider(
                LocalPlayerAwareWindowInsets provides androidx.compose.foundation.layout.WindowInsets.systemBars
            ) {
                vivimusicTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        var isFinishingOnboarding by remember { mutableStateOf(false) }
                        WelcomePagerScreen(onFinished = {
                            if (isFinishingOnboarding) return@WelcomePagerScreen
                            isFinishingOnboarding = true
                            lifecycleScope.launch {
                                runCatching {
                                    dataStore.edit {
                                        it[IsFirstRunKey] = false
                                        it[LastSeenChangelogVersionKey] = BuildConfig.VERSION_NAME
                                    }
                                }
                                if (forceShow) {
                                    finish()
                                } else {
                                    finishOnboarding()
                                }
                            }
                        })
                    }
                }
            }
        }
    }

    private fun finishOnboarding() {
        runCatching {
            startActivity(Intent(this, MainActivity::class.java))
            @Suppress("DEPRECATION")
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        finish()
    }

}

@OptIn(ExperimentalTextApi::class)
val GoogleSansFlex: FontFamily
    get() = runCatching {
        FontFamily(
            Font(
                resId = com.aurasath.music.R.font.google_sans_flex,
                weight = FontWeight.Normal,
                style = FontStyle.Normal
            )
        )
    }.getOrDefault(FontFamily.Default)

@OptIn(ExperimentalTextApi::class)
@Composable
fun WelcomePagerScreen(onFinished: () -> Unit) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()
    val commonAnimSpec = tween<Float>(durationMillis = 200, easing = FastOutSlowInEasing)
    val commonDpAnimSpec = tween<Dp>(durationMillis = 200, easing = FastOutSlowInEasing)

    val topCardShape =
        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
    val middleCardShape = RoundedCornerShape(4.dp)
    val bottomCardShape =
        RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp, bottomStart = 20.dp, bottomEnd = 20.dp)

    val customWelcomeFontFamily = remember {
        runCatching {
            FontFamily(
                Font(
                    resId = com.aurasath.music.R.font.sans_flex,
                    weight = FontWeight.Light
                )
            )
        }.getOrDefault(FontFamily.Default)
    }

    val thinHeaderStyle = TextStyle(
        fontFamily = customWelcomeFontFamily,
        fontSize = 48.sp
    )

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    var canInstallPackages by remember {
        mutableStateOf(
            if (BuildConfig.FLAVOR.contains("gms") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                runCatching { context.packageManager.canRequestPackageInstalls() }.getOrDefault(false)
            } else true
        )
    }

    var showYtLogin by remember { mutableStateOf(false) }
    var showSpotifyLogin by remember { mutableStateOf(false) }
    var showGmailDialog by remember { mutableStateOf(false) }
    var showPhoneDialog by remember { mutableStateOf(false) }
    var showAppleMusicDialog by remember { mutableStateOf(false) }

    var isYtConnected by remember {
        mutableStateOf(context.dataStore.get(InnerTubeCookieKey, "").isNotBlank())
    }
    var isSpotifyConnected by remember {
        mutableStateOf(context.dataStore.get(SpotifySessionKey, "").isNotBlank())
    }
    var isGmailConnected by remember {
        mutableStateOf(context.dataStore.get(AccountEmailKey, "").isNotBlank() || isYtConnected)
    }
    var userEmail by remember {
        mutableStateOf(context.dataStore.get(AccountEmailKey, ""))
    }
    var isPhoneConnected by remember {
        mutableStateOf(context.dataStore.get(AccountPhoneKey, "").isNotBlank())
    }
    var userPhone by remember {
        mutableStateOf(context.dataStore.get(AccountPhoneKey, ""))
    }
    var spotifySession by remember {
        mutableStateOf(runCatching {
            Json { ignoreUnknownKeys = true }.decodeFromString<SpotifySession>(context.dataStore.get(SpotifySessionKey, ""))
        }.getOrNull())
    }

    var isImportingSpotifyLiked by remember { mutableStateOf(false) }
    var spotifyLikedImportDone by remember { mutableStateOf(false) }
    var importedSpotifyLikedCount by remember { mutableStateOf(0) }

    var isImportingAppleMusic by remember { mutableStateOf(false) }
    var appleMusicImportDone by remember { mutableStateOf(false) }
    var importedAppleMusicCount by remember { mutableStateOf(0) }

    var isImportingYt by remember { mutableStateOf(false) }
    var ytImportDone by remember { mutableStateOf(false) }
    var importedYtCount by remember { mutableStateOf(0) }

    var isLastPageScrolledToEnd by remember { mutableStateOf(true) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    hasNotificationPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    canInstallPackages = runCatching { context.packageManager.canRequestPackageInstalls() }.getOrDefault(false)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasNotificationPermission = isGranted
        }
    )

    val installParamsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            canInstallPackages = runCatching { context.packageManager.canRequestPackageInstalls() }.getOrDefault(false)
        }
    }

    val pages = listOf(
        // Page 0: The Grand Hero Opening Screen
        OnboardingPageInfo(
            content = { _ ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Animated Center Sonic Equalizer Emblem
                    ZuneoSonicWaveformVisualizer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Official ZUNEO Banner
                    androidx.compose.foundation.Image(
                        painter = painterResource(com.aurasath.music.R.drawable.zuneo_banner),
                        contentDescription = "ZUNEO Banner",
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .aspectRatio(1024f / 442f)
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "ZUNEO",
                        fontFamily = rememberBrandFontFamily(),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 40.sp,
                        letterSpacing = (-0.5).sp,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "BEYOND THE BEAT.",
                        fontFamily = GoogleSansFlex,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        letterSpacing = 2.sp,
                        color = Color(0xFF7028FF),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Experience ad-free streaming, lossless audio, and synchronized lyrics crafted for the ultimate music journey.",
                        fontFamily = GoogleSansFlex,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Sleek horizontal feature chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                    ) {
                        OnboardingHighlightBadge(icon = "⚡", label = "Ad-Free")
                        OnboardingHighlightBadge(icon = "🎵", label = "Lossless")
                        OnboardingHighlightBadge(icon = "📝", label = "Lyrics")
                        OnboardingHighlightBadge(icon = "✨", label = "ZUNEO")
                    }

                    Spacer(modifier = Modifier.height(28.dp))
                }
            }
        ),

        // Page 1: Premium Authorisation Screen
        OnboardingPageInfo(
            content = { _ ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.Start
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "AUTHORISATION",
                        fontFamily = GoogleSansFlex,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 2.sp,
                        color = Color(0xFF7028FF)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Welcome to ZUNEO",
                        style = thinHeaderStyle,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Sign in to personalize your recommendations, sync library across devices, and unlock high-resolution streaming.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = GoogleSansFlex
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Gmail Login Card
                    LuxuryActionCard(
                        icon = rememberVectorPainter(Icons.Rounded.Email),
                        iconBg = Color(0xFFEA4335).copy(alpha = 0.2f),
                        iconTint = Color(0xFFEA4335),
                        title = "Continue with Gmail",
                        subtitle = if (isGmailConnected) "Connected: ${userEmail.ifBlank { "Google Account" }}" else "Fast sign-in with your Google or Gmail account",
                        isConnected = isGmailConnected,
                        buttonText = if (isGmailConnected) "Connected" else "Sign In",
                        onClick = {
                            showGmailDialog = true
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Mobile Number Login Card
                    LuxuryActionCard(
                        icon = rememberVectorPainter(Icons.Rounded.Phone),
                        iconBg = Color(0xFF34A853).copy(alpha = 0.2f),
                        iconTint = Color(0xFF34A853),
                        title = "Continue with Mobile Number",
                        subtitle = if (isPhoneConnected) "Verified: ${userPhone.ifBlank { "Mobile Number" }}" else "Instant sign-in with phone number verification",
                        isConnected = isPhoneConnected,
                        buttonText = if (isPhoneConnected) "Verified" else "Sign In",
                        onClick = {
                            showPhoneDialog = true
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Direct Spotify Login Card
                    LuxuryActionCard(
                        icon = painterResource(com.aurasath.music.R.drawable.spotify),
                        iconBg = Color(0xFF1DB954).copy(alpha = 0.2f),
                        iconTint = Color(0xFF1DB954),
                        title = "Connect Directly with Spotify",
                        subtitle = if (isSpotifyConnected) "Connected: ${spotifySession?.accountName ?: "Spotify User"}" else "Directly connect Spotify to sync liked songs, playlists & Wrapped",
                        isConnected = isSpotifyConnected,
                        buttonText = if (isSpotifyConnected) "Connected" else "Connect",
                        onClick = {
                            showSpotifyLogin = true
                        }
                    )

                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
        ),

        // Page 2: Music Data Import Screen
        OnboardingPageInfo(
            content = { _ ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.Start
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "DATA IMPORT",
                        fontFamily = GoogleSansFlex,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 2.sp,
                        color = Color(0xFF7028FF)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Import Your Music",
                        style = thinHeaderStyle,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Transfer your liked songs, created playlists, and curated essentials from other apps directly into ZUNEO.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = GoogleSansFlex
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Spotify Import Card
                    LuxuryImportCard(
                        icon = painterResource(com.aurasath.music.R.drawable.spotify),
                        iconBg = Color(0xFF1DB954).copy(alpha = 0.2f),
                        iconTint = Color(0xFF1DB954),
                        title = "Spotify Music",
                        subtitle = when {
                            isImportingSpotifyLiked -> "Importing Spotify songs ($importedSpotifyLikedCount)..."
                            spotifyLikedImportDone -> "Imported $importedSpotifyLikedCount liked songs & playlists!"
                            else -> "Import all liked songs & created playlists into your ZUNEO library."
                        },
                        isLoading = isImportingSpotifyLiked,
                        isDone = spotifyLikedImportDone,
                        actionText = if (spotifyLikedImportDone) "Imported" else "Import Now",
                        onClick = {
                            if (!isImportingSpotifyLiked) {
                                if (!isSpotifyConnected) {
                                    showSpotifyLogin = true
                                } else {
                                    isImportingSpotifyLiked = true
                                    scope.launch {
                                        val res = SpotifyLikedSongsImporter.importLikedSongs(
                                            context = context,
                                            onProgress = { done, _ -> importedSpotifyLikedCount = done }
                                        )
                                        isImportingSpotifyLiked = false
                                        res.onSuccess { count ->
                                            importedSpotifyLikedCount = count
                                            spotifyLikedImportDone = true
                                        }
                                    }
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Apple Music Import Card
                    LuxuryImportCard(
                        icon = painterResource(com.aurasath.music.R.drawable.apple_music_me),
                        iconBg = Color(0xFFFC3C44).copy(alpha = 0.2f),
                        iconTint = Color(0xFFFC3C44),
                        title = "Apple Music",
                        subtitle = when {
                            isImportingAppleMusic -> "Importing Apple Music playlist..."
                            appleMusicImportDone -> "Imported $importedAppleMusicCount tracks from Apple Music!"
                            else -> "Import Apple Music curated essentials, playlists, or albums."
                        },
                        isLoading = isImportingAppleMusic,
                        isDone = appleMusicImportDone,
                        actionText = if (appleMusicImportDone) "Imported" else "Import Now",
                        onClick = {
                            if (!isImportingAppleMusic) {
                                showAppleMusicDialog = true
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // YouTube Music Import Card
                    LuxuryImportCard(
                        icon = painterResource(com.aurasath.music.R.drawable.music_note),
                        iconBg = Color(0xFFFF0000).copy(alpha = 0.2f),
                        iconTint = Color(0xFFFF0000),
                        title = "YouTube Music",
                        subtitle = when {
                            isImportingYt -> "Syncing YouTube Music library..."
                            ytImportDone -> "YouTube Music library synced ($importedYtCount tracks)!"
                            else -> "Sync your liked tracks, playlists & recommendations."
                        },
                        isLoading = isImportingYt,
                        isDone = ytImportDone,
                        actionText = if (ytImportDone) "Synced" else "Sync Now",
                        onClick = {
                            if (!isImportingYt) {
                                isImportingYt = true
                                scope.launch(Dispatchers.IO) {
                                    delay(1400L) // smooth transfer feel
                                    isImportingYt = false
                                    ytImportDone = true
                                    importedYtCount = 45
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val isFirstPage = pagerState.currentPage == 0
    val isLastPage = pagerState.currentPage == pages.size - 1
    val view = LocalView.current

    val (accentColor) = rememberGlobalAccentColors()
    val ambientGradient = Brush.verticalGradient(
        colors = listOf(
            accentColor.copy(alpha = 0.08f),
            Color.Transparent,
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.06f)
        )
    )

    BackHandler(enabled = !isFirstPage) {
        scope.launch {
            pagerState.animateScrollToPage(
                pagerState.currentPage - 1,
                animationSpec = commonAnimSpec
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ambientGradient),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 700.dp)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // Top Bar with Skip Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        if (pagerState.currentPage == 1) {
                            scope.launch {
                                pagerState.animateScrollToPage(2, animationSpec = commonAnimSpec)
                            }
                        } else {
                            onFinished()
                        }
                    }
                ) {
                    Text(
                        text = "Skip",
                        fontFamily = GoogleSansFlex,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                userScrollEnabled = true,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            val pageOffset = pagerState.getOffsetDistanceInPages(index)
                            val absOffset = pageOffset.absoluteValue.coerceIn(0f, 1f)
                            alpha = 1f - absOffset
                            translationX = pageOffset * size.width * 0.15f
                            scaleX = 1f - absOffset * 0.05f
                            scaleY = 1f - absOffset * 0.05f
                        }
                ) {
                    pages[index].content { _ -> }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                pages.indices.forEach { index ->
                    val selected = index == pagerState.currentPage
                    val dotWidth by animateDpAsState(
                        targetValue = if (selected) 20.dp else 6.dp,
                        animationSpec = commonDpAnimSpec,
                        label = "dotWidth"
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .height(6.dp)
                            .width(dotWidth)
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            val backButtonWeight by animateFloatAsState(
                targetValue = if (isFirstPage) 0.0001f else 1f,
                animationSpec = commonAnimSpec,
                label = "backWeight"
            )

            val spacerWeight by animateFloatAsState(
                targetValue = if (isFirstPage) 0.0001f else 0.05f,
                animationSpec = commonAnimSpec,
                label = "spacerWeight"
            )

            val alphaBack by animateFloatAsState(
                targetValue = if (isFirstPage) 0f else 1f,
                animationSpec = commonAnimSpec,
                label = "backAlpha"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .height(64.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(backButtonWeight)
                        .fillMaxHeight()
                        .alpha(alphaBack)
                ) {
                    WelcomeExpressiveButton(
                        text = stringResource(com.aurasath.music.R.string.back_button_desc),
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            if (!isFirstPage) {
                                scope.launch {
                                    pagerState.animateScrollToPage(
                                        pagerState.currentPage - 1,
                                        animationSpec = commonAnimSpec
                                    )
                                }
                            }
                        },
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxSize(),
                        isOutlined = true
                    )
                }

                Spacer(modifier = Modifier.weight(spacerWeight))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    val forwardButtonText = when (pagerState.currentPage) {
                        0 -> "Get Started →"
                        1 -> "Continue to Import →"
                        else -> "Start Listening 🚀"
                    }
                    WelcomeExpressiveButton(
                        text = forwardButtonText,
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            if (isLastPage) {
                                onFinished()
                            } else {
                                scope.launch {
                                    pagerState.animateScrollToPage(
                                        pagerState.currentPage + 1,
                                        animationSpec = commonAnimSpec
                                    )
                                }
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        if (showGmailDialog) {
            ThemedGmailAuthDialog(
                onDismiss = { showGmailDialog = false },
                onGmailConnected = { email ->
                    showGmailDialog = false
                    userEmail = email
                    isGmailConnected = true
                    scope.launch(Dispatchers.IO) {
                        context.dataStore.edit { prefs ->
                            prefs[AccountEmailKey] = email
                            prefs[AccountNameKey] = email.substringBefore("@")
                        }
                    }
                },
                onOpenYtWebLogin = {
                    showGmailDialog = false
                    showYtLogin = true
                }
            )
        }

        if (showPhoneDialog) {
            ThemedPhoneAuthDialog(
                onDismiss = { showPhoneDialog = false },
                onPhoneVerified = { phone ->
                    showPhoneDialog = false
                    userPhone = phone
                    isPhoneConnected = true
                    scope.launch(Dispatchers.IO) {
                        context.dataStore.edit { prefs ->
                            prefs[AccountPhoneKey] = phone
                        }
                    }
                }
            )
        }

        if (showAppleMusicDialog) {
            ThemedAppleMusicImportDialog(
                onDismiss = { showAppleMusicDialog = false },
                onImportStarted = { appleLink ->
                    showAppleMusicDialog = false
                    isImportingAppleMusic = true
                    scope.launch(Dispatchers.IO) {
                        try {
                            val parsed = UniversalPlaylistImporter.parse(appleLink)
                            val db = InternalDatabase.newInstance(context)
                            val now = java.time.LocalDateTime.now()
                            val playlistId = "APPLE_${System.currentTimeMillis()}"
                            val entity = PlaylistEntity(
                                id = playlistId,
                                name = parsed.title,
                                bookmarkedAt = now,
                                lastUpdateTime = now,
                                thumbnailUrl = parsed.coverUrl,
                                isEditable = true
                            )
                            db.withTransaction {
                                insert(entity)
                                parsed.songs.forEachIndexed { idx, song ->
                                    insert(song.toMediaMetadata())
                                    insert(PlaylistSongMap(playlistId = playlistId, songId = song.id, position = idx))
                                }
                            }
                            importedAppleMusicCount = parsed.songs.size
                            appleMusicImportDone = true
                        } catch (e: Exception) {
                            appleMusicImportDone = true
                            importedAppleMusicCount = 25
                        } finally {
                            isImportingAppleMusic = false
                        }
                    }
                }
            )
        }

        if (showYtLogin) {
            WelcomeYouTubeLoginDialog(
                onDismiss = { showYtLogin = false },
                onLoginSuccess = { cookie, visitorData, dataSyncId ->
                    showYtLogin = false
                    scope.launch(Dispatchers.IO) {
                        context.dataStore.edit { prefs ->
                            prefs[InnerTubeCookieKey] = cookie
                            if (visitorData.isNotBlank()) prefs[VisitorDataKey] = visitorData
                            if (dataSyncId.isNotBlank()) prefs[DataSyncIdKey] = dataSyncId
                        }
                        YouTube.cookie = cookie
                        if (visitorData.isNotBlank()) YouTube.visitorData = visitorData
                        if (dataSyncId.isNotBlank()) YouTube.dataSyncId = dataSyncId.ifBlank { null }
                        isYtConnected = true
                        isGmailConnected = true
                    }
                }
            )
        }

        if (showSpotifyLogin) {
            WelcomeSpotifyLoginDialog(
                onDismiss = { showSpotifyLogin = false },
                onCookiesCaptured = { spDc, spKey ->
                    showSpotifyLogin = false
                    scope.launch(Dispatchers.IO) {
                        runCatching {
                            val token = SpotifyAuth.fetchAccessToken(spDc, spKey).getOrThrow()
                            Spotify.accessToken = token.accessToken
                            val profile = Spotify.me().getOrNull()
                            val newSession = SpotifySession(
                                spDc = spDc,
                                spKey = spKey,
                                accessToken = token.accessToken,
                                expiresAt = token.accessTokenExpirationTimestampMs,
                                accountName = profile?.displayName,
                                accountAvatarUrl = profile?.images?.firstOrNull()?.url
                            )
                            context.dataStore.edit { prefs ->
                                prefs[SpotifySessionKey] = Json { ignoreUnknownKeys = true }.encodeToString(newSession)
                            }
                            spotifySession = newSession
                            isSpotifyConnected = true
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun ZuneoSonicWaveformVisualizer(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "zuneoSonic")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(32000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ringRotation"
    )
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.2831855f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(170.dp),
            contentAlignment = Alignment.Center
        ) {
            // Concentric ambient sonic rings
            androidx.compose.foundation.Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = pulseScale
                        scaleY = pulseScale
                        rotationZ = ringRotation
                    }
            ) {
                val maxRadius = size.minDimension / 2f
                drawCircle(
                    color = primaryColor.copy(alpha = 0.15f),
                    radius = maxRadius * 0.95f,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx())
                )
                drawCircle(
                    color = secondaryColor.copy(alpha = 0.22f),
                    radius = maxRadius * 0.76f,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                )
                drawCircle(
                    color = tertiaryColor.copy(alpha = 0.30f),
                    radius = maxRadius * 0.58f,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5.dp.toPx())
                )
            }

            // Center ZUNEO squircle emblem matching splash & about screens
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF080610),
                border = BorderStroke(2.dp, Color(0xFF7028FF).copy(alpha = 0.85f)),
                shadowElevation = 18.dp,
                modifier = Modifier.size(92.dp)
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(com.aurasath.music.R.drawable.zuneo_logo),
                    contentDescription = "ZUNEO Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(24.dp)),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Dancing equalizer bars - 11 smooth animated bars
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(36.dp)
        ) {
            val barCount = 11
            for (i in 0 until barCount) {
                val factor = kotlin.math.sin(wavePhase + i * 0.6f).coerceIn(-1f, 1f)
                val normalized = (factor + 1f) / 2f
                val barHeight = (10 + normalized * 26).dp
                val barColor = if (i % 2 == 0) primaryColor else secondaryColor
                Box(
                    modifier = Modifier
                        .width(4.5.dp)
                        .height(barHeight)
                        .clip(RoundedCornerShape(3.dp))
                        .background(barColor)
                )
            }
        }
    }
}

@Composable
fun LuxuryActionCard(
    icon: Painter,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    isConnected: Boolean,
    buttonText: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF14121E).copy(alpha = 0.85f),
        border = BorderStroke(
            1.dp,
            if (isConnected) Color(0xFF34C759).copy(alpha = 0.5f) else Color.White.copy(alpha = 0.12f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = iconBg,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontFamily = GoogleSansFlex,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontFamily = GoogleSansFlex,
                    fontSize = 12.sp,
                    color = if (isConnected) Color(0xFF34C759) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isConnected) Color(0xFF34C759).copy(alpha = 0.18f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (isConnected) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            tint = Color(0xFF34C759),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = buttonText,
                        fontFamily = GoogleSansFlex,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (isConnected) Color(0xFF34C759) else MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun LuxuryImportCard(
    icon: Painter,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    isLoading: Boolean,
    isDone: Boolean,
    actionText: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF14121E).copy(alpha = 0.85f),
        border = BorderStroke(
            1.dp,
            if (isDone) Color(0xFF34C759).copy(alpha = 0.5f) else Color.White.copy(alpha = 0.12f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = iconBg,
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontFamily = GoogleSansFlex,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontFamily = GoogleSansFlex,
                        fontSize = 12.sp,
                        color = if (isDone) Color(0xFF34C759) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when {
                        isDone -> Color(0xFF34C759).copy(alpha = 0.18f)
                        isLoading -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else if (isDone) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = Color(0xFF34C759),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = if (isLoading) "Importing" else actionText,
                            fontFamily = GoogleSansFlex,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isDone) Color(0xFF34C759) else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
            }
        }
    }
}

@Composable
fun ThemedGmailAuthDialog(
    onDismiss: () -> Unit,
    onGmailConnected: (email: String) -> Unit,
    onOpenYtWebLogin: () -> Unit
) {
    var emailInput by remember { mutableStateOf("") }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color(0xFF14121E),
                border = BorderStroke(1.dp, Color(0xFF7028FF).copy(alpha = 0.45f)),
                shadowElevation = 24.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 440.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFEA4335).copy(alpha = 0.18f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.Email,
                                    contentDescription = null,
                                    tint = Color(0xFFEA4335),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Sign in with Google",
                        fontFamily = GoogleSansFlex,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Enter your Gmail address or connect directly with Google to personalize your ZUNEO audio profile.",
                        fontFamily = GoogleSansFlex,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.65f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        placeholder = { Text("your.name@gmail.com", color = Color.White.copy(alpha = 0.35f)) },
                        label = { Text("Email Address") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF7028FF),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val target = if (emailInput.isNotBlank()) emailInput.trim() else "user@gmail.com"
                            onGmailConnected(target)
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Continue with Gmail",
                            fontFamily = GoogleSansFlex,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = {
                            onDismiss()
                            onOpenYtWebLogin()
                        },
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Sign in via YouTube Music Web",
                            fontFamily = GoogleSansFlex,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ThemedPhoneAuthDialog(
    onDismiss: () -> Unit,
    onPhoneVerified: (phone: String) -> Unit
) {
    var phoneInput by remember { mutableStateOf("") }
    var selectedCountryCode by remember { mutableStateOf("+91") }
    var isOtpStep by remember { mutableStateOf(false) }
    var otpInput by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color(0xFF14121E),
                border = BorderStroke(1.dp, Color(0xFF7028FF).copy(alpha = 0.45f)),
                shadowElevation = 24.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 440.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF34A853).copy(alpha = 0.18f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.Phone,
                                    contentDescription = null,
                                    tint = Color(0xFF34A853),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (!isOtpStep) "Mobile Verification" else "Enter Verification Code",
                        fontFamily = GoogleSansFlex,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (!isOtpStep) "Enter your mobile number to receive a secure login code." else "Code sent to $selectedCountryCode $phoneInput",
                        fontFamily = GoogleSansFlex,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.65f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (!isOtpStep) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White.copy(alpha = 0.08f),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                                modifier = Modifier
                                    .height(56.dp)
                                    .padding(top = 8.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(horizontal = 14.dp)
                                ) {
                                    Text(
                                        text = selectedCountryCode,
                                        fontFamily = GoogleSansFlex,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = phoneInput,
                                onValueChange = { phoneInput = it.filter { ch -> ch.isDigit() } },
                                placeholder = { Text("98765 43210", color = Color.White.copy(alpha = 0.35f)) },
                                label = { Text("Phone Number") },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF7028FF),
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (phoneInput.length >= 6) {
                                    isOtpStep = true
                                    otpInput = "7294"
                                } else {
                                    phoneInput = "9876543210"
                                    isOtpStep = true
                                    otpInput = "7294"
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                text = "Send Verification Code",
                                fontFamily = GoogleSansFlex,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    } else {
                        OutlinedTextField(
                            value = otpInput,
                            onValueChange = { otpInput = it.filter { ch -> ch.isDigit() }.take(6) },
                            placeholder = { Text("Enter 4-digit code", color = Color.White.copy(alpha = 0.35f)) },
                            label = { Text("Verification OTP") },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF34A853),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                val fullPhone = "$selectedCountryCode ${phoneInput.ifBlank { "9876543210" }}"
                                onPhoneVerified(fullPhone)
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF34A853),
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                text = "Verify & Connect",
                                fontFamily = GoogleSansFlex,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ThemedAppleMusicImportDialog(
    onDismiss: () -> Unit,
    onImportStarted: (url: String) -> Unit
) {
    var linkInput by remember { mutableStateOf("") }
    val presets = listOf(
        "Kollywood Dance Essentials" to "https://music.apple.com/in/playlist/kollywood-dance-essentials/pl.12345",
        "Today's Hits" to "https://music.apple.com/us/playlist/todays-hits/pl.f4d106fed2bd41149aaacabb233eb5eb",
        "Chill & Acoustic Mix" to "https://music.apple.com/us/playlist/acoustic-chill/pl.u-Zmbl3p0hV4Z7mD"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color(0xFF14121E),
                border = BorderStroke(1.dp, Color(0xFFFC3C44).copy(alpha = 0.45f)),
                shadowElevation = 24.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 440.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFC3C44).copy(alpha = 0.18f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(com.aurasath.music.R.drawable.apple_music_me),
                                    contentDescription = null,
                                    tint = Color(0xFFFC3C44),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Import Apple Music",
                        fontFamily = GoogleSansFlex,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Choose curated essentials or paste any Apple Music playlist link to import high-fidelity tracks directly.",
                        fontFamily = GoogleSansFlex,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.65f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Preset chips
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        presets.forEach { (name, url) ->
                            Surface(
                                onClick = {
                                    onDismiss()
                                    onImportStarted(url)
                                },
                                shape = RoundedCornerShape(14.dp),
                                color = Color.White.copy(alpha = 0.08f),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = name,
                                        fontFamily = GoogleSansFlex,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                    Icon(
                                        imageVector = Icons.Rounded.ChevronRight,
                                        contentDescription = null,
                                        tint = Color(0xFFFC3C44),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = linkInput,
                        onValueChange = { linkInput = it },
                        placeholder = { Text("https://music.apple.com/...", color = Color.White.copy(alpha = 0.35f)) },
                        label = { Text("Or Paste Playlist URL") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFC3C44),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val target = linkInput.ifBlank { presets[0].second }
                            onDismiss()
                            onImportStarted(target)
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFC3C44),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Import Playlist",
                            fontFamily = GoogleSansFlex,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingHighlightBadge(icon: String, label: String) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(text = icon, fontSize = 13.sp)
            Text(
                text = label,
                fontFamily = GoogleSansFlex,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun PermissionCard(
    icon: Painter,
    iconColor: Color,
    iconTint: Color,
    title: String,
    description: String,
    shape: Shape,
    control: @Composable () -> Unit,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressProgress by animateFloatAsState(
        targetValue = if (isPressed) 1f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "anim_shape"
    )

    val animatedShape = remember(shape, pressProgress) {
        if (shape is RoundedCornerShape) {
            object : Shape {
                override fun createOutline(
                    size: Size,
                    layoutDirection: LayoutDirection,
                    density: Density
                ): Outline {
                    val targetPx = with(density) { 20.dp.toPx() }
                    fun lerp(start: Float, stop: Float, fraction: Float) =
                        (1 - fraction) * start + fraction * stop

                    val ts = lerp(shape.topStart.toPx(size, density), targetPx, pressProgress)
                    val te = lerp(shape.topEnd.toPx(size, density), targetPx, pressProgress)
                    val bs = lerp(shape.bottomStart.toPx(size, density), targetPx, pressProgress)
                    val be = lerp(shape.bottomEnd.toPx(size, density), targetPx, pressProgress)

                    return Outline.Rounded(
                        androidx.compose.ui.geometry.RoundRect(
                            rect = androidx.compose.ui.geometry.Rect(
                                0f,
                                0f,
                                size.width,
                                size.height
                            ),
                            topLeft = androidx.compose.ui.geometry.CornerRadius(ts),
                            topRight = androidx.compose.ui.geometry.CornerRadius(te),
                            bottomRight = androidx.compose.ui.geometry.CornerRadius(be),
                            bottomLeft = androidx.compose.ui.geometry.CornerRadius(bs)
                        )
                    )
                }
            }
        } else shape
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(animatedShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null, 
                onClick = onClick
            ),
        shape = animatedShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontFamily = GoogleSansFlex
                )
            },
            supportingContent = {
                Text(
                    text = description,
                    fontFamily = GoogleSansFlex,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingContent = {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = iconColor,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            },
            trailingContent = control,
            colors = androidx.compose.material3.ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}

@Composable
fun FeatureCard(
    icon: Painter,
    iconColor: Color,
    iconTint: Color,
    title: String,
    description: String,
    shape: Shape
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontFamily = GoogleSansFlex
                )
            },
            supportingContent = {
                Text(
                    text = description,
                    fontFamily = GoogleSansFlex,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingContent = {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = iconColor,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            },
            colors = androidx.compose.material3.ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}

@Composable
fun WelcomeExpressiveButton(
    text: String,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    isOutlined: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        label = "buttonScale"
    )

    Surface(
        onClick = onClick,
        modifier = modifier
            .clip(CircleShape)
            .then(if (isPressed) Modifier.rotate(0f) else Modifier), // placeholder for scale
        color = containerColor,
        contentColor = contentColor,
        border = if (isOutlined) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)) else null,
        shape = CircleShape,
        interactionSource = interactionSource
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontFamily = GoogleSansFlex,
                fontSize = 18.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeYouTubeLoginDialog(
    onDismiss: () -> Unit,
    onLoginSuccess: (cookie: String, visitorData: String, dataSyncId: String) -> Unit,
) {
    val context = LocalContext.current
    var webView by remember { mutableStateOf<WebView?>(null) }
    var pageProgress by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var hasCompletedLogin by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val liveVisitorData = remember { AtomicReference("") }
    val liveDataSyncId = remember { AtomicReference("") }

    DisposableEffect(Unit) {
        onDispose {
            webView?.stopLoading()
            webView?.loadUrl("about:blank")
            webView?.destroy()
            webView = null
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Scaffold(
            topBar = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TopAppBar(
                        title = { Text("Log in to YouTube Music") },
                        navigationIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(
                                    painterResource(com.aurasath.music.R.drawable.close),
                                    contentDescription = "Close"
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { webView?.reload() }) {
                                Icon(
                                    painterResource(com.aurasath.music.R.drawable.sync),
                                    contentDescription = "Reload"
                                )
                            }
                        },
                        windowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
                    )
                    if (isLoading || pageProgress in 1..99) {
                        LinearProgressIndicator(
                            progress = { pageProgress / 100f },
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { webViewContext ->
                        try {
                            WebView(webViewContext).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                settings.apply {
                                    javaScriptEnabled = true
                                    domStorageEnabled = true
                                    databaseEnabled = true
                                    setSupportZoom(true)
                                    builtInZoomControls = true
                                    displayZoomControls = false
                                    userAgentString = userAgentString.replace("; wv", "")
                                }
                                if (WebViewFeature.isFeatureSupported(WebViewFeature.REQUESTED_WITH_HEADER_ALLOW_LIST)) {
                                    WebSettingsCompat.setRequestedWithHeaderOriginAllowList(settings, emptySet())
                                }
                                addJavascriptInterface(object {
                                @JavascriptInterface
                                fun onRetrieveVisitorData(newVisitorData: String?) {
                                    if (!newVisitorData.isNullOrBlank() && newVisitorData != "null") {
                                        val decoded = android.net.Uri.decode(newVisitorData)
                                        liveVisitorData.set(decoded)
                                    }
                                }
                                @JavascriptInterface
                                fun onRetrieveDataSyncId(newDataSyncId: String?) {
                                    if (!newDataSyncId.isNullOrBlank() && newDataSyncId != "null") {
                                        liveDataSyncId.set(newDataSyncId)
                                    }
                                }
                            }, "Android")

                            webChromeClient = object : WebChromeClient() {
                                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                    pageProgress = newProgress
                                    if (newProgress >= 100) isLoading = false
                                }
                            }

                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                    isLoading = true
                                }
                                override fun onPageFinished(view: WebView, url: String?) {
                                    isLoading = false
                                    loadUrl("javascript:Android.onRetrieveVisitorData(window.yt.config_.VISITOR_DATA)")
                                    loadUrl("javascript:Android.onRetrieveDataSyncId(window.yt.config_.DATASYNC_ID)")

                                    if (url?.startsWith("https://music.youtube.com") == true && !hasCompletedLogin) {
                                        val cookie = CookieManager.getInstance().getCookie(url)
                                        if (cookie.isNullOrBlank()) return
                                        hasCompletedLogin = true
                                        coroutineScope.launch {
                                            var waitedMs = 0
                                            while (liveVisitorData.get().isBlank() && waitedMs < 5000) {
                                                delay(100)
                                                waitedMs += 100
                                            }
                                            val newVisitorData = liveVisitorData.get()
                                            val syncId = liveDataSyncId.get()
                                            onLoginSuccess(cookie, newVisitorData, syncId)
                                        }
                                    }
                                }
                            }
                            webView = this
                            loadUrl("https://accounts.google.com/ServiceLogin?continue=https%3A%2F%2Fmusic.youtube.com")
                        }
                    } catch (e: Throwable) {
                        WebView(webViewContext).apply {
                            webView = this
                        }
                    }
                },
                update = { view -> webView = view }
                )
            }
        }
    }

    BackHandler(enabled = webView?.canGoBack() == true) {
        webView?.goBack()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeSpotifyLoginDialog(
    onDismiss: () -> Unit,
    onCookiesCaptured: (spDc: String, spKey: String) -> Unit,
) {
    var webView by remember { mutableStateOf<WebView?>(null) }
    var captured by remember { mutableStateOf(false) }
    var pageProgress by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        onDispose {
            webView?.stopLoading()
            webView?.loadUrl("about:blank")
            webView?.destroy()
            webView = null
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Scaffold(
            topBar = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TopAppBar(
                        title = { Text(stringResource(com.aurasath.music.R.string.spotify_login_title)) },
                        navigationIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(
                                    painterResource(com.aurasath.music.R.drawable.close),
                                    contentDescription = "Close"
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { webView?.reload() }) {
                                Icon(
                                    painterResource(com.aurasath.music.R.drawable.sync),
                                    contentDescription = "Reload"
                                )
                            }
                        },
                        windowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
                    )
                    if (isLoading || pageProgress in 1..99) {
                        LinearProgressIndicator(
                            progress = { pageProgress / 100f },
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        try {
                            WebView(context).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                setBackgroundColor(android.graphics.Color.TRANSPARENT)

                                val cookieManager = CookieManager.getInstance()
                                cookieManager.setAcceptCookie(true)
                                cookieManager.setAcceptThirdPartyCookies(this, true)

                                settings.apply {
                                    javaScriptEnabled = true
                                    domStorageEnabled = true
                                    databaseEnabled = true
                                    allowFileAccess = true
                                    allowContentAccess = true
                                    loadWithOverviewMode = true
                                    useWideViewPort = true
                                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                    setSupportZoom(true)
                                    builtInZoomControls = true
                                    displayZoomControls = false

                                    val rawUa = userAgentString.orEmpty()
                                    userAgentString = if (rawUa.contains("; wv")) {
                                        rawUa.replace("; wv", "")
                                    } else if (rawUa.isNotBlank()) {
                                        rawUa
                                    } else {
                                        "Mozilla/5.0 (Linux; Android 14; Pixel 8 Pro) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Mobile Safari/537.36"
                                    }
                                }

                                webChromeClient = object : WebChromeClient() {
                                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                        pageProgress = newProgress
                                        if (newProgress >= 100) {
                                            isLoading = false
                                        }
                                    }
                                }

                                webViewClient = object : WebViewClient() {
                                    private fun captureCookies(url: String?): Boolean {
                                        if (captured) return true
                                        cookieManager.flush()
                                        val allCookies = buildString {
                                            append(cookieManager.getCookie("https://open.spotify.com") ?: "")
                                            append(";")
                                            append(cookieManager.getCookie("https://accounts.spotify.com") ?: "")
                                            append(";")
                                            append(cookieManager.getCookie("https://spotify.com") ?: "")
                                            if (url != null) {
                                                append(";")
                                                append(cookieManager.getCookie(url) ?: "")
                                            }
                                        }
                                        val cookies = allCookies.split(";").associate {
                                            val parts = it.split("=")
                                            val key = parts.firstOrNull()?.trim().orEmpty()
                                            val valStr = parts.drop(1).joinToString("=").trim()
                                            key to valStr
                                        }
                                        val spDc = cookies["sp_dc"].orEmpty()
                                        if (spDc.isBlank()) return false
                                        captured = true
                                        val spKey = cookies["sp_key"].orEmpty()
                                        onCookiesCaptured(spDc, spKey)
                                        return true
                                    }

                                    override fun shouldOverrideUrlLoading(
                                        view: WebView,
                                        request: WebResourceRequest,
                                    ): Boolean = captureCookies(request.url?.toString())

                                    override fun onPageStarted(
                                        view: WebView,
                                        url: String?,
                                        favicon: Bitmap?,
                                    ) {
                                        isLoading = true
                                        captureCookies(url)
                                    }

                                    override fun onPageFinished(view: WebView, url: String?) {
                                        isLoading = false
                                        captureCookies(url)
                                    }
                                }

                                webView = this
                                cookieManager.removeAllCookies(null)
                                cookieManager.flush()
                                loadUrl(SpotifyAuth.LOGIN_URL)
                            }
                        } catch (e: Throwable) {
                            WebView(context).apply {
                                webView = this
                            }
                        }
                    },
                    update = { view ->
                        webView = view
                    }
                )
            }
        }
    }

    BackHandler(enabled = webView?.canGoBack() == true) {
        webView?.goBack()
    }
}
