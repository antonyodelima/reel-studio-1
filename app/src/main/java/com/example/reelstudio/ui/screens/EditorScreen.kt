package com.example.reelstudio.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.reelstudio.data.local.SceneEntity
import com.example.reelstudio.data.model.ReelDefaults
import com.example.reelstudio.ui.components.VisualTimelineComponent
import com.example.reelstudio.util.SpeechHelper
import com.example.reelstudio.ui.theme.BorderLight
import com.example.reelstudio.ui.theme.Coral
import com.example.reelstudio.ui.theme.CoralLight
import com.example.reelstudio.ui.theme.Ink
import com.example.reelstudio.ui.theme.Mint
import com.example.reelstudio.ui.theme.MintLight
import com.example.reelstudio.ui.theme.Muted
import com.example.reelstudio.ui.theme.PaperCard
import com.example.reelstudio.ui.theme.PaperDark
import com.example.reelstudio.ui.theme.Violet
import com.example.reelstudio.ui.theme.VioletLight
import com.example.reelstudio.ui.viewmodel.EditorViewModel
import com.example.reelstudio.util.ExportResult

@Composable
fun EditorScreen(
    viewModel: EditorViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val project by viewModel.project.collectAsState()
    val scenes by viewModel.scenes.collectAsState()
    val activeIndex by viewModel.activeSceneIndex.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val renderProgress by viewModel.renderProgress.collectAsState()

    var showRenderModal by remember { mutableStateOf(false) }

    val speechHelper = remember(context) { SpeechHelper(context) }
    DisposableEffect(Unit) {
        onDispose {
            speechHelper.shutdown()
        }
    }

    val activeScene = scenes.getOrNull(activeIndex) ?: scenes.firstOrNull()
    val exportResult by viewModel.exportResult.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar
        item {
            Spacer(modifier = Modifier.height(4.dp))
            EditorHeader(
                projectName = project?.name ?: "Reel sem título",
                preset = project?.preset ?: "Lançamento de produto",
                sceneCount = scenes.size,
                onNameChange = { viewModel.updateProjectName(it) },
                onBack = onBack,
                onSave = {
                    viewModel.saveProjectNow {
                        Toast.makeText(context, "Projeto salvo no banco de dados local!", Toast.LENGTH_SHORT).show()
                    }
                },
                onProduce = { showRenderModal = true },
                onExportMp4 = {
                    viewModel.exportSingleMp4(context) { result ->
                        Toast.makeText(context, "MP4 exportado com sucesso: ${result.file.name}", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        // Production Stages
        item {
            ProductionStagesBar(
                selectedTab = selectedTab,
                onSelectStage = { stageIndex ->
                    when (stageIndex) {
                        0, 1 -> viewModel.setTab("Cenas")
                        2 -> viewModel.setTab("Legendas")
                        3 -> viewModel.setTab("Áudio")
                    }
                }
            )
        }

        // Live Canvas 9:16 Preview Player
        item {
            activeScene?.let { scene ->
                ReelPlayerCanvas(
                    scene = scene,
                    sceneIndex = activeIndex,
                    totalScenes = scenes.size,
                    isPlaying = isPlaying,
                    onTogglePlay = { viewModel.togglePlay() }
                )
            }
        }

        // Visual Timeline Component for reordering and trimming clips
        item {
            VisualTimelineComponent(
                scenes = scenes,
                selectedClipIndex = activeIndex,
                isPlaying = isPlaying,
                onSelectClip = { viewModel.selectScene(it) },
                onMoveClipLeft = { viewModel.moveClipLeft(it) },
                onMoveClipRight = { viewModel.moveClipRight(it) },
                onTrimClip = { id, s, e -> viewModel.trimClip(id, s, e) },
                onDuplicateClip = { viewModel.duplicateClip(it) },
                onDeleteClip = { viewModel.deleteScene(it) },
                onSplitClip = { viewModel.splitClip(it) }
            )
        }

        // Scrubbable Timeline
        item {
            TimelineBar(
                scenes = scenes,
                activeIndex = activeIndex,
                onSelectScene = { viewModel.selectScene(it) }
            )
        }

        // Storyboard Scenes List
        item {
            Text(
                text = "STORYBOARD (${scenes.size} CENAS)",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    letterSpacing = 1.2.sp
                ),
                color = Coral
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        itemsIndexed(scenes, key = { _, item -> item.id }) { index, scene ->
            SceneListItem(
                scene = scene,
                index = index,
                isSelected = index == activeIndex,
                onSelect = { viewModel.selectScene(index) },
                onDelete = {
                    if (scenes.size > 1) {
                        viewModel.deleteScene(scene.id)
                        Toast.makeText(context, "Cena removida", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "O projeto precisa ter pelo menos 1 cena", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        // Add Scene Button
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = 1.5.dp,
                        color = Coral.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        viewModel.addScene()
                        Toast.makeText(context, "Cena adicionada", Toast.LENGTH_SHORT).show()
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Coral,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Adicionar cena",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        ),
                        color = Coral
                    )
                }
            }
        }

        // Inspector Tabs (Cenas, Legendas, Áudio)
        item {
            InspectorTabsSection(
                selectedTab = selectedTab,
                onSelectTab = { viewModel.setTab(it) },
                viewModel = viewModel,
                activeScene = activeScene,
                speechHelper = speechHelper
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showRenderModal) {
        RenderProductionDialog(
            onDismiss = { showRenderModal = false },
            onStartRender = { format, quality, engine ->
                showRenderModal = false
                viewModel.startProductionRender(format, quality, engine) {
                    Toast.makeText(context, "Render concluído! Arquivos salvos.", Toast.LENGTH_LONG).show()
                }
            },
            onExportSingleMp4 = { format, quality, engine ->
                showRenderModal = false
                viewModel.exportSingleMp4(context, format, quality, engine) { result ->
                    Toast.makeText(context, "MP4 exportado: ${result.sizeFormatted}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    if (renderProgress != null) {
        RenderingProgressDialog(
            progress = renderProgress ?: 0,
            onDismiss = { viewModel.dismissRenderDialog() }
        )
    }

    exportResult?.let { result ->
        ExportSuccessDialog(
            result = result,
            onDismiss = { viewModel.dismissExportResult() },
            onShare = { viewModel.shareExportedMp4(context) },
            onSaveToGallery = {
                val ok = viewModel.saveExportedMp4ToGallery(context)
                if (ok) {
                    Toast.makeText(context, "Vídeo salvo na Galeria / Filmes!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Não foi possível salvar na Galeria", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}

@Composable
private fun EditorHeader(
    projectName: String,
    preset: String,
    sceneCount: Int,
    onNameChange: (String) -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onProduce: () -> Unit,
    onExportMp4: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PaperCard)
                        .testTag("editor_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Ink,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Column(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = projectName,
                        onValueChange = onNameChange,
                        singleLine = true,
                        textStyle = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("editor_project_name_input"),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Coral,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    Text(
                        text = "$preset · 1080 × 1920 · $sceneCount cenas",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = Muted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(PaperCard)
                        .clickable { onSave() }
                        .padding(horizontal = 8.dp, vertical = 7.dp)
                        .testTag("editor_save_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Salvar",
                        tint = Ink,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Violet)
                        .clickable { onExportMp4() }
                        .padding(horizontal = 9.dp, vertical = 7.dp)
                        .testTag("editor_export_mp4_header_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Exportar MP4",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "MP4",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color.White,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }

                Button(
                    onClick = onProduce,
                    colors = ButtonDefaults.buttonColors(containerColor = Coral),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("editor_produce_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Movie,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Produzir",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color.White,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductionStagesBar(
    selectedTab: String,
    onSelectStage: (stageIndex: Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BorderLight)
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val stages = listOf("Planejar", "Escrever", "Projetar", "Produzir")
        stages.forEachIndexed { index, stage ->
            val isActive = when (index) {
                0, 1 -> selectedTab == "Cenas"
                2 -> selectedTab == "Legendas"
                3 -> selectedTab == "Áudio"
                else -> false
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isActive) CoralLight else Color.Transparent)
                    .clickable { onSelectStage(index) }
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${index + 1} $stage",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (isActive) FontWeight.Black else FontWeight.Medium,
                        fontSize = 10.sp
                    ),
                    color = if (isActive) Coral else Muted
                )
            }
        }
    }
}

@Composable
private fun ReelPlayerCanvas(
    scene: SceneEntity,
    sceneIndex: Int,
    totalScenes: Int,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit
) {
    val accentColor = ReelDefaults.getColor(scene.accent)
    val gradient = Brush.linearGradient(
        listOf(
            accentColor,
            accentColor.copy(alpha = 0.75f),
            PaperDark
        )
    )

    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = PaperDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Live Status Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Mint)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Prévia ao vivo",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                Text(
                    text = "${scene.label} · ${scene.duration}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp
                    ),
                    color = Coral
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 9:16 Canvas Box
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .aspectRatio(9f / 16f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(gradient)
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                // Top Watermark & Scene order
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${scene.label} · ${scene.duration}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 8.sp,
                                letterSpacing = 1.sp
                            ),
                            color = Color.White
                        )
                    }

                    Text(
                        text = "REEL STUDIO",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            letterSpacing = 1.sp
                        ),
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }

                // Scene Title / Headline
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 32.dp, height = 3.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = scene.title,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            lineHeight = 24.sp
                        ),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Floating Styled Caption Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black.copy(alpha = 0.35f))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = scene.caption,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Footer metadata
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${String.format("%02d", sceneIndex + 1)} / ${String.format("%02d", totalScenes)}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Northstar Studio",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }

                // Big Play / Pause button
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(PaperCard)
                        .align(Alignment.BottomCenter)
                        .clickable { onTogglePlay() }
                        .testTag("preview_play_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pausar" else "Reproduzir",
                        tint = Ink,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineBar(
    scenes: List<SceneEntity>,
    activeIndex: Int,
    onSelectScene: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = PaperCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderLight, BorderLight))),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TIMELINE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 9.sp,
                        letterSpacing = 1.sp
                    ),
                    color = Muted
                )
                Text(
                    text = "${String.format("00:%02d", (activeIndex + 1) * 5)} / 00:32",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    ),
                    color = Muted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Scene blocks row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(BorderLight)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                scenes.forEachIndexed { index, scene ->
                    val isSelected = index == activeIndex
                    val accentColor = ReelDefaults.getColor(scene.accent)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Coral else PaperCard)
                            .clickable { onSelectScene(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = String.format("%02d", index + 1),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp
                            ),
                            color = if (isSelected) Color.White else Ink
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SceneListItem(
    scene: SceneEntity,
    index: Int,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    val accentColor = ReelDefaults.getColor(scene.accent)
    val gradient = Brush.linearGradient(listOf(accentColor, PaperDark))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) CoralLight else PaperCard)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) Coral else BorderLight,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onSelect() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail
        Box(
            modifier = Modifier
                .size(width = 40.dp, height = 54.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(gradient),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = String.format("%02d", index + 1),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp
                ),
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = scene.label.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 9.sp,
                        letterSpacing = 1.sp
                    ),
                    color = Coral
                )
                if (isSelected) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(Coral)
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = scene.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1
            )
            Text(
                text = "Legenda: ${scene.caption}",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = Muted,
                maxLines = 1
            )
        }

        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(30.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Excluir cena",
                tint = Muted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun InspectorTabsSection(
    selectedTab: String,
    onSelectTab: (String) -> Unit,
    viewModel: EditorViewModel,
    activeScene: SceneEntity?,
    speechHelper: SpeechHelper
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = PaperCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderLight, BorderLight))),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Tabs
            val tabs = listOf("Cenas", "Legendas", "Áudio")
            TabRow(
                selectedTabIndex = tabs.indexOf(selectedTab),
                containerColor = BorderLight,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[tabs.indexOf(selectedTab)]),
                        color = Coral
                    )
                },
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                tabs.forEach { tab ->
                    val isSelected = selectedTab == tab
                    Tab(
                        selected = isSelected,
                        onClick = { onSelectTab(tab) },
                        text = {
                            Text(
                                text = tab,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = if (isSelected) Ink else Muted
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                "Cenas" -> InspectorScenesTab(viewModel, activeScene)
                "Legendas" -> InspectorCaptionsTab(viewModel, activeScene)
                "Áudio" -> InspectorAudioTab(viewModel, activeScene, speechHelper)
            }
        }
    }
}

