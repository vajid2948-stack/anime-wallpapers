package com.example.ui

import android.app.Application
import android.app.WallpaperManager
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.WallpaperRepository
import com.example.model.AnimeWallpaper
import com.example.model.WallpaperCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab(val title: String, val icon: String) {
    EXPLORE("Explore", "browse"),
    CATEGORIES("Categories", "category"),
    FAVORITES("Favorites", "favorite")
}

sealed interface ApplyStatus {
    object Idle : ApplyStatus
    object Applying : ApplyStatus
    data class Success(val message: String) : ApplyStatus
    data class Error(val error: String) : ApplyStatus
}

sealed interface DownloadStatus {
    object Idle : DownloadStatus
    object Downloading : DownloadStatus
    data class Success(val message: String) : DownloadStatus
    data class Error(val error: String) : DownloadStatus
}

class WallpaperViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WallpaperRepository(application.applicationContext)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(WallpaperCategory.ALL)
    val selectedCategory: StateFlow<WallpaperCategory> = _selectedCategory.asStateFlow()

    private val _selectedTag = MutableStateFlow("All")
    val selectedTag: StateFlow<String> = _selectedTag.asStateFlow()

    private val _currentTab = MutableStateFlow(MainTab.EXPLORE)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _selectedWallpaper = MutableStateFlow<AnimeWallpaper?>(null)
    val selectedWallpaper: StateFlow<AnimeWallpaper?> = _selectedWallpaper.asStateFlow()

    private val _applyStatus = MutableStateFlow<ApplyStatus>(ApplyStatus.Idle)
    val applyStatus: StateFlow<ApplyStatus> = _applyStatus.asStateFlow()

    private val _downloadStatus = MutableStateFlow<DownloadStatus>(DownloadStatus.Idle)
    val downloadStatus: StateFlow<DownloadStatus> = _downloadStatus.asStateFlow()

    val favorites: StateFlow<Set<String>> = repository.favorites

    val allWallpapers: List<AnimeWallpaper> = repository.getAllWallpapers()

    val featuredWallpapers: List<AnimeWallpaper> = allWallpapers.filter { it.drawableResId != null }

    val filteredWallpapers: StateFlow<List<AnimeWallpaper>> = combine(
        _searchQuery,
        _selectedCategory,
        _selectedTag
    ) { query, category, tag ->
        allWallpapers.filter { wp ->
            val matchesQuery = query.isBlank() ||
                wp.title.contains(query, ignoreCase = true) ||
                wp.animeSeries.contains(query, ignoreCase = true) ||
                wp.character.contains(query, ignoreCase = true) ||
                wp.tags.any { it.contains(query, ignoreCase = true) }

            val matchesCategory = category == WallpaperCategory.ALL ||
                (category == WallpaperCategory.TRENDING && (wp.rating >= 4.9f || wp.drawableResId != null)) ||
                wp.category == category

            val matchesTag = tag == "All" || wp.tags.any { it.equals(tag, ignoreCase = true) }

            matchesQuery && matchesCategory && matchesTag
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), allWallpapers)

    val favoriteWallpapers: StateFlow<List<AnimeWallpaper>> = repository.favorites.combine(_searchQuery) { favs, query ->
        allWallpapers.filter { favs.contains(it.id) && (query.isBlank() || it.title.contains(query, ignoreCase = true)) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelect(category: WallpaperCategory) {
        _selectedCategory.value = category
    }

    fun onTagSelect(tag: String) {
        _selectedTag.value = tag
    }

    fun onTabChange(tab: MainTab) {
        _currentTab.value = tab
    }

    fun selectWallpaper(wallpaper: AnimeWallpaper?) {
        _selectedWallpaper.value = wallpaper
    }

    fun toggleFavorite(wallpaperId: String) {
        repository.toggleFavorite(wallpaperId)
    }

    fun isFavorite(wallpaperId: String): Boolean {
        return repository.isFavorite(wallpaperId)
    }

    fun shareWallpaper(wallpaper: AnimeWallpaper) {
        repository.shareWallpaper(wallpaper)
    }

    fun applyWallpaper(wallpaper: AnimeWallpaper, whichTarget: Int) {
        viewModelScope.launch {
            _applyStatus.value = ApplyStatus.Applying
            val result = repository.applyWallpaper(wallpaper, whichTarget)
            result.onSuccess {
                val targetText = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    when (whichTarget) {
                        WallpaperManager.FLAG_SYSTEM -> "Home Screen"
                        WallpaperManager.FLAG_LOCK -> "Lock Screen"
                        else -> "Home & Lock Screen"
                    }
                } else {
                    "Device Wallpaper"
                }
                _applyStatus.value = ApplyStatus.Success("Successfully applied to $targetText!")
            }.onFailure { err ->
                _applyStatus.value = ApplyStatus.Error(err.localizedMessage ?: "Failed to set wallpaper")
            }
        }
    }

    fun clearApplyStatus() {
        _applyStatus.value = ApplyStatus.Idle
    }

    fun downloadWallpaper(wallpaper: AnimeWallpaper) {
        viewModelScope.launch {
            _downloadStatus.value = DownloadStatus.Downloading
            val result = repository.downloadWallpaperToGallery(wallpaper)
            result.onSuccess { path ->
                _downloadStatus.value = DownloadStatus.Success("Wallpaper saved to Gallery!")
            }.onFailure { err ->
                _downloadStatus.value = DownloadStatus.Error(err.localizedMessage ?: "Failed to download wallpaper")
            }
        }
    }

    fun clearDownloadStatus() {
        _downloadStatus.value = DownloadStatus.Idle
    }
}
