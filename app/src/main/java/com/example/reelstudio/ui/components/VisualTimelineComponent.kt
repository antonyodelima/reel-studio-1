package com.example.reelstudio.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
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
import com.example.reelstudio.data.local.SceneEntity
import com.example.reelstudio.data.model.ReelDefaults
import com.example.reelstudio.ui.theme.BorderLight
import com.example.reelstudio.ui.theme.Coral
import com.example.reelstudio.ui.theme.CoralLight
import com.example.reelstudio.ui.theme.Ink
import com.example.reelstudio.ui.theme.Mint
import com.example.reelstudio.ui.theme.Muted
import com.example.reelstudio.ui.theme.PaperCard
import com.example.reelstudio.ui.theme.PaperDark
import com.example.reelstudio.ui.theme.Violet
import com.example.reelstudio.ui.theme.VioletLight

@Composable
fun VisualTimelineComponent(
    scenes: List<SceneEntity>,
    selectedClipIndex: Int,
    isPlaying: Boolean,
    onSelectClip: (Int) -> Unit,
    onMoveClipLeft: (Int) -> Unit,
    onMoveClipRight: (Int) -> Unit,
    onTrimClip: (sceneId: Long, startSec: Float, endSec: Float) -> Unit,
    onDuplicateClip: (Int) -> Unit,
    onDeleteClip: (Long) -> Unit,
    onSplitClip: (Int) -> Unit
) {
    var showTrimEditor by remember { mutableStateOf(true) }
    val selectedScene = scenes.getOrNull(selectedClipIndex) ?: scenes.firstOrNull()

    val totalDurationSec = scenes.sumOf { scene ->
        (scene.trimEndSeconds - scene.trimStartSeconds).coerceAtLeast(0.5f).toDouble()
    }
    val totalMins = (totalDurationSec / 60).toInt()
    val totalSecs = (totalDurationSec % 60).toInt()
    val totalDurationStr = String.format("%02d:%02d", totalMins, totalSecs)

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = PaperCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderLight, BorderLight))),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("visual_timeline_component")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row with Title and Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CoralLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timeline,
                            contentDescription = null,
                            tint = Coral,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "LINHA DO TEMPO VISUAL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 9.sp,
                                letterSpacing = 1.2.sp
                            ),
                            color = Coral,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Reordenar & Aparar Clipes",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Total Timeline Duration Badge
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Ink)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Total: $totalDurationStr",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = Color.White,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Toolbar for active clip: Reorder, Trim, Split, Duplicate, Delete
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BorderLight)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left / Right Reorder buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onMoveClipLeft(selectedClipIndex) },
                        enabled = selectedClipIndex > 0,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Mover para a esquerda",
                            tint = if (selectedClipIndex > 0) Ink else Muted.copy(alpha = 0.4f),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = "Reordenar",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = Ink
                    )

                    IconButton(
                        onClick = { onMoveClipRight(selectedClipIndex) },
                        enabled = selectedClipIndex < scenes.size - 1,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Mover para a direita",
                            tint = if (selectedClipIndex < scenes.size - 1) Ink else Muted.copy(alpha = 0.4f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Quick Action buttons
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Toggle Trim
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (showTrimEditor) CoralLight else Color.Transparent)
                            .clickable { showTrimEditor = !showTrimEditor }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ContentCut,
                                contentDescription = "Aparar",
                                tint = if (showTrimEditor) Coral else Muted,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Aparar",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = if (showTrimEditor) Coral else Muted
                            )
                        }
                    }

                    // Duplicate
                    IconButton(
                        onClick = { onDuplicateClip(selectedClipIndex) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Duplicar clipe",
                            tint = Ink,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    // Split
                    IconButton(
                        onClick = { onSplitClip(selectedClipIndex) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCut,
                            contentDescription = "Dividir clipe",
                            tint = Violet,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    // Delete
                    IconButton(
                        onClick = {
                            selectedScene?.let { onDeleteClip(it.id) }
                        },
                        enabled = scenes.size > 1,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Excluir clipe",
                            tint = if (scenes.size > 1) Coral else Muted.copy(alpha = 0.4f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filmstrip Container
            FilmstripTrackView(
                scenes = scenes,
                selectedClipIndex = selectedClipIndex,
                isPlaying = isPlaying,
                onSelectClip = onSelectClip
            )

            // Trimming Controls Panel
            AnimatedVisibility(
                visible = showTrimEditor && selectedScene != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                selectedScene?.let { scene ->
                    ClipTrimEditorPanel(
                        scene = scene,
                        onTrimChanged = { start, end ->
                            onTrimClip(scene.id, start, end)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FilmstripTrackView(
    scenes: List<SceneEntity>,
    selectedClipIndex: Int,
    isPlaying: Boolean,
    onSelectClip: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(PaperDark)
            .padding(vertical = 8.dp, horizontal = 6.dp)
    ) {
        Column {
            // Top Filmstrip Perforation markings
            FilmstripPerforations()

            Spacer(modifier = Modifier.height(6.dp))

            // Scrollable clips track
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                scenes.forEachIndexed { index, scene ->
                    val isSelected = index == selectedClipIndex
                    val accentColor = ReelDefaults.getColor(scene.accent)
                    val trimmedDuration = (scene.trimEndSeconds - scene.trimStartSeconds).coerceAtLeast(0.5f)
                    val durationStr = String.format("%.1fs", trimmedDuration)

                    // Clip Card on the timeline
                    Box(
                        modifier = Modifier
                            .width((80 + (trimmedDuration * 12)).coerceIn(85f, 160f).dp)
                            .height(76.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        accentColor.copy(alpha = if (isSelected) 0.95f else 0.55f),
                                        PaperDark
                                    )
                                )
                            )
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) Coral else Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { onSelectClip(index) }
                            .padding(6.dp)
                    ) {
                        // Header: Index & duration
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = String.format("%02d", index + 1),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 8.sp
                                    ),
                                    color = Color.White
                                )
                            }

                            Text(
                                text = durationStr,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                ),
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }

                        // Title in the middle
                        Text(
                            text = scene.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = Color.White,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(top = 10.dp)
                        )

                        // Trim indicators at borders
                        if (scene.trimStartSeconds > 0.1f || scene.trimEndSeconds < scene.originalDurationSeconds - 0.1f) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Coral)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "✂",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Bottom Filmstrip Perforation markings
            FilmstripPerforations()
        }
    }
}

@Composable
private fun FilmstripPerforations() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        repeat(18) {
            Box(
                modifier = Modifier
                    .size(width = 8.dp, height = 4.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(Color.White.copy(alpha = 0.2f))
            )
        }
    }
}

@Composable
private fun ClipTrimEditorPanel(
    scene: SceneEntity,
    onTrimChanged: (start: Float, end: Float) -> Unit
) {
    val originalMax = scene.originalDurationSeconds.coerceAtLeast(scene.durationSeconds.toFloat()).coerceAtLeast(3.0f)
    var currentRange by remember(scene.id, scene.trimStartSeconds, scene.trimEndSeconds) {
        mutableStateOf(scene.trimStartSeconds..scene.trimEndSeconds.coerceAtMost(originalMax))
    }

    val trimmedDuration = (currentRange.endInclusive - currentRange.start).coerceAtLeast(0.5f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(BorderLight)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ContentCut,
                    contentDescription = null,
                    tint = Coral,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Aparar clipe: ${scene.label}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ),
                    color = Ink
                )
            }

            Text(
                text = String.format("%.1fs (original: %.1fs)", trimmedDuration, originalMax),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp
                ),
                color = Coral
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Visual In-Out Handle Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(PaperDark.copy(alpha = 0.1f))
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "[ In: " + String.format("%.1fs", currentRange.start),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    color = Coral
                )
                Text(
                    text = "Out: " + String.format("%.1fs", currentRange.endInclusive) + " ]",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    color = Coral
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Range Slider for Trimming Head & Tail
        RangeSlider(
            value = currentRange,
            onValueChange = { range ->
                val newStart = range.start.coerceAtLeast(0f)
                val newEnd = range.endInclusive.coerceAtMost(originalMax)
                if (newEnd - newStart >= 0.5f) {
                    currentRange = newStart..newEnd
                    onTrimChanged(newStart, newEnd)
                }
            },
            valueRange = 0f..originalMax,
            colors = SliderDefaults.colors(
                thumbColor = Coral,
                activeTrackColor = Coral,
                inactiveTrackColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("trim_range_slider")
        )

        // Quick Preset Buttons (Reset, -1s tail, -1s head)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(PaperCard)
                    .clickable {
                        currentRange = 0f..originalMax
                        onTrimChanged(0f, originalMax)
                    }
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Replay,
                        contentDescription = null,
                        tint = Muted,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Resetar corte",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                        color = Muted
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(PaperCard)
                    .clickable {
                        val end = (currentRange.endInclusive - 1.0f).coerceAtLeast(currentRange.start + 0.5f)
                        currentRange = currentRange.start..end
                        onTrimChanged(currentRange.start, end)
                    }
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "Aparar fim -1s",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                    color = Ink
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(PaperCard)
                    .clickable {
                        val start = (currentRange.start + 1.0f).coerceAtMost(currentRange.endInclusive - 0.5f)
                        currentRange = start..currentRange.endInclusive
                        onTrimChanged(start, currentRange.endInclusive)
                    }
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "Aparar início +1s",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                    color = Ink
                )
            }
        }
    }
}