@Composable
private fun InspectorScenesTab(viewModel: EditorViewModel, activeScene: SceneEntity?) {
    val context = LocalContext.current
    if (activeScene == null) return

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "INSPECTOR DE CENA",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 9.sp,
                        letterSpacing = 1.sp
                    ),
                    color = Muted
                )
                Text(
                    text = activeScene.label,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            IconButton(
                onClick = { viewModel.toggleSceneLock() },
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (activeScene.isLocked) CoralLight else BorderLight)
            ) {
                Icon(
                    imageVector = if (activeScene.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                    contentDescription = if (activeScene.isLocked) "Bloqueado" else "Desbloqueado",
                    tint = if (activeScene.isLocked) Coral else Muted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Title / Caption editor
        Text(
            text = "Texto da cena",
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 11.sp),
            color = Muted
        )
        OutlinedTextField(
            value = activeScene.title,
            onValueChange = { viewModel.updateSceneTitle(it) },
            minLines = 2,
            maxLines = 4,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("inspector_scene_title_input"),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedIndicatorColor = Coral,
                unfocusedIndicatorColor = BorderLight
            )
        )

        // Media treatment
        Text(
            text = "Tratamento de mídia",
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 11.sp),
            color = Muted
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ReelDefaults.mediaTreatments.forEach { media ->
                val isSelected = activeScene.mediaType == media
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) CoralLight else MaterialTheme.colorScheme.background)
                        .border(1.dp, if (isSelected) Coral else BorderLight, RoundedCornerShape(10.dp))
                        .clickable { viewModel.updateSceneMedia(media) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = media,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = if (isSelected) Coral else Ink
                    )
                }
            }
        }

        // Transitions
        Text(
            text = "Transição",
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 11.sp),
            color = Muted
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ReelDefaults.transitionOptions.forEach { transition ->
                val isSelected = activeScene.transition == transition
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) VioletLight else MaterialTheme.colorScheme.background)
                        .border(1.dp, if (isSelected) Violet else BorderLight, RoundedCornerShape(10.dp))
                        .clickable { viewModel.updateSceneTransition(transition) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = transition,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = if (isSelected) Violet else Ink
                    )
                }
            }
        }

        // Scene Duration Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Duração da cena",
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 11.sp),
                color = Muted
            )
            Text(
                text = "${activeScene.durationSeconds}s",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                ),
                color = Coral
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(2, 3, 4, 5, 6, 8, 10).forEach { sec ->
                val isSelected = activeScene.durationSeconds == sec
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Coral else MaterialTheme.colorScheme.background)
                        .border(1.dp, if (isSelected) Coral else BorderLight, RoundedCornerShape(8.dp))
                        .clickable { viewModel.updateSceneDuration(sec) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${sec}s",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = if (isSelected) Color.White else Ink
                    )
                }
            }
        }

        // Regenerate Scene Action
        Button(
            onClick = {
                viewModel.regenerateScene()
                Toast.makeText(context, "Cena regenerada com novo roteiro", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = VioletLight),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Violet,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Regenerar cena",
                fontWeight = FontWeight.Bold,
                color = Violet,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun InspectorCaptionsTab(viewModel: EditorViewModel, activeScene: SceneEntity?) {
    val context = LocalContext.current
    if (activeScene == null) return

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "FAIXA DE LEGENDAS",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Black,
                fontSize = 9.sp,
                letterSpacing = 1.sp
            ),
            color = Muted
        )
        Text(
            text = "Faça cada palavra ter impacto.",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
            color = MaterialTheme.colorScheme.onBackground
        )

        // Caption text editor
        Text(
            text = "Texto da legenda",
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 11.sp),
            color = Muted
        )
        OutlinedTextField(
            value = activeScene.caption,
            onValueChange = { viewModel.updateSceneCaption(it) },
            minLines = 2,
            maxLines = 4,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("inspector_scene_caption_input"),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedIndicatorColor = Coral,
                unfocusedIndicatorColor = BorderLight
            )
        )

        // Caption styles pills
        Text(
            text = "Estilo da legenda",
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 11.sp),
            color = Muted
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ReelDefaults.captionStyles.forEach { style ->
                val isSelected = style == activeScene.captionStyle
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) Ink else MaterialTheme.colorScheme.background)
                        .border(1.dp, if (isSelected) Ink else BorderLight, RoundedCornerShape(10.dp))
                        .clickable { viewModel.updateSceneCaptionStyle(style) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = style,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                            fontSize = 9.sp
                        ),
                        color = if (isSelected) Color.White else Ink
                    )
                }
            }
        }

        // Quick AI caption tools
        Text(
            text = "Ações rápidas de legenda",
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 11.sp),
            color = Muted
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    val words = activeScene.caption.split(" ")
                    val mid = words.size / 2
                    val newCaption = if (words.size > 2) {
                        words.take(mid).joinToString(" ") + "\n" + words.drop(mid).joinToString(" ")
                    } else activeScene.caption
                    viewModel.updateSceneCaption(newCaption)
                    Toast.makeText(context, "Legenda formatada em 2 linhas", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = PaperCard),
                border = ButtonDefaults.outlinedButtonBorder().copy(brush = Brush.linearGradient(listOf(BorderLight, BorderLight))),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
            ) {
                Text("2 Linhas", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Ink)
            }

            Button(
                onClick = {
                    val uppercase = activeScene.caption.uppercase()
                    viewModel.updateSceneCaption(uppercase)
                    Toast.makeText(context, "Destaque em MAIÚSCULAS aplicado", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = PaperCard),
                border = ButtonDefaults.outlinedButtonBorder().copy(brush = Brush.linearGradient(listOf(BorderLight, BorderLight))),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
            ) {
                Text("Destacar", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Ink)
            }

            Button(
                onClick = {
                    val shortCaption = activeScene.title.take(35).trimEnd() + "..."
                    viewModel.updateSceneCaption(shortCaption)
                    Toast.makeText(context, "Legenda resumida", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = PaperCard),
                border = ButtonDefaults.outlinedButtonBorder().copy(brush = Brush.linearGradient(listOf(BorderLight, BorderLight))),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
            ) {
                Text("Resumir", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Ink)
            }
        }
    }
}

