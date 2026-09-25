package com.example.reelstudio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reelstudio.ui.theme.BorderLight
import com.example.reelstudio.ui.theme.Coral
import com.example.reelstudio.ui.theme.CoralLight
import com.example.reelstudio.ui.theme.Ink
import com.example.reelstudio.ui.theme.Mint
import com.example.reelstudio.ui.theme.MintLight
import com.example.reelstudio.ui.theme.Muted
import com.example.reelstudio.ui.theme.PaperCard
import com.example.reelstudio.ui.theme.PaperDark

/**
 * Componente UI de Range Slider simples e responsivo para aparar vídeos (Video Trimming),
 * permitindo definir o ponto de início (start) e fim (end) com precisão visual.
 */
@Composable
fun VideoTrimmingRangeSlider(
    startSeconds: Float,
    endSeconds: Float,
    maxDurationSeconds: Float,
    minClipDurationSeconds: Float = 0.5f,
    onTrimChange: (startSec: Float, endSec: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val safeMax = maxDurationSeconds.coerceAtLeast(1.0f)
    var sliderPosition by remember(startSeconds, endSeconds, safeMax) {
        val safeStart = startSeconds.coerceIn(0f, safeMax - minClipDurationSeconds)
        val safeEnd = endSeconds.coerceIn(safeStart + minClipDurationSeconds, safeMax)
        mutableStateOf(safeStart..safeEnd)
    }

    val currentDuration = (sliderPosition.endInclusive - sliderPosition.start).coerceAtLeast(minClipDurationSeconds)

    fun formatSeconds(sec: Float): String {
        val totalSec = sec.coerceAtLeast(0f)
        val minutes = (totalSec / 60).toInt()
        val seconds = totalSec % 60
        return String.format("%02d:%04.1fs", minutes, seconds)
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = PaperCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(BorderLight, BorderLight))
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("video_trimming_range_slider_container")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Labels e duração
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(CoralLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCut,
                            contentDescription = "Aparar vídeo",
                            tint = Coral,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "APARAR CENA",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            fontSize = 10.sp
                        ),
                        color = Ink
                    )
                }

                // Badge de duração resultante
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MintLight)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Corte: ${String.format("%.1fs", currentDuration)} / ${String.format("%.1fs", safeMax)}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp
                        ),
                        color = Mint,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Timestamps: Início e Fim
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ponto inicial
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = BorderLight,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Muted,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Início: ${formatSeconds(sliderPosition.start)}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = Ink,
                            maxLines = 1
                        )
                    }
                }

                // Ponto final
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = BorderLight,
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Muted,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Fim: ${formatSeconds(sliderPosition.endInclusive)}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = Coral,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Trilha visual do vídeo (frame strip decorativa)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(PaperDark)
                    .border(1.dp, BorderLight, RoundedCornerShape(6.dp))
            ) {
                // Faixa ativa iluminada
                val startFraction = (sliderPosition.start / safeMax).coerceIn(0f, 1f)
                val endFraction = (sliderPosition.endInclusive / safeMax).coerceIn(startFraction, 1f)

                Row(modifier = Modifier.fillMaxWidth()) {
                    if (startFraction > 0f) {
                        Spacer(modifier = Modifier.weight(startFraction.coerceAtLeast(0.001f)))
                    }
                    Box(
                        modifier = Modifier
                            .weight((endFraction - startFraction).coerceAtLeast(0.001f))
                            .height(20.dp)
                            .background(Coral.copy(alpha = 0.35f))
                            .border(1.5.dp, Coral, RoundedCornerShape(4.dp))
                    )
                    if (1f - endFraction > 0f) {
                        Spacer(modifier = Modifier.weight((1f - endFraction).coerceAtLeast(0.001f)))
                    }
                }
            }

            // Material 3 Range Slider interativo
            RangeSlider(
                value = sliderPosition,
                onValueChange = { newRange ->
                    val rawStart = newRange.start
                    val rawEnd = newRange.endInclusive
                    val safeStart: Float
                    val safeEnd: Float

                    if (rawEnd - rawStart < minClipDurationSeconds) {
                        safeStart = rawStart
                        safeEnd = (rawStart + minClipDurationSeconds).coerceAtMost(safeMax)
                    } else {
                        safeStart = rawStart.coerceIn(0f, safeMax - minClipDurationSeconds)
                        safeEnd = rawEnd.coerceIn(safeStart + minClipDurationSeconds, safeMax)
                    }

                    sliderPosition = safeStart..safeEnd
                    onTrimChange(safeStart, safeEnd)
                },
                valueRange = 0f..safeMax,
                colors = SliderDefaults.colors(
                    thumbColor = Coral,
                    activeTrackColor = Coral,
                    inactiveTrackColor = BorderLight
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("video_trim_range_slider")
            )

            // Marcadores de tempo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "00:00.0s",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = Muted
                )
                Text(
                    text = formatSeconds(safeMax / 2),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = Muted
                )
                Text(
                    text = formatSeconds(safeMax),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = Muted
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Presets Rápidos de Corte
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Preset: Primeiros 3s
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BorderLight,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    TextButton(
                        onClick = {
                            val newEnd = 3.0f.coerceAtMost(safeMax)
                            sliderPosition = 0f..newEnd
                            onTrimChange(0f, newEnd)
                        },
                        contentPadding = ButtonDefaults.TextButtonContentPadding,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "0s - 3s",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Ink,
                            maxLines = 1
                        )
                    }
                }

                // Preset: Metade 1
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BorderLight,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    TextButton(
                        onClick = {
                            val mid = (safeMax / 2).coerceAtLeast(minClipDurationSeconds)
                            sliderPosition = 0f..mid
                            onTrimChange(0f, mid)
                        },
                        contentPadding = ButtonDefaults.TextButtonContentPadding,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "1ª Metade",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Ink,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Preset: Resetar Completo
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Coral.copy(alpha = 0.1f),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    TextButton(
                        onClick = {
                            sliderPosition = 0f..safeMax
                            onTrimChange(0f, safeMax)
                        },
                        contentPadding = ButtonDefaults.TextButtonContentPadding,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = null,
                            tint = Coral,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "Total",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Coral,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
