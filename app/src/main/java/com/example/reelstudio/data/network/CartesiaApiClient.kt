package com.example.reelstudio.data.network

import android.util.Log
import com.example.reelstudio.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Cartesia API Client for realistic Text-to-Speech (TTS) audio streaming
 * and voice cloning using Cartesia Sonic 2.0.
 */
class CartesiaApiClient(
    private var customApiKey: String? = null
) {
    companion object {
        private const val TAG = "CartesiaApiClient"
        private const val BASE_URL = "https://api.cartesia.ai"
        private const val TTS_BYTES_ENDPOINT = "$BASE_URL/tts/bytes"
        private const val VOICES_ENDPOINT = "$BASE_URL/voices"
        private const val CLONE_ENDPOINT = "$BASE_URL/voices/clone/clip"
        private const val API_VERSION = "2024-06-10"

        // Default high-quality realistic Cartesia voice IDs
        val PRESET_VOICE_IDS = mapOf(
            "sofia" to "a0e99841-438c-4a64-b679-ae501e7d6091",
            "lucas" to "638efaaa-4d0c-442e-b701-3fae16ae0079",
            "beatriz" to "e0436d4f-f3b1-4c69-8ab3-380d3adbe6f2",
            "gabriel" to "fb26447f-308b-471e-8b00-8e9f04284eb5",
            "maya" to "c45bc5ec-5968-4f0f-8b29-e31a0478ef8a",
            "theo" to "79f8b5fb-2cf8-4795-8025-c1a147ce1385",
            "elena" to "2ee87190-8f84-4925-97da-e52547f9462c"
        )
    }

    /**
     * Resolves the active Cartesia API key from Custom Key, BuildConfig, or Environment.
     */
    fun getApiKey(): String {
        customApiKey?.trim()?.let {
            if (it.isNotEmpty() && !it.equals("YOUR_CARTESIA_API_KEY", ignoreCase = true)) {
                return it
            }
        }

        try {
            val buildConfigKey = BuildConfig.CARTESIA_API_KEY.trim()
            if (buildConfigKey.isNotEmpty() && !buildConfigKey.equals("YOUR_CARTESIA_API_KEY", ignoreCase = true)) {
                return buildConfigKey
            }
        } catch (_: Throwable) {
            // BuildConfig field might not be present in some configurations
        }

        return System.getenv("CARTESIA_API_KEY")?.trim() ?: ""
    }

    fun isApiKeyConfigured(): Boolean {
        val key = getApiKey()
        return key.isNotBlank() && !key.equals("YOUR_CARTESIA_API_KEY", ignoreCase = true)
    }

    fun setCustomApiKey(key: String?) {
        customApiKey = key
    }

    /**
     * Resolves a voice name or ID to a valid Cartesia voice UUID string.
     */
    fun resolveVoiceId(voiceNameOrId: String): String {
        val clean = voiceNameOrId.trim().lowercase()
        return PRESET_VOICE_IDS[clean]
            ?: PRESET_VOICE_IDS[clean.removePrefix("clone-")]
            ?: if (voiceNameOrId.contains("-") && voiceNameOrId.length >= 20) voiceNameOrId else PRESET_VOICE_IDS["sofia"]!!
    }

    /**
     * Resolves language code (e.g. "pt", "en", "es", "fr", "de", "ja")
     */
    fun resolveLanguageCode(languageName: String): String {
        val clean = languageName.lowercase()
        return when {
            clean.contains("pt") || clean.contains("portug") -> "pt"
            clean.contains("en") || clean.contains("ingl") || clean.contains("english") -> "en"
            clean.contains("es") || clean.contains("espan") || clean.contains("spanish") -> "es"
            clean.contains("fr") || clean.contains("franc") || clean.contains("french") -> "fr"
            clean.contains("de") || clean.contains("alem") || clean.contains("german") -> "de"
            clean.contains("ja") || clean.contains("japon") || clean.contains("japanese") -> "ja"
            else -> "pt"
        }
    }

    /**
     * Fetches realistic Text-to-Speech audio bytes (WAV PCM 44.1kHz) from Cartesia Sonic 2.0.
     */
    suspend fun fetchTtsAudioBytes(
        transcript: String,
        voiceNameOrId: String = "sofia",
        language: String = "pt",
        sampleRate: Int = 44100
    ): Result<ByteArray> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank() || apiKey.equals("YOUR_CARTESIA_API_KEY", ignoreCase = true)) {
            return@withContext Result.failure(
                IllegalStateException("CARTESIA_API_KEY não configurada. Configure sua chave no painel Secrets.")
            )
        }

        if (transcript.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Transcrição de texto vazia"))
        }

        var connection: HttpURLConnection? = null
        try {
            val url = URL(TTS_BYTES_ENDPOINT)
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 15000
                readTimeout = 30000
                doOutput = true
                doInput = true
                setRequestProperty("X-API-Key", apiKey)
                setRequestProperty("Cartesia-Version", API_VERSION)
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "audio/wav, audio/*, application/octet-stream")
            }

            val resolvedVoiceId = resolveVoiceId(voiceNameOrId)
            val langCode = resolveLanguageCode(language)

            // Build Cartesia Sonic JSON request body
            val requestJson = JSONObject().apply {
                put("model_id", "sonic-multilingual")
                put("transcript", transcript)
                put("voice", JSONObject().apply {
                    put("mode", "id")
                    put("id", resolvedVoiceId)
                })
                put("output_format", JSONObject().apply {
                    put("container", "wav")
                    put("encoding", "pcm_s16le")
                    put("sample_rate", sampleRate)
                })
                put("language", langCode)
            }

            val requestBytes = requestJson.toString().toByteArray(Charsets.UTF_8)
            connection.outputStream.use { os ->
                os.write(requestBytes)
                os.flush()
            }

            val responseCode = connection.responseCode
            if (responseCode in 200..299) {
                val inputStream: InputStream = connection.inputStream
                val buffer = ByteArrayOutputStream()
                val chunk = ByteArray(8192)
                var bytesRead: Int
                while (inputStream.read(chunk).also { bytesRead = it } != -1) {
                    buffer.write(chunk, 0, bytesRead)
                }
                val audioBytes = buffer.toByteArray()
                Log.d(TAG, "Cartesia TTS stream received: ${audioBytes.size} bytes for voice $resolvedVoiceId")
                Result.success(audioBytes)
            } else {
                val errorMsg = try {
                    connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "HTTP $responseCode"
                } catch (e: Exception) {
                    "HTTP $responseCode"
                }
                Log.e(TAG, "Cartesia API error ($responseCode): $errorMsg")
                Result.failure(RuntimeException("Erro na API Cartesia ($responseCode): $errorMsg"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Falha na requisição Cartesia API", e)
            Result.failure(e)
        } finally {
            connection?.disconnect()
        }
    }

    /**
     * Streams TTS audio directly to a target OutputStream (useful for caching to disk or streaming).
     */
    suspend fun streamTtsToOutputStream(
        transcript: String,
        voiceNameOrId: String,
        outputStream: OutputStream,
        language: String = "pt",
        onProgress: ((bytesWritten: Int) -> Unit)? = null
    ): Result<Int> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank() || apiKey.equals("YOUR_CARTESIA_API_KEY", ignoreCase = true)) {
            return@withContext Result.failure(
                IllegalStateException("CARTESIA_API_KEY não configurada.")
            )
        }

        var connection: HttpURLConnection? = null
        try {
            val url = URL(TTS_BYTES_ENDPOINT)
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 15000
                readTimeout = 30000
                doOutput = true
                doInput = true
                setRequestProperty("X-API-Key", apiKey)
                setRequestProperty("Cartesia-Version", API_VERSION)
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "audio/wav, audio/*, application/octet-stream")
            }

            val resolvedVoiceId = resolveVoiceId(voiceNameOrId)
            val langCode = resolveLanguageCode(language)

            val requestJson = JSONObject().apply {
                put("model_id", "sonic-multilingual")
                put("transcript", transcript)
                put("voice", JSONObject().apply {
                    put("mode", "id")
                    put("id", resolvedVoiceId)
                })
                put("output_format", JSONObject().apply {
                    put("container", "wav")
                    put("encoding", "pcm_s16le")
                    put("sample_rate", 44100)
                })
                put("language", langCode)
            }

            connection.outputStream.use { os ->
                os.write(requestJson.toString().toByteArray(Charsets.UTF_8))
                os.flush()
            }

            val responseCode = connection.responseCode
            if (responseCode in 200..299) {
                val inputStream: InputStream = connection.inputStream
                val buffer = ByteArray(8192)
                var totalBytes = 0
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                    totalBytes += read
                    onProgress?.invoke(totalBytes)
                }
                outputStream.flush()
                Result.success(totalBytes)
            } else {
                val errorMsg = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "HTTP $responseCode"
                Result.failure(RuntimeException("Erro na API Cartesia ($responseCode): $errorMsg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            connection?.disconnect()
        }
    }

    /**
     * Lists available voices from Cartesia API.
     */
    suspend fun listVoices(): Result<List<CartesiaVoiceDto>> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank() || apiKey.equals("YOUR_CARTESIA_API_KEY", ignoreCase = true)) {
            return@withContext Result.failure(IllegalStateException("CARTESIA_API_KEY não configurada."))
        }

        var connection: HttpURLConnection? = null
        try {
            val url = URL(VOICES_ENDPOINT)
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10000
                readTimeout = 15000
                setRequestProperty("X-API-Key", apiKey)
                setRequestProperty("Cartesia-Version", API_VERSION)
                setRequestProperty("Accept", "application/json")
            }

            val responseCode = connection.responseCode
            if (responseCode in 200..299) {
                val jsonString = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonArray = JSONArray(jsonString)
                val voices = mutableListOf<CartesiaVoiceDto>()

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    voices.add(
                        CartesiaVoiceDto(
                            id = obj.optString("id"),
                            name = obj.optString("name"),
                            description = obj.optString("description"),
                            language = obj.optString("language", "pt"),
                            gender = obj.optString("gender", "")
                        )
                    )
                }
                Result.success(voices)
            } else {
                val errorMsg = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "HTTP $responseCode"
                Result.failure(RuntimeException("Erro ao listar vozes ($responseCode): $errorMsg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            connection?.disconnect()
        }
    }
}

data class CartesiaVoiceDto(
    val id: String,
    val name: String,
    val description: String,
    val language: String,
    val gender: String
)
