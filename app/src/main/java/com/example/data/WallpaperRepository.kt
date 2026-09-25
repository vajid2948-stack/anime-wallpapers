package com.example.data

import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.model.AnimeWallpaper
import com.example.model.WallpaperCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class WallpaperRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences("anime_wallpapers_prefs", Context.MODE_PRIVATE)
    private val PREF_KEY_FAVORITES = "user_favorites"

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    init {
        val saved = prefs.getStringSet(PREF_KEY_FAVORITES, emptySet()) ?: emptySet()
        _favorites.value = saved
    }

    fun getAllWallpapers(): List<AnimeWallpaper> = WallpaperData.wallpapers

    fun getWallpaperById(id: String): AnimeWallpaper? {
        return WallpaperData.wallpapers.find { it.id == id }
    }

    fun toggleFavorite(wallpaperId: String) {
        val current = _favorites.value.toMutableSet()
        if (current.contains(wallpaperId)) {
            current.remove(wallpaperId)
        } else {
            current.add(wallpaperId)
        }
        _favorites.value = current
        prefs.edit().putStringSet(PREF_KEY_FAVORITES, current).apply()
    }

    fun isFavorite(wallpaperId: String): Boolean {
        return _favorites.value.contains(wallpaperId)
    }

    suspend fun loadBitmap(wallpaper: AnimeWallpaper): Bitmap? = withContext(Dispatchers.IO) {
        try {
            if (wallpaper.drawableResId != null) {
                BitmapFactory.decodeResource(context.resources, wallpaper.drawableResId)
            } else if (wallpaper.imageUrl.isNotBlank()) {
                val loader = ImageLoader(context)
                val request = ImageRequest.Builder(context)
                    .data(wallpaper.imageUrl)
                    .allowHardware(false)
                    .build()
                val result = (loader.execute(request) as? SuccessResult)?.drawable
                (result as? BitmapDrawable)?.bitmap
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun applyWallpaper(
        wallpaper: AnimeWallpaper,
        whichFlag: Int // WallpaperManager.FLAG_SYSTEM, FLAG_LOCK, or both
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val bitmap = loadBitmap(wallpaper)
                ?: return@withContext Result.failure(Exception("Could not load image"))

            val wallpaperManager = WallpaperManager.getInstance(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                wallpaperManager.setBitmap(bitmap, null, true, whichFlag)
            } else {
                wallpaperManager.setBitmap(bitmap)
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun downloadWallpaperToGallery(wallpaper: AnimeWallpaper): Result<String> = withContext(Dispatchers.IO) {
        try {
            val bitmap = loadBitmap(wallpaper)
                ?: return@withContext Result.failure(Exception("Could not load image to download"))

            val cleanTitle = wallpaper.title.replace("\\s+".toRegex(), "_").lowercase()
            val fileName = "anime_wallpaper_${cleanTitle}_${System.currentTimeMillis()}.jpg"

            var outputStream: OutputStream? = null
            var savedPath = "Pictures/AnimeWallpapers"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/AnimeWallpapers")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }

                val uri: Uri? = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    outputStream = context.contentResolver.openOutputStream(uri)
                    if (outputStream != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
                        outputStream.flush()
                        outputStream.close()
                    }
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    context.contentResolver.update(uri, contentValues, null, null)
                } else {
                    return@withContext Result.failure(Exception("Failed to create MediaStore entry"))
                }
            } else {
                val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val appDir = File(picturesDir, "AnimeWallpapers")
                if (!appDir.exists()) {
                    appDir.mkdirs()
                }
                val imageFile = File(appDir, fileName)
                outputStream = FileOutputStream(imageFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
                outputStream.flush()
                outputStream.close()
                savedPath = imageFile.absolutePath

                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                }
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            }

            Result.success(savedPath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun shareWallpaper(wallpaper: AnimeWallpaper) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "Check out this epic Anime Wallpaper: ${wallpaper.title} (${wallpaper.animeSeries}) in 4K Ultra HD!"
            )
            type = "text/plain"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Wallpaper").apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(shareIntent)
    }
}
