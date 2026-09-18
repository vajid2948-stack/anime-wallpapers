package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.model.WallpaperCategory
import com.example.ui.components.AdMobBanner
import com.example.ui.components.InterstitialAdHelper
import com.example.ui.components.UpdateDialog
import com.example.ui.components.WallpaperDetailModal
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.theme.AnimeDarkSurface
import com.example.ui.theme.AnimeDarkSurfaceHighlight
import com.example.ui.theme.AnimeDarkVoid
import com.example.ui.theme.AnimeNeonCyan
import com.example.ui.theme.AnimeNeonPink
import com.example.ui.theme.AnimeNeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: WallpaperViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedTag by viewModel.selectedTag.collectAsState()
    val filteredWallpapers by viewModel.filteredWallpapers.collectAsState()
    val favoriteWallpapers by viewModel.favoriteWallpapers.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val selectedWallpaper by viewModel.selectedWallpaper.collectAsState()
    val applyStatus by viewModel.applyStatus.collectAsState()
    val downloadStatus by viewModel.downloadStatus.collectAsState()
    val updateInfo by viewModel.updateInfo.collectAsState()
    val isCheckingUpdate by viewModel.isCheckingUpdate.collectAsState()

    var isSearchActive by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // React to Apply status
    LaunchedEffect(applyStatus) {
        when (val status = applyStatus) {
            is ApplyStatus.Success -> {
                snackbarHostState.showSnackbar(
                    message = status.message,
                    duration = SnackbarDuration.Short
                )
                viewModel.clearApplyStatus()
            }
            is ApplyStatus.Error -> {
                snackbarHostState.showSnackbar(
                    message = "Error: ${status.error}",
                    duration = SnackbarDuration.Long
                )
                viewModel.clearApplyStatus()
            }
            else -> Unit
        }
    }

    // React to Download status
    LaunchedEffect(downloadStatus) {
        when (val status = downloadStatus) {
            is DownloadStatus.Success -> {
                snackbarHostState.showSnackbar(
                    message = status.message,
                    duration = SnackbarDuration.Short
                )
                viewModel.clearDownloadStatus()
            }
            is DownloadStatus.Error -> {
                snackbarHostState.showSnackbar(
                    message = "Download Failed: ${status.error}",
                    duration = SnackbarDuration.Long
                )
                viewModel.clearDownloadStatus()
            }
            else -> Unit
        }
    }

    Scaffold(
        containerColor = AnimeDarkVoid,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AnimeDarkSurface,
                    titleContentColor = TextPrimary
                ),
                title = {
                    if (isSearchActive) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { viewModel.onSearchQueryChange(it) },
                            placeholder = { Text("Search series, anime, tag...", color = TextSecondary, fontSize = 14.sp) },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                cursorColor = AnimeNeonPink
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("search_text_field")
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = AnimeNeonPink,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_app_icon),
                                        contentDescription = "Anime Logo",
                                        modifier = Modifier.size(34.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Anime Wallpapers",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "4K & Ultra HD Collection",
                                    fontSize = 11.sp,
                                    color = AnimeNeonCyan
                                )
                            }
                        }
                    }
                },
                actions = {
                    if (isSearchActive) {
                        IconButton(
                            onClick = {
                                if (searchQuery.isNotEmpty()) {
                                    viewModel.onSearchQueryChange("")
                                } else {
                                    isSearchActive = false
                                }
                            },
                            modifier = Modifier.testTag("clear_search_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Clear search",
                                tint = TextSecondary
                            )
                        }
                    } else {
                        IconButton(
                            onClick = {
                                viewModel.checkForUpdates(manual = true) { msg ->
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(msg)
                                    }
                                }
                            },
                            modifier = Modifier.testTag("check_update_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SystemUpdate,
                                contentDescription = "Check for Updates",
                                tint = AnimeNeonCyan
                            )
                        }

                        IconButton(
                            onClick = { isSearchActive = true },
                            modifier = Modifier.testTag("open_search_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search wallpapers",
                                tint = TextPrimary
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AnimeDarkSurface)
            ) {
                AdMobBanner(modifier = Modifier.fillMaxWidth())
                NavigationBar(
                    containerColor = AnimeDarkSurface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                // Explore Tab
                NavigationBarItem(
                    selected = currentTab == MainTab.EXPLORE,
                    onClick = { viewModel.onTabChange(MainTab.EXPLORE) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == MainTab.EXPLORE) Icons.Filled.Explore else Icons.Outlined.Explore,
                            contentDescription = "Explore"
                        )
                    },
                    label = { Text("Explore", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AnimeNeonPink,
                        selectedTextColor = AnimeNeonPink,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = AnimeDarkSurfaceHighlight
                    ),
                    modifier = Modifier.testTag("nav_explore")
                )

                // Categories Tab
                NavigationBarItem(
                    selected = currentTab == MainTab.CATEGORIES,
                    onClick = { viewModel.onTabChange(MainTab.CATEGORIES) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == MainTab.CATEGORIES) Icons.Filled.Category else Icons.Outlined.Category,
                            contentDescription = "Categories"
                        )
                    },
                    label = { Text("Categories", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AnimeNeonPink,
                        selectedTextColor = AnimeNeonPink,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = AnimeDarkSurfaceHighlight
                    ),
                    modifier = Modifier.testTag("nav_categories")
                )

                // Favorites Tab
                NavigationBarItem(
                    selected = currentTab == MainTab.FAVORITES,
                    onClick = { viewModel.onTabChange(MainTab.FAVORITES) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (favorites.isNotEmpty()) {
                                    Surface(
                                        shape = CircleShape,
                                        color = AnimeNeonPink,
                                        modifier = Modifier.size(16.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${favorites.size}",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (currentTab == MainTab.FAVORITES) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorites"
                            )
                        }
                    },
                    label = { Text("Favorites", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AnimeNeonPink,
                        selectedTextColor = AnimeNeonPink,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = AnimeDarkSurfaceHighlight
                    ),
                    modifier = Modifier.testTag("nav_favorites")
                )
            }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                MainTab.EXPLORE -> {
                    ExploreScreen(
                        wallpapers = filteredWallpapers,
                        featuredWallpapers = viewModel.featuredWallpapers,
                        favorites = favorites,
                        selectedCategory = selectedCategory,
                        selectedTag = selectedTag,
                        onCategorySelect = { viewModel.onCategorySelect(it) },
                        onTagSelect = { viewModel.onTagSelect(it) },
                        onWallpaperClick = { viewModel.selectWallpaper(it) },
                        onFavoriteToggle = { viewModel.toggleFavorite(it) }
                    )
                }
                MainTab.CATEGORIES -> {
                    CategoriesScreen(
                        allWallpapers = viewModel.allWallpapers,
                        onSelectCategory = { cat ->
                            viewModel.onCategorySelect(cat)
                            viewModel.onTabChange(MainTab.EXPLORE)
                        }
                    )
                }
                MainTab.FAVORITES -> {
                    FavoritesScreen(
                        favoriteWallpapers = favoriteWallpapers,
                        onWallpaperClick = { viewModel.selectWallpaper(it) },
                        onFavoriteToggle = { viewModel.toggleFavorite(it) },
                        onExploreClick = { viewModel.onTabChange(MainTab.EXPLORE) }
                    )
                }
            }

            // Wallpaper Detail & Action Dialog (Includes Download & Set Wallpaper)
            selectedWallpaper?.let { wp ->
                WallpaperDetailModal(
                    wallpaper = wp,
                    isFavorite = favorites.contains(wp.id),
                    applyStatus = applyStatus,
                    downloadStatus = downloadStatus,
                    onDismiss = { viewModel.selectWallpaper(null) },
                    onFavoriteToggle = { viewModel.toggleFavorite(wp.id) },
                    onDownload = {
                        InterstitialAdHelper.showAd(context) {
                            viewModel.downloadWallpaper(wp)
                        }
                    },
                    onApply = { target ->
                        InterstitialAdHelper.showAd(context) {
                            viewModel.applyWallpaper(wp, target)
                        }
                    },
                    onShare = { viewModel.shareWallpaper(wp) }
                )
            }

            // In-App Update Dialog
            updateInfo?.let { info ->
                if (info.hasUpdate) {
                    UpdateDialog(
                        updateInfo = info,
                        currentVersionName = viewModel.currentVersionName,
                        onUpdateClick = { viewModel.openUpdateDownload(context) },
                        onDismiss = { viewModel.dismissUpdateDialog() }
                    )
                }
            }
        }
    }
}
