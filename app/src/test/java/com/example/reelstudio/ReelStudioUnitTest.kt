package com.example.reelstudio

import com.example.reelstudio.data.local.SceneEntity
import com.example.reelstudio.data.model.ReelDefaults
import com.example.reelstudio.data.network.CartesiaApiClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReelStudioUnitTest {

    @Test
    fun testTotalDurationCalculation() {
        val scenes = listOf(
            SceneEntity(
                id = 1L,
                projectId = "p1",
                orderIndex = 0,
                label = "Cena 1",
                title = "Introdução",
                caption = "Olá",
                durationSeconds = 5,
                trimStartSeconds = 0.5f,
                trimEndSeconds = 4.5f,
                originalDurationSeconds = 5.0f
            ),
            SceneEntity(
                id = 2L,
                projectId = "p1",
                orderIndex = 1,
                label = "Cena 2",
                title = "Problema",
                caption = "Como resolver",
                durationSeconds = 6,
                trimStartSeconds = 1.0f,
                trimEndSeconds = 5.0f,
                originalDurationSeconds = 6.0f
            )
        )

        val totalTrimmedSeconds = scenes.sumOf { scene ->
            (scene.trimEndSeconds - scene.trimStartSeconds).coerceAtLeast(0.5f).toDouble()
        }

        // Scene 1: 4.5 - 0.5 = 4.0
        // Scene 2: 5.0 - 1.0 = 4.0
        // Total = 8.0
        assertEquals(8.0, totalTrimmedSeconds, 0.01)
    }

    @Test
    fun testPresetAndFormatOptions() {
        assertTrue(ReelDefaults.formatOptions.contains("9:16 Portrait"))
        assertTrue(ReelDefaults.formatOptions.contains("16:9 Landscape"))
        assertTrue(ReelDefaults.engineOptions.contains("Remotion"))
    }

    @Test
    fun testTrimmingCoerceBounds() {
        val startSec = -1.0f
        val endSec = 10.0f
        val maxDuration = 6.0f

        val safeStart = startSec.coerceAtLeast(0f)
        val safeEnd = endSec.coerceAtMost(maxDuration)
        val trimmed = (safeEnd - safeStart).coerceAtLeast(0.5f)

        assertEquals(0f, safeStart, 0.001f)
        assertEquals(6.0f, safeEnd, 0.001f)
        assertEquals(6.0f, trimmed, 0.001f)
    }

    @Test
    fun testCartesiaApiClientVoiceResolution() {
        val client = CartesiaApiClient()

        // Test preset voice mappings
        val sofiaId = client.resolveVoiceId("Sofia")
        val lucasId = client.resolveVoiceId("Lucas")
        val beatrizId = client.resolveVoiceId("Beatriz")

        assertNotNull(sofiaId)
        assertNotNull(lucasId)
        assertNotNull(beatrizId)
        assertEquals("a0e99841-438c-4a64-b679-ae501e7d6091", sofiaId)
        assertEquals("638efaaa-4d0c-442e-b701-3fae16ae0079", lucasId)
        assertEquals("e0436d4f-f3b1-4c69-8ab3-380d3adbe6f2", beatrizId)

        // Custom UUID should be preserved
        val customUuid = "12345678-1234-1234-1234-123456789abc"
        assertEquals(customUuid, client.resolveVoiceId(customUuid))
    }

    @Test
    fun testCartesiaApiClientLanguageMapping() {
        val client = CartesiaApiClient()

        assertEquals("pt", client.resolveLanguageCode("Português (BR)"))
        assertEquals("pt", client.resolveLanguageCode("pt"))
        assertEquals("en", client.resolveLanguageCode("Inglês"))
        assertEquals("en", client.resolveLanguageCode("English"))
        assertEquals("es", client.resolveLanguageCode("Espanhol"))
    }

    @Test
    fun testCartesiaApiClientCustomApiKey() {
        val client = CartesiaApiClient()
        client.setCustomApiKey("test_cartesia_key_12345")

        assertEquals("test_cartesia_key_12345", client.getApiKey())
        assertTrue(client.isApiKeyConfigured())

        client.setCustomApiKey("YOUR_CARTESIA_API_KEY")
        assertFalse(client.isApiKeyConfigured())
    }
}
