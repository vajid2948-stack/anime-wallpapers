package com.example.model

enum class WallpaperCategory(val displayName: String, val icon: String) {
    ALL("All Wallpapers", "✨"),
    TRENDING("Trending", "🔥"),
    ACTION("Action & Shonen", "⚔️"),
    CYBERPUNK("Cyberpunk", "🌆"),
    LOFI_CHILL("Lo-Fi & Chill", "🌸"),
    FANTASY("Fantasy & Magic", "🔮"),
    DARK_ANIME("Dark & AMOLED", "🌑"),
    SCENERY("Scenery & Skies", "🏞️")
}

data class AnimeWallpaper(
    val id: String,
    val title: String,
    val animeSeries: String,
    val character: String,
    val category: WallpaperCategory,
    val tags: List<String>,
    val drawableResId: Int? = null,
    val imageUrl: String = "",
    val accentColorHex: Long = 0xFF1F1A3A,
    val downloads: String = "12.4K",
    val rating: Float = 4.9f,
    val resolution: String = "4K UHD (2160x3840)",
    val description: String = ""
)
