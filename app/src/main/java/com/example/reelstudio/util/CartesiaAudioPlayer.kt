package com.example.reelstudio.util

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.example.reelstudio.data.network.CartesiaApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

/**
 * High-performance Cartesia audio player with disk caching and streaming playback.
 */
class CartesiaAudioPlayer(
    private val context: Context,
    private val apiClient: CartesiaApiClient = CartesiaApiClient()
) {
    companion object {
        private const val TAG = "CartesiaAudioPlayer"
    }

    private var mediaPlayer: MediaPlayer? = null
    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    val client: CartesiaApiClient get() = apiClient

    /**
     * Synthesizes text with Cartesia Sonic and plays the high-fidelity WAV audio.
     */
    fun playCartesiaTts(
        text: String,
        voiceNameOrId: String = "sofia",
        language: String = "pt",
        onStart: () -> Unit = {},
        onDone: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        stop()

        playbackJob = scope.launch {
            try {
                val cacheDir = File(context.cacheDir, "cartesia_audio").apply { if (!exists()) mkdirs() }
                val cacheKey = hashText("$text|$voiceNameOrId|$language")
                val cachedFile = File(cacheDir, "cartesia_$cacheKey.wav")

                val targetAudioFile: File = if (cachedFile.exists() && cachedFile.length() > 100) {
                    Log.d(TAG, "Playing from cache: ${cachedFile.name}")
                    cachedFile
                } else {
                    onStart()
                    val result = apiClient.fetchTtsAudioBytes(
                        transcript = text,
                        voiceNameOrId = voiceNameOrId,
                        language = language
                    )

                    if (result.isSuccess) {
                        val audioBytes = result.getOrThrow()
                        withContext(Dispatchers.IO) {
                            FileOutputStream(cachedFile).use { fos ->
                                fos.write(audioBytes)
                                fos.flush()
                            }
                        }
                        cachedFile
                    } else {
                        val error = result.exceptionOrNull()?.message ?: "Erro ao gerar áudio Cartesia"
                        Log.e(TAG, "Cartesia TTS failed: $error")
                        onError(error)
                        return@launch
                    }
                }

                playAudioFile(targetAudioFile, onDone, onError)
            } catch (e: Exception) {
                Log.e(TAG, "Erro na execução do áudio Cartesia", e)
                onError(e.message ?: "Erro desconhecido")
                onDone()
            }
        }
    }

    /**
     * Plays raw audio bytes directly.
     */
    fun playBytes(
        bytes: ByteArray,
        onDone: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        stop()
        playbackJob = scope.launch {
            try {
                val tempFile = File.createTempFile("cartesia_stream_", ".wav", context.cacheDir)
                withContext(Dispatchers.IO) {
                    FileOutputStream(tempFile).use { it.write(bytes) }
                }
                playAudioFile(tempFile, onDone, onError)
            } catch (e: Exception) {
                onError(e.message ?: "Falha ao reproduzir áudio")
                onDone()
            }
        }
    }

    private fun playAudioFile(
        file: File,
        onDone: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            stop()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                setOnPreparedListener { mp ->
                    mp.start()
                }
                setOnCompletionListener {
                    stop()
                    onDone()
                }
                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "MediaPlayer error: what=$what extra=$extra")
                    stop()
                    onError("Erro no player ($what, $extra)")
                    onDone()
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Falha ao inicializar MediaPlayer", e)
            onError(e.message ?: "Falha no player de áudio")
            onDone()
        }
    }

    fun isPlaying(): Boolean {
        return try {
            mediaPlayer?.isPlaying == true
        } catch (_: Exception) {
            false
        }
    }

    fun stop() {
        playbackJob?.cancel()
        playbackJob = null
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
    }

    fun release() {
        stop()
    }

    private fun hashText(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }.take(20)
    }
}
