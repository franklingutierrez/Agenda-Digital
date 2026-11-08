package com.example

import com.example.util.AppUpdateManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AppVersionUnitTest {

    @Test
    fun testVersionComparison_1_10_is_strictly_newer_than_1_9() {
        // Validación crítica: comparación numérica semántica (1.10 > 1.9)
        assertTrue(
            "1.10 debe ser mayor que 1.9",
            AppUpdateManager.isVersionStrictlyNewer("1.10", "1.9")
        )
        assertTrue(
            "v1.10 debe ser mayor que v1.9",
            AppUpdateManager.isVersionStrictlyNewer("v1.10", "v1.9")
        )
        assertFalse(
            "1.9 no es mayor que 1.10",
            AppUpdateManager.isVersionStrictlyNewer("1.9", "1.10")
        )
    }

    @Test
    fun testVersionComparison_equality_is_rejected() {
        // Versiones idénticas no deben activar actualización
        assertFalse(
            "1.0 no es más nueva que 1.0",
            AppUpdateManager.isVersionStrictlyNewer("1.0", "1.0")
        )
        assertFalse(
            "v1.0 no es más nueva que 1.0",
            AppUpdateManager.isVersionStrictlyNewer("v1.0", "1.0")
        )
        assertFalse(
            "1.0.0 no es más nueva que 1.0.0",
            AppUpdateManager.isVersionStrictlyNewer("1.0.0", "1.0.0")
        )
    }

    @Test
    fun testVersionComparison_inferior_version_is_rejected() {
        // Versiones remotas inferiores no deben activar actualización
        assertFalse(
            "0.9 no debe activar actualización si estamos en 1.0",
            AppUpdateManager.isVersionStrictlyNewer("0.9", "1.0")
        )
        assertFalse(
            "1.0 no debe activar actualización si estamos en 1.1",
            AppUpdateManager.isVersionStrictlyNewer("1.0", "1.1")
        )
        assertFalse(
            "1.0.5 no debe activar actualización si estamos en 1.1.0",
            AppUpdateManager.isVersionStrictlyNewer("1.0.5", "1.1.0")
        )
    }

    @Test
    fun testVersionComparison_invalid_tags_are_rejected() {
        // Tags con nombres no numéricos, vacíos o malformados deben ser descartados
        assertFalse(AppUpdateManager.isVersionStrictlyNewer("beta-1", "1.0"))
        assertFalse(AppUpdateManager.isVersionStrictlyNewer("release-candidate", "1.0"))
        assertFalse(AppUpdateManager.isVersionStrictlyNewer("", "1.0"))
        assertFalse(AppUpdateManager.isVersionStrictlyNewer("   ", "1.0"))
        assertFalse(AppUpdateManager.isVersionStrictlyNewer("v", "1.0"))
        assertFalse(AppUpdateManager.isVersionStrictlyNewer("1.a.2", "1.0"))

        assertNull(AppUpdateManager.sanitizeTagToVersion("beta-2.0"))
        assertNull(AppUpdateManager.sanitizeTagToVersion("v"))
        assertNull(AppUpdateManager.sanitizeTagToVersion(""))
        assertNull(AppUpdateManager.sanitizeTagToVersion("1.0-alpha"))
    }

    @Test
    fun testSanitizeTagToVersion_valid_prefixes() {
        assertEquals("1.0", AppUpdateManager.sanitizeTagToVersion("1.0"))
        assertEquals("1.0", AppUpdateManager.sanitizeTagToVersion("v1.0"))
        assertEquals("1.2.3", AppUpdateManager.sanitizeTagToVersion("V1.2.3"))
        assertEquals("2.0.0", AppUpdateManager.sanitizeTagToVersion("  v2.0.0  "))
    }

    @Test
    fun testApkAssetSelection_and_repo_url_validation() {
        val validRepoUrl = "https://github.com/franklingutierrez/Agenda-Digital/releases/download/v1.1/app-release.apk"
        val foreignRepoUrl = "https://github.com/otro-usuario/otro-repo/releases/download/v1.1/app-release.apk"
        val oldDragontecUrl = "https://github.com/dragontec691/agenda-digital-peru/releases/download/v1.1/app-release.apk"

        assertTrue(
            "URL del repo correcto debe ser aceptada",
            AppUpdateManager.isUrlFromCorrectRepo(validRepoUrl)
        )
        assertFalse(
            "URL de otro repositorio debe ser rechazada",
            AppUpdateManager.isUrlFromCorrectRepo(foreignRepoUrl)
        )
        assertFalse(
            "URL antigua de dragontec691 debe ser rechazada",
            AppUpdateManager.isUrlFromCorrectRepo(oldDragontecUrl)
        )
    }
}