@Composable
private fun InspectorAudioTab(
    viewModel: EditorViewModel,
    activeScene: SceneEntity?,
    speechHelper: SpeechHelper
) {
    val selectedVoice by viewModel.selectedVoice.collectAsState()
    val selectedSoundtrack by viewModel.selectedSoundtrack.collectAsState()
    val musicVolume by viewModel.musicVolume.collectAsState()
    val isGeneratingVoice by viewModel.isGeneratingVoice.collectAsState()
    val voiceClones by viewModel.voiceClones.collectAsState()

    val allVoices = listOf("Sofia", "Maya", "Theo", "Nina", "Voz da Marca") + voiceClones.map { it.name }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "NARRAÇÃO E ÁUDIO",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Black,
                fontSize = 9.sp,
                letterSpacing = 1.sp
            ),
            color = Muted
        )
        Text(
            text = "Dê voz à cena ativa.",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
            color = MaterialTheme.colorScheme.onBackground
        )

        // Cartesia Voice selection row
        Text(
            text = "Voz da cena",
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 11.sp),
            color = Muted
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            allVoices.forEach { voice ->
                val isSelected = selectedVoice == voice
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) CoralLight else MaterialTheme.colorScheme.background)
                        .border(1.dp, if (isSelected) Coral else BorderLight, RoundedCornerShape(10.dp))
                        .clickable { viewModel.setVoice(voice) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = voice,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = if (isSelected) Coral else Ink
                    )
                }
            }
        }

        // Generate voice button
        Button(
            onClick = {
                viewModel.generateVoiceForCurrentScene { voice, text ->
                    val pitch = when (voice) {
                        "Sofia" -> 1.1f
                        "Maya" -> 1.25f
                        "Theo" -> 0.85f
                        "Nina" -> 1.05f
                        else -> 1.0f
                    }
                    speechHelper.speak(text, pitch = pitch)
                }
            },
            enabled = !isGeneratingVoice,
            colors = ButtonDefaults.buttonColors(containerColor = Coral),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isGeneratingVoice) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Gerando narração…", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            } else {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Gerar narração da cena ($selectedVoice)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Scene Voice Status & Audio Play button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(BorderLight)
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = null,
                        tint = Coral,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    val voiceLabel = activeScene?.voiceName?.ifBlank { selectedVoice } ?: selectedVoice
                    Text(
                        text = if (activeScene?.voiceStatus == "ready") "Áudio pronto ($voiceLabel)" else "Status: sem áudio gerado",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Ink
                    )
                }

                Button(
                    onClick = {
                        val sceneCaption = activeScene?.caption.orEmpty()
                        val sceneTitle = activeScene?.title.orEmpty()
                        val text = if (sceneCaption.isNotBlank()) sceneCaption else if (sceneTitle.isNotBlank()) sceneTitle else "Prévia de áudio da cena"
                        val pitch = when (selectedVoice) {
                            "Sofia" -> 1.1f
                            "Maya" -> 1.25f
                            "Theo" -> 0.85f
                            "Nina" -> 1.05f
                            else -> 1.0f
                        }
                        speechHelper.speak(text, pitch = pitch)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Ink),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Ouvir",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ouvir", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        // Music Soundtrack
        Text(
            text = "Trilha musical",
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 11.sp),
            color = Muted
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ReelDefaults.soundtracks.forEach { track ->
                val isSelected = selectedSoundtrack == track
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) MintLight else MaterialTheme.colorScheme.background)
                        .border(1.dp, if (isSelected) Mint else BorderLight, RoundedCornerShape(10.dp))
                        .clickable { viewModel.setSoundtrack(track) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = track,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = if (isSelected) Mint else Ink
                    )
                }
            }
        }

        // Music Volume Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Volume da música",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Ink
            )
            Text(
                text = "${musicVolume.toInt()}%",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                color = Coral
            )
        }

        Slider(
            value = musicVolume,
            onValueChange = { viewModel.setMusicVolume(it) },
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(
                thumbColor = Coral,
                activeTrackColor = Coral,
                inactiveTrackColor = BorderLight
            )
        )
    }
}

