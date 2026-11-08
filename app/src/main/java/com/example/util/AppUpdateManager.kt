package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class AppUpdateInfo(
    val hasUpdate: Boolean,
    val latestVersionName: String,
    val latestVersionCode: Int,
    val releaseNotes: String,
    val downloadUrl: String,
    val releasePageUrl: String
)

object AppUpdateManager {
    private const val TAG = "AppUpdateManager"

    /**
     * Repositorio de GitHub para consultar los releases.
     * Puedes actualizar 'owner' y 'repo' según la cuenta del usuario en GitHub.
     */
    var githubOwner: String = "dragontec691"
    var githubRepo: String = "agenda-digital-peru"

    suspend fun checkForUpdates(currentVersionCode: Int, currentVersionName: String): AppUpdateInfo? {
        return withContext(Dispatchers.IO) {
            try {
                val apiUrl = "https://api.github.com/repos/$githubOwner/$githubRepo/releases/latest"
                val url = URL(apiUrl)
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 8000
                    readTimeout = 8000
                    setRequestProperty("Accept", "application/vnd.github.v3+json")
                    setRequestProperty("User-Agent", "AgendaDigital-Android")
                }

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(responseText)

                    val tagName = json.optString("tag_name", "").removePrefix("v").trim()
                    val bodyNotes = json.optString("body", "Nueva versión disponible con mejoras y optimizaciones.")
                    val htmlUrl = json.optString("html_url", "https://github.com/$githubOwner/$githubRepo/releases")

                    var apkDownloadUrl = htmlUrl
                    val assets = json.optJSONArray("assets")
                    if (assets != null) {
                        for (i in 0 until assets.length()) {
                            val asset = assets.getJSONObject(i)
                            val name = asset.optString("name", "")
                            if (name.endsWith(".apk", ignoreCase = true)) {
                                apkDownloadUrl = asset.optString("browser_download_url", htmlUrl)
                                break
                            }
                        }
                    }

                    // Comparación semántica simple o por código
                    val isNewer = isVersionNewer(tagName, currentVersionName)

                    AppUpdateInfo(
                        hasUpdate = isNewer,
                        latestVersionName = tagName.ifEmpty { "1.1" },
                        latestVersionCode = currentVersionCode + 1,
                        releaseNotes = bodyNotes,
                        downloadUrl = apkDownloadUrl,
                        releasePageUrl = htmlUrl
                    )
                } else {
                    Log.d(TAG, "No se encontró release en GitHub o repositorio privado (código: ${connection.responseCode})")
                    null
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error consultando actualizaciones: ${e.message}")
                null
            }
        }
    }

    fun openDownloadUrl(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error abriendo URL de actualización", e)
        }
    }

    private fun isVersionNewer(remoteVersion: String, currentVersion: String): Boolean {
        if (remoteVersion.isBlank()) return false
        val remoteParts = remoteVersion.split(".").mapNotNull { it.toIntOrNull() }
        val currentParts = currentVersion.split(".").mapNotNull { it.toIntOrNull() }

        val maxLen = maxOf(remoteParts.size, currentParts.size)
        for (i in 0 until maxLen) {
            val r = remoteParts.getOrElse(i) { 0 }
            val c = currentParts.getOrElse(i) { 0 }
            if (r > c) return true
            if (r < c) return false
        }
        return false
    }
}
