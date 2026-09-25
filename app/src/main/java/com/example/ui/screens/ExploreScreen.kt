package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fireplace
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AnimeWallpaper
import com.example.model.WallpaperCategory
import com.example.ui.components.WallpaperCard
import com.example.ui.theme.AnimeNeonCyan
import com.example.ui.theme.AnimeNeonPink
import com.example.ui.theme.AnimeNeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ExploreScreen(
    wallpapers: List<AnimeWallpaper>,
    featuredWallpapers: List<AnimeWallpaper>,
    favorites: Set<String>,
    selectedCategory: WallpaperCategory,
    selectedTag: String,
    onCategorySelect: (WallpaperCategory) -> Unit,
    onTagSelect: (String) -> Unit,
    onWallpaperClick: (AnimeWallpaper) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val quickTags = listOf(
        "All",
        "Naruto",
        "Gojo",
        "One Piece",
        "Demon Slayer",
        "AOT",
        "Dragon Ball",
        "Solo Leveling",
        "Bleach",
        "4K UHD",
        "AMOLED"
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = modifier
            .fillMaxSize()
            .testTag("explore_wallpapers_grid")
    ) {
        // Featured Ultra HD Carousel Header
        if (selectedCategory == WallpaperCategory.ALL && selectedTag == "All") {
            item(span = { GridItemSpan(2) }) {
                Column(modifier = Modifier.padding(bottom = 6.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = AnimeNeonPink.copy(alpha = 0.2f),
                                modifier = Modifier.size(26.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("🔥", fontSize = 14.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Featured 4K Anime Art",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AnimeNeonCyan.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Ultra HD",
                                color = AnimeNeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(featuredWallpapers, key = { it.id }) { item ->
                            FeaturedHeroCard(
                                wallpaper = item,
                                onClick = { onWallpaperClick(item) }
                            )
                        }
                    }
                }
            }
        }

        // Category Filter Tabs
        item(span = { GridItemSpan(2) }) {
            Column(modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)) {
                Text(
                    text = "Categories",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WallpaperCategory.values().forEach { cat ->
                        val isSelected = cat == selectedCategory
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) AnimeNeonPink else Color(0xFF1E1736),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) AnimeNeonPink else Color(0x33A855F7)
                            ),
                            modifier = Modifier
                                .clickable { onCategorySelect(cat) }
                                .testTag("category_chip_${cat.name}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = cat.icon, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = cat.displayName,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Tag Filter Chips
        item(span = { GridItemSpan(2) }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickTags.forEach { tag ->
                    val isSelected = tag == selectedTag
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) AnimeNeonCyan.copy(alpha = 0.25f) else Color(0xFF151025),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) AnimeNeonCyan else Color(0x228B5CF6)
                        ),
                        modifier = Modifier
                            .clickable { onTagSelect(tag) }
                            .testTag("tag_chip_$tag")
                    ) {
                        Text(
                            text = if (tag == "All") "All Tags" else "#$tag",
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) AnimeNeonCyan else TextSecondary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Count Header
        item(span = { GridItemSpan(2) }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${wallpapers.size} Wallpapers Available",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Tap to Download & Set",
                    fontSize = 11.sp,
                    color = AnimeNeonCyan
                )
            }
        }

        // Wallpaper Grid Items
        if (wallpapers.isEmpty()) {
            item(span = { GridItemSpan(2) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SearchOff,
                            contentDescription = null,
                            tint = AnimeNeonPurple,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "No anime wallpapers found",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Text(
                            text = "Try searching for a different anime series or character",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        } else {
            items(wallpapers, key = { it.id }) { item ->
                WallpaperCard(
                    wallpaper = item,
                    isFavorite = favorites.contains(item.id),
                    onFavoriteClick = { onFavoriteToggle(item.id) },
                    onClick = { onWallpaperClick(item) }
                )
            }
        }

        // Spacer at bottom
        item(span = { GridItemSpan(2) }) {
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}

@Composable
fun FeaturedHeroCard(
    wallpaper: AnimeWallpaper,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1530)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier
            .width(260.dp)
            .height(160.dp)
            .clickable(onClick = onClick)
            .testTag("featured_card_${wallpaper.id}")
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (wallpaper.drawableResId != null) {
                Image(
                    painter = painterResource(id = wallpaper.drawableResId),
                    contentDescription = wallpaper.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0x33000000),
                                Color(0x990A0713),
                                Color(0xF00A0713)
                            )
                        )
                    )
            )

            // Top Badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = AnimeNeonPink,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
            ) {
                Text(
                    text = "FEATURED 4K",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }

            // Bottom Content
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = wallpaper.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${wallpaper.animeSeries} • ${wallpaper.character}",
                    color = AnimeNeonCyan,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${wallpaper.downloads} downloads",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Download,
                            contentDescription = "Download ready",
                            tint = AnimeNeonCyan,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Download",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AnimeNeonCyan
                        )
                    }
                }
            }
        }
    }
}