@Composable
private fun RenderProductionDialog(
    onDismiss: () -> Unit,
    onStartRender: (format: String, quality: String, engine: String) -> Unit,
    onExportSingleMp4: (format: String, quality: String, engine: String) -> Unit
) {
    var selectedFormat by remember { mutableStateOf("9:16 Portrait") }
    var selectedQuality by remember { mutableStateOf("1080p") }
    var selectedEngine by remember { mutableStateOf("Remotion") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxWidth().testTag("render_production_dialog")
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "PRODUÇÃO",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.4.sp
                            ),
                            color = Coral
                        )
                        Text(
                            text = "Exportação de vídeo",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Fechar", tint = Muted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Format choice
                Text("Formato da tela", style = MaterialTheme.typography.labelLarge.copy(fontSize = 11.sp), color = Muted)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ReelDefaults.formatOptions.forEach { format ->
                        val isSelected = selectedFormat == format
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) CoralLight else PaperCard)
                                .border(1.dp, if (isSelected) Coral else BorderLight, RoundedCornerShape(10.dp))
                                .clickable { selectedFormat = format }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = format.split(" ")[0],
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = if (isSelected) Coral else Ink
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quality choice
                Text("Qualidade", style = MaterialTheme.typography.labelLarge.copy(fontSize = 11.sp), color = Muted)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("720p", "1080p", "4K").forEach { qual ->
                        val isSelected = selectedQuality == qual
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Ink else PaperCard)
                                .border(1.dp, BorderLight, RoundedCornerShape(10.dp))
                                .clickable { selectedQuality = qual }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = qual,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = if (isSelected) Color.White else Ink
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Engine choice
                Text("Motor de renderização", style = MaterialTheme.typography.labelLarge.copy(fontSize = 11.sp), color = Muted)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ReelDefaults.engineOptions.forEach { eng ->
                        val isSelected = selectedEngine == eng
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) VioletLight else PaperCard)
                                .border(1.dp, if (isSelected) Violet else BorderLight, RoundedCornerShape(10.dp))
                                .clickable { selectedEngine = eng }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = eng,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = if (isSelected) Violet else Ink
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Estimate box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(BorderLight)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Tempo estimado de exportação", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Ink)
                        Text(text = "~2-3 seg", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black), color = Coral)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Single MP4 Export Button (Primary CUJ)
                Button(
                    onClick = { onExportSingleMp4(selectedFormat, selectedQuality, selectedEngine) },
                    colors = ButtonDefaults.buttonColors(containerColor = Coral),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("export_mp4_button")
                ) {
                    Icon(imageVector = Icons.Default.VideoFile, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Exportar como MP4 único", fontWeight = FontWeight.Black, fontSize = 13.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Full production render button
                Button(
                    onClick = { onStartRender(selectedFormat, selectedQuality, selectedEngine) },
                    colors = ButtonDefaults.buttonColors(containerColor = Ink.copy(alpha = 0.08f), contentColor = Ink),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("submit_render_button")
                ) {
                    Icon(imageVector = Icons.Default.Movie, contentDescription = null, tint = Ink, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Simulação Remotion Completa", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Ink)
                }
            }
        }
    }
}

