package com.example.reelstudio.data.model

import androidx.compose.ui.graphics.Color
import com.example.reelstudio.ui.theme.Amber
import com.example.reelstudio.ui.theme.AmberLight
import com.example.reelstudio.ui.theme.Coral
import com.example.reelstudio.ui.theme.CoralLight
import com.example.reelstudio.ui.theme.Mint
import com.example.reelstudio.ui.theme.MintLight
import com.example.reelstudio.ui.theme.Plum
import com.example.reelstudio.ui.theme.PlumLight
import com.example.reelstudio.ui.theme.Sky
import com.example.reelstudio.ui.theme.SkyLight
import com.example.reelstudio.ui.theme.Violet
import com.example.reelstudio.ui.theme.VioletLight

enum class ProjectStatus(val labelPt: String) {
    DRAFT("Rascunho"),
    RENDERING("Renderizando"),
    READY("Pronto");

    companion object {
        fun fromString(str: String): ProjectStatus = when (str.lowercase()) {
            "ready" -> READY
            "rendering" -> RENDERING
            else -> DRAFT
        }
    }
}

data class PresetInfo(
    val name: String,
    val meta: String,
    val colorName: String,
    val icon: String,
    val description: String = ""
)

data class VoiceInfo(
    val id: String,
    val name: String,
    val role: String,
    val language: String,
    val colorName: String,
    val initials: String,
    val isCustomClone: Boolean = false,
    val engine: String = "Cartesia Sonic 2.0",
    val naturalness: String = "99.8%",
    val isRobotic: Boolean = false,
    val style: String = "Conversacional Realista"
)

data class StatItem(
    val label: String,
    val value: String,
    val delta: String
)

data class ActivityItem(
    val label: String,
    val action: String,
    val time: String,
    val colorName: String
)

object ReelDefaults {
    val presets = listOf(
        PresetInfo("Lançamento de produto", "Revelação marcante · 9:16", "coral", "✦", "Estrutura rápida para apresentar produtos e features com impacto visual"),
        PresetInfo("Impacto do criador", "Cortes rápidos · 9:16", "violet", "✺", "Ideal para criadores construindo audiência com autoridade e energia"),
        PresetInfo("Explicador editorial", "História limpa · 16:9", "sky", "▤", "Narrativa clara e espaçosa para ensinar conceitos ou cases de sucesso"),
        PresetInfo("História com dados", "Gráficos e números · 1:1", "mint", "⌁", "Formato focado em insights, métricas e comprovação empírica"),
        PresetInfo("Demo para desenvolvedores", "Demonstração do produto · 16:9", "amber", "⌘", "Walkthrough de código, terminais e produtos técnicos"),
        PresetInfo("Marca cinematográfica", "Atmosférico · 9:16", "plum", "◒", "Estética sofisticada para manifestos de marca e anúncios")
    )

    val voices = listOf(
        VoiceInfo("sofia", "Sofia", "Conversacional · acolhedora · respiração natural", "Português (BR)", "mint", "SO", style = "Conversacional"),
        VoiceInfo("lucas", "Lucas", "Narrativa dinâmica · autoridade e clareza", "Português (BR)", "coral", "LU", style = "Narrativo"),
        VoiceInfo("beatriz", "Beatriz", "Expressiva · ritmo ágil para redes sociais", "Português (BR)", "violet", "BE", style = "Entusiasta"),
        VoiceInfo("gabriel", "Gabriel", "Cinematográfico · tom grave e envolvente", "Português (BR)", "amber", "GA", style = "Documentário"),
        VoiceInfo("maya", "Maya", "Acolhedora · confiante · dicção impecável", "Inglês (Global)", "sky", "MA", style = "Global"),
        VoiceInfo("theo", "Theo", "Energético · clareza e ritmo comercial", "Inglês (Reino Unido)", "plum", "TH", style = "Tech Demo"),
        VoiceInfo("elena", "Elena", "Suave · cinematográfica · tom caloroso", "Espanhol (LatAm)", "coral", "EL", style = "Editorial")
    )

    val soundtracks = listOf(
        "Brilho ambiente",
        "Lo-fi tranquilo",
        "Ritmo acelerado",
        "Tensão cinematográfica"
    )

    val formatOptions = listOf("9:16 Portrait", "16:9 Landscape", "1:1 Square")
    val engineOptions = listOf("Remotion", "FFmpeg", "HyperFrames")
    val captionStyles = listOf("Karaoke", "Minimal", "Editorial", "Cinematic")
    val transitionOptions = listOf("Dissolve", "Cut", "Slide", "Whip")
    val mediaTreatments = listOf("Gradient", "Video", "Image", "None")

    val sampleStats = listOf(
        StatItem("Projetos este mês", "18", "+24%"),
        StatItem("Minutos renderizados", "42.8", "+12%"),
        StatItem("Conclusão média", "78%", "+8%")
    )

    val recentActivity = listOf(
        ActivityItem("Lançamento de produto / Aurora", "render concluído", "2 min atrás", "coral"),
        ActivityItem("Por que equipes entregam mais rápido", "render iniciado", "Ontem", "mint"),
        ActivityItem("História da fundadora — do zero", "cena 03 atualizada", "Ontem", "violet")
    )

    val platformLabels = listOf("Reels do Instagram", "Shorts do YouTube", "TikTok", "LinkedIn")

    fun getColor(colorName: String): Color = when (colorName.lowercase()) {
        "coral" -> Coral
        "violet" -> Violet
        "mint" -> Mint
        "sky" -> Sky
        "amber" -> Amber
        "plum" -> Plum
        else -> Coral
    }

    fun getLightColor(colorName: String): Color = when (colorName.lowercase()) {
        "coral" -> CoralLight
        "violet" -> VioletLight
        "mint" -> MintLight
        "sky" -> SkyLight
        "amber" -> AmberLight
        "plum" -> PlumLight
        else -> CoralLight
    }
}
