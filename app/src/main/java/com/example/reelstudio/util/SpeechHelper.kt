package com.example.reelstudio.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import com.example.reelstudio.data.network.CartesiaApiClient
import java.util.Locale

/**
 * Speech Helper supporting both high-fidelity Cartesia Sonic 2.0 API streaming
 * and high-quality local prosody modulated speech synthesis.
 */
class SpeechHelper(private val context: Context) {
    companion object {
        private const val TAG = "SpeechHelper"
    }

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    val cartesiaPlayer: CartesiaAudioPlayer = CartesiaAudioPlayer(context)
    val apiClient: CartesiaApiClient get() = cartesiaPlayer.client

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                applyNaturalPortugueseVoice()
            }
        }
    }

    private fun applyNaturalPortugueseVoice() {
        try {
            val ptLocale = Locale("pt", "BR")
            val langResult = tts?.setLanguage(ptLocale)
            if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.getDefault())
            }

            // Selecionar voz de maior qualidade e naturalidade disponível (evitar vozes robóticas)
            tts?.voices?.let { voices ->
                val bestVoice = voices.find { voice ->
                    voice.locale.language == "pt" &&
                        !voice.isNetworkConnectionRequired &&
                        voice.quality >= Voice.QUALITY_NORMAL &&
                        !voice.name.contains("robotic", ignoreCase = true)
                } ?: voices.find { it.locale.language == "pt" }
                bestVoice?.let { tts?.voice = it }
            }
        } catch (e: Exception) {
            tts?.setLanguage(Locale.getDefault())
        }
    }

    /**
     * Checks if real Cartesia Sonic 2.0 API calls are active with a valid API key.
     */
    fun isUsingCartesiaApi(): Boolean {
        return apiClient.isApiKeyConfigured()
    }

    /**
     * Reproduz áudio com entonação humana ultra-realista via Cartesia Sonic 2.0 API
     * ou prosódia natural via síntese local.
     */
    fun speak(
        text: String,
        voiceName: String = "Sofia",
        pitch: Float? = null,
        speechRate: Float? = null,
        onDone: () -> Unit = {}
    ) {
        stop()

        // 1. Try Cartesia Sonic API first if key is configured
        if (isUsingCartesiaApi()) {
            Log.d(TAG, "Utilizando Cartesia Sonic API para voz: $voiceName")
            cartesiaPlayer.playCartesiaTts(
                text = text,
                voiceNameOrId = voiceName,
                language = "pt",
                onStart = {
                    Log.d(TAG, "Iniciando síntese e streaming Cartesia...")
                },
                onDone = onDone,
                onError = { error ->
                    Log.w(TAG, "Cartesia API falhou ($error), utilizando síntese de prosódia local.")
                    speakWithLocalTts(text, voiceName, pitch, speechRate, onDone)
                }
            )
            return
        }

        // 2. Fallback to local high-quality prosody TTS
        speakWithLocalTts(text, voiceName, pitch, speechRate, onDone)
    }

    private fun speakWithLocalTts(
        text: String,
        voiceName: String,
        pitch: Float?,
        speechRate: Float?,
        onDone: () -> Unit
    ) {
        if (!isInitialized || tts == null) {
            onDone()
            return
        }

        // Configuração de perfil vocal hiper-realista sem tom robótico
        val (effectiveLocale, defaultPitch, defaultRate) = when (voiceName.lowercase()) {
            "lucas" -> Triple(Locale("pt", "BR"), 0.96f, 1.05f) // Autoritativo, jovem
            "beatriz" -> Triple(Locale("pt", "BR"), 1.14f, 1.08f) // Dinâmica, criadora
            "gabriel" -> Triple(Locale("pt", "BR"), 0.84f, 0.96f) // Grave, documentário
            "maya" -> Triple(Locale.US, 1.04f, 1.0f) // Claro, acolhedor
            "theo" -> Triple(Locale.UK, 0.92f, 1.04f) // Enérgico, britânico
            "elena" -> Triple(Locale("es", "ES"), 1.08f, 1.0f) // Expressivo, cálido
            else -> Triple(Locale("pt", "BR"), 1.05f, 1.02f) // Sofia: Conversacional natural
        }

        try {
            tts?.setLanguage(effectiveLocale)
        } catch (_: Exception) { }

        tts?.setPitch(pitch ?: defaultPitch)
        tts?.setSpeechRate(speechRate ?: defaultRate)

        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) {
                onDone()
            }
            override fun onError(utteranceId: String?) {
                onDone()
            }
        })
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "speech_${System.currentTimeMillis()}")
    }

    fun stop() {
        cartesiaPlayer.stop()
        tts?.stop()
    }

    fun shutdown() {
        cartesiaPlayer.release()
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
