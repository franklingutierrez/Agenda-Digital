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
    val releaseNotes: String,
    val downloadUrl: String,
    val releasePageUrl: String
)

object AppUpdateManager {
    private const val TAG = "AppUpdateManager"

    // Repositorio conectado en GitHub
    const val GITHUB_OWNER: String = "franklingutierrez"
    const val GITHUB_REPO: String = "Agenda-Digital"

    suspend fun checkForUpdates(currentVersionName: String): AppUpdateInfo? {
        return withContext(Dispatchers.IO) {
            try {
                val apiUrl = "https://api.github.com/repos/$GITHUB_OWNER/$GITHUB_REPO/releases/latest"
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
                    val rawTag = json.optString("tag_name", "").trim()
                    val htmlUrl = json.optString("html_url", "https://github.com/$GITHUB_OWNER/$GITHUB_REPO/releases")
                    val bodyNotes = json.optString("body", "Nueva versión disponible con mejoras.")

                    // Validar tag y versión numérica
                    val remoteVersion = sanitizeTagToVersion(rawTag) ?: return@withContext null

                    // Buscar asset APK en el release del repositorio correcto
                    val assets = json.optJSONArray("assets")
                    var apkDownloadUrl: String? = null
                    if (assets != null) {
                        for (i in 0 until assets.length()) {
                            val asset = assets.getJSONObject(i)
                            val name = asset.optString("name", "")
                            if (name.endsWith(".apk", ignoreCase = true)) {
                                val browserUrl = asset.optString("browser_download_url", "")
                                if (browserUrl.isNotBlank() && isUrlFromCorrectRepo(browserUrl)) {
                                    apkDownloadUrl = browserUrl
                                    break
                                }
                            }
                        }
                    }

                    // Si no hay asset APK descargable del repositorio correcto, no notificar actualización directa
                    val finalApkUrl = apkDownloadUrl ?: return@withContext null

                    val isNewer = isVersionStrictlyNewer(remoteVersion, currentVersionName)
                    if (!isNewer) {
                        return@withContext null
                    }

                    AppUpdateInfo(
                        hasUpdate = true,
                        latestVersionName = remoteVersion,
                        releaseNotes = bodyNotes,
                        downloadUrl = finalApkUrl,
                        releasePageUrl = htmlUrl
                    )
                } else {
                    Log.d(TAG, "No se encontró release en GitHub (HTTP ${connection.responseCode})")
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

    /**
     * Valida y sanitiza una etiqueta de versión (ej. "v1.2", "1.2", "v1.10.3").
     * Rechaza tags inválidos como "beta", "v-draft", "release-candidate", "", etc.
     */
    fun sanitizeTagToVersion(rawTag: String): String? {
        val trimmed = rawTag.trim()
        if (trimmed.isEmpty()) return null
        val tagWithoutPrefix = if (trimmed.startsWith("v", ignoreCase = true)) {
            trimmed.substring(1).trim()
        } else {
            trimmed
        }

        if (tagWithoutPrefix.isEmpty()) return null

        val parts = tagWithoutPrefix.split(".")
        if (parts.isEmpty()) return null

        // Cada segmento debe ser un número entero válido >= 0
        for (part in parts) {
            val num = part.toIntOrNull() ?: return null
            if (num < 0) return null
        }

        return tagWithoutPrefix
    }

    /**
     * Compara numéricamente dos versiones semánticas (ej. "1.10" > "1.9", "1.0.1" > "1.0").
     * Devuelve true estrictamente si remoteVersion > currentVersion.
     * Rechaza versiones inválidas, menores o iguales.
     */
    fun isVersionStrictlyNewer(remoteVersion: String, currentVersion: String): Boolean {
        val remoteValid = sanitizeTagToVersion(remoteVersion) ?: return false
        val currentValid = sanitizeTagToVersion(currentVersion) ?: return false

        val remoteParts = remoteValid.split(".").map { it.toInt() }
        val currentParts = currentValid.split(".").map { it.toInt() }

        val maxLen = maxOf(remoteParts.size, currentParts.size)
        for (i in 0 until maxLen) {
            val r = remoteParts.getOrElse(i) { 0 }
            val c = currentParts.getOrElse(i) { 0 }
            if (r > c) return true
            if (r < c) return false
        }

        return false
    }

    /**
     * Valida que la URL del asset APK pertenezca al repositorio oficial de GitHub configurado.
     */
    fun isUrlFromCorrectRepo(url: String): Boolean {
        val expectedSegment = "$GITHUB_OWNER/$GITHUB_REPO"
        return url.contains(expectedSegment, ignoreCase = true) ||
                url.contains("github.com/$expectedSegment", ignoreCase = true)
    }
}