@Composable
private fun RenderingProgressDialog(
    progress: Int,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { if (progress >= 100) onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(if (progress >= 100) MintLight else CoralLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (progress >= 100) Icons.Default.Check else Icons.Default.Movie,
                        contentDescription = null,
                        tint = if (progress >= 100) Mint else Coral,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (progress >= 100) "Render concluído com sucesso!" else "Compondo e renderizando cenas…",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (progress >= 100) "MP4, SRT e VTT gerados e salvos localmente." else "O worker Remotion está processando legendas e trilha de áudio.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = Muted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                LinearProgressIndicator(
                    progress = { progress / 100f },
                    color = if (progress >= 100) Mint else Coral,
                    trackColor = BorderLight,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "$progress%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    ),
                    color = if (progress >= 100) Mint else Coral
                )

                if (progress >= 100) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Ink),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Fechar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ExportSuccessDialog(
    result: ExportResult,
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    onSaveToGallery: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("export_success_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(MintLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Mint,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "MP4 exportado com sucesso!",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Seu projeto foi compilado em um único arquivo de vídeo MP4 no dispositivo.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = Muted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // Info Box
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PaperCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderLight, BorderLight))),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Arquivo:", style = MaterialTheme.typography.labelSmall, color = Muted)
                            Text(
                                text = result.file.name,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Ink,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.widthIn(max = 160.dp)
                            )
                        }
                        HorizontalDivider(color = BorderLight)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Resolução:", style = MaterialTheme.typography.labelSmall, color = Muted)
                            Text(result.resolution, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Ink)
                        }
                        HorizontalDivider(color = BorderLight)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Duração:", style = MaterialTheme.typography.labelSmall, color = Muted)
                            Text(result.durationFormatted, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Coral)
                        }
                        HorizontalDivider(color = BorderLight)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Tamanho:", style = MaterialTheme.typography.labelSmall, color = Muted)
                            Text(result.sizeFormatted, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Mint)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action buttons
                Button(
                    onClick = onShare,
                    colors = ButtonDefaults.buttonColors(containerColor = Coral),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("share_mp4_button")
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Compartilhar MP4", fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onSaveToGallery,
                    colors = ButtonDefaults.buttonColors(containerColor = Violet),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_gallery_button")
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Salvar na Galeria", fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Ink.copy(alpha = 0.08f), contentColor = Ink),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("close_export_dialog_button")
                ) {
                    Text("Concluir", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

