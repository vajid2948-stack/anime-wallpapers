package com.example.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

data class AppUpdateInfo(
    val hasUpdate: Boolean,
    val latestVersionCode: Int,
    val latestVersionName: String,
    val releaseNotes: String,
    val downloadUrl: String,
    val isForceUpdate: Boolean
)

object UpdateManager {

    // Default GitHub raw configuration endpoint (or fallback)
    // You can host version_update.json on GitHub or any free host
    private const val UPDATE_CONFIG_URL =
        "https://raw.githubusercontent.com/Vajid2948/anime-wallpapers/main/version_update.json"

    // Default fallback download URL if remote doesn't specify
    private const val DEFAULT_DOWNLOAD_URL =
        "https://github.com/Vajid2948/anime-wallpapers/releases/latest"

    fun getCurrentVersionCode(context: Context): Int {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                pInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                pInfo.versionCode
            }
        } catch (e: Exception) {
            1
        }
    }

    fun getCurrentVersionName(context: Context): String {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName ?: "1.0"
        } catch (e: Exception) {
            "1.0"
        }
    }

    suspend fun checkForUpdate(context: Context): AppUpdateInfo = withContext(Dispatchers.IO) {
        val currentCode = getCurrentVersionCode(context)
        var connection: HttpURLConnection? = null
        try {
            val url = URL(UPDATE_CONFIG_URL)
            connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "AnimeWallpapersApp")

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.use { it.readText() }
                val json = JSONObject(response)

                val latestCode = json.optInt("versionCode", currentCode)
                val latestName = json.optString("versionName", "1.1")
                val releaseNotes = json.optString(
                    "releaseNotes",
                    "• Added 20+ New Ultra-HD Anime Wallpapers (Naruto, Gojo, Luffy Gear 5)\n• Performance & download speed improvements"
                )
                val downloadUrl = json.optString("downloadUrl", DEFAULT_DOWNLOAD_URL)
                val isForce = json.optBoolean("forceUpdate", false)

                return@withContext AppUpdateInfo(
                    hasUpdate = latestCode > currentCode,
                    latestVersionCode = latestCode,
                    latestVersionName = latestName,
                    releaseNotes = releaseNotes,
                    downloadUrl = downloadUrl,
                    isForceUpdate = isForce
                )
            }
        } catch (e: Exception) {
            // Network failure or repo not yet pushed, return no update
        } finally {
            connection?.disconnect()
        }

        AppUpdateInfo(
            hasUpdate = false,
            latestVersionCode = currentCode,
            latestVersionName = getCurrentVersionName(context),
            releaseNotes = "",
            downloadUrl = DEFAULT_DOWNLOAD_URL,
            isForceUpdate = false
        )
    }

    fun openDownloadUrl(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to github repo
            try {
                val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(DEFAULT_DOWNLOAD_URL)).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(fallbackIntent)
            } catch (_: Exception) {}
        }
    }
}
