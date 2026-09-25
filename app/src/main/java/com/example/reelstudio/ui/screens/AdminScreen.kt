package com.example.reelstudio.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reelstudio.ui.theme.Amber
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
import com.example.reelstudio.ui.viewmodel.AdminViewModel
import com.example.reelstudio.util.SpeechHelper

@Composable
fun AdminScreen(
    viewModel: AdminViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val speechHelper = remember(context) { SpeechHelper(context) }
    DisposableEffect(Unit) {
        onDispose { speechHelper.shutdown() }
    }

    LaunchedEffect(Unit) {
        viewModel.calculateCacheSize(context)
    }

    val projects by viewModel.allProjects.collectAsState()
    val clones by viewModel.allVoiceClones.collectAsState()
    val activeEngine by viewModel.activeEngine.collectAsState()
    val maxResolution by viewModel.maxResolution.collectAsState()
    val exportFps by viewModel.exportFps.collectAsState()
    val gpuAcceleration by viewModel.gpuAcceleration.collectAsState()
    val antiRoboticFilter by viewModel.antiRoboticFilter.collectAsState()
    val cartesiaModel by viewModel.cartesiaModel.collectAsState()
    val renderConcurrency by viewModel.renderConcurrency.collectAsState()
    val cacheSizeBytes by viewModel.cacheSizeBytes.collectAsState()
    val isStressTesting by viewModel.isStressTesting.collectAsState()
    val stressProgress by viewModel.stressTestProgress.collectAsState()
    val systemLogs by viewModel.systemLogs.collectAsState()

    var selectedAdminSection by remember { mutableStateOf("Pipeline") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header Admin Superior
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
            color = PaperDark,
            shadowElevation = 3.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                            .testTag("admin_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "AMBIENTE DE CONTROLE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 9.sp,
                                    letterSpacing = 1.2.sp
                                ),
                                color = Coral
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "ADMIN",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 8.sp
                                    ),
                                    color = Color.White
                                )
                            }
                        }
                        Text(
                            text = "Console Central do Estúdio",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp
                            ),
                            color = Color.White
                        )
                    }
                }

                // Badge de Status do Ambiente
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MintLight.copy(alpha = 0.25f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Mint)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "ONLINE · 82ms",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 9.sp
                            ),
                            color = Mint
                        )
                    }
                }
            }
        }

        // Conteúdo com rolagem
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
            }

            // KPIs em Tempo Real
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Projetos
                    AdminKpiCard(
                        title = "Projetos",
                        value = projects.size.toString(),
                        subtitle = "Banco Room local",
                        color = Coral,
                        modifier = Modifier.weight(1f)
                    )
                    // Clones
                    AdminKpiCard(
                        title = "Vozes Clones",
                        value = clones.size.toString(),
                        subtitle = "Cartesia sonic",
                        color = Violet,
                        modifier = Modifier.weight(1f)
                    )
                    // Cache
                    val cacheMb = cacheSizeBytes / (1024 * 1024)
                    AdminKpiCard(
                        title = "Cache Vídeo",
                        value = "${cacheMb}MB",
                        subtitle = "Espaço ocupado",
                        color = Mint,
                        modifier = Modifier.weight(1f)
                    )
                    // Sucesso
                    AdminKpiCard(
                        title = "Pipeline",
                        value = "99.8%",
                        subtitle = "Render success",
                        color = Amber,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Seletor de Seções Administrativas
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(BorderLight)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Pipeline", "Vozes Cartesia", "Manutenção", "Logs").forEach { section ->
                        val isSelected = selectedAdminSection == section
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Ink else Color.Transparent)
                                .clickable { selectedAdminSection = section }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = section,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = if (isSelected) Color.White else Muted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // SEÇÃO 1: PIPELINE & MOTORES DE RENDER
            if (selectedAdminSection == "Pipeline") {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = PaperCard),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderLight, BorderLight))),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Movie, contentDescription = null, tint = Coral, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Motores de Renderização do Estúdio",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, fontSize = 14.sp),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))

                            // Seleção de Motor
                            Text("Motor de Composição:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Muted)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Remotion 4.0", "FFmpeg Core", "HyperFrames").forEach { engine ->
                                    val isSelected = activeEngine.contains(engine.take(6))
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) CoralLight else BorderLight)
                                            .border(1.dp, if (isSelected) Coral else Color.Transparent, RoundedCornerShape(10.dp))
                                            .clickable { viewModel.setEngine(engine) }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = engine,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                                fontSize = 10.sp
                                            ),
                                            color = if (isSelected) Coral else Ink,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Resolução Máxima
                            Text("Resolução de Saída Padrão:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Muted)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("1080p (Full HD)", "4K Ultra", "720p Econômico").forEach { res ->
                                    val isSelected = maxResolution == res
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) Ink else BorderLight)
                                            .clickable { viewModel.setResolution(res) }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = res.split(" ").first(),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            ),
                                            color = if (isSelected) Color.White else Ink
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Taxa de FPS
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Taxa de Quadros (FPS)", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp))
                                    Text("Maior fluidez para vídeos em movimento rápido", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = Muted)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf(30, 60).forEach { fps ->
                                        val isSelected = exportFps == fps
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSelected) Coral else BorderLight)
                                                .clickable { viewModel.setFps(fps) }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = "${fps} FPS",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp
                                                ),
                                                color = if (isSelected) Color.White else Ink
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = BorderLight)
                            Spacer(modifier = Modifier.height(12.dp))

                            // GPU Hardware Switch
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                    Text("Aceleração por Hardware (GPU / MediaCodec)", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp))
                                    Text("Renderização acelerada usando codecs nativos do aparelho.", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = Muted)
                                }
                                Switch(
                                    checked = gpuAcceleration,
                                    onCheckedChange = { viewModel.toggleGpu() },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Coral)
                                )
                            }
                        }
                    }
                }
            }

            // SEÇÃO 2: VOZES & IA CARTESIA (REMOÇÃO DE VOZES ROBÓTICAS)
            if (selectedAdminSection == "Vozes Cartesia") {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = PaperCard),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderLight, BorderLight))),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Mic, contentDescription = null, tint = Violet, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Motor Vocal Cartesia Sonic 2.0",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, fontSize = 14.sp),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            // Banner Anti-Robótico
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(MintLight)
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Security, contentDescription = null, tint = Mint, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Filtro Anti-Robótico Ativado",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 11.sp),
                                            color = Mint
                                        )
                                        Text(
                                            text = "Vozes metálicas ou monótonas são substituídas por prosódia dinâmica e respiração humana.",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                            color = Ink.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Switch do Filtro Anti-Robótico
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                    Text("Prosódia Orgânica & Eliminação Robótica", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp))
                                    Text("Modula entonação e pausas realistas em português e inglês.", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = Muted)
                                }
                                Switch(
                                    checked = antiRoboticFilter,
                                    onCheckedChange = { viewModel.toggleAntiRoboticFilter() },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Mint)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = BorderLight)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Teste Rápido de Síntese Cartesia
                            Text("Teste de Fala Humana (Prévia Imediata):", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Muted)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        speechHelper.speak(
                                            text = "Olá criador! Essa é a voz Sofia da Cartesia Sonic. Prosódia 100% natural, sem tom robótico.",
                                            voiceName = "Sofia"
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Coral),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Voz Sofia", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        speechHelper.speak(
                                            text = "Fala aí! Aqui é o Lucas. Narrador dinâmico em português com clareza e ritmo jovem.",
                                            voiceName = "Lucas"
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Violet),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Voz Lucas", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // SEÇÃO 3: MANUTENÇÃO & TESTE DE CARGA
            if (selectedAdminSection == "Manutenção") {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = PaperCard),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderLight, BorderLight))),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CleaningServices, contentDescription = null, tint = Amber, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Ferramentas de Manutenção e Dados",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, fontSize = 14.sp),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))

                            // Limpar Cache
                            Button(
                                onClick = {
                                    viewModel.clearCache(context) { bytes ->
                                        Toast.makeText(context, "Cache limpo: ${bytes / 1024} KB liberados", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BorderLight, contentColor = Ink),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Limpar Cache de Renderização e MP4 Temporários", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Povoar Projetos Demo
                            Button(
                                onClick = {
                                    viewModel.seedDemoProjects {
                                        Toast.makeText(context, "3 Projetos Demo criados com sucesso!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = VioletLight, contentColor = Violet),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Povoar Estúdio com 3 Projetos de Demonstração", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Teste de Estresse do Pipeline
                            Button(
                                onClick = {
                                    viewModel.runStressTest {
                                        Toast.makeText(context, "Teste de estresse finalizado com sucesso!", Toast.LENGTH_LONG).show()
                                    }
                                },
                                enabled = !isStressTesting,
                                colors = ButtonDefaults.buttonColors(containerColor = Coral),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Speed, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isStressTesting) "Executando Teste de Carga ($stressProgress%)..." else "Executar Teste de Estresse de Render",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            if (isStressTesting) {
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { stressProgress / 100f },
                                    color = Coral,
                                    trackColor = BorderLight,
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape)
                                )
                            }
                        }
                    }
                }
            }

            // SEÇÃO 4: CONSOLE DE LOGS DO ESTÚDIO
            if (selectedAdminSection == "Logs") {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = PaperDark),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Terminal, contentDescription = null, tint = Mint, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Console de Telemetria & Logs", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White))
                                }

                                Button(
                                    onClick = {
                                        val text = systemLogs.joinToString("\n") { "[${it.timestamp}] [${it.level}] ${it.message}" }
                                        clipboardManager.setText(AnnotatedString(text))
                                        Toast.makeText(context, "Logs copiados para a área de transferência!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = ButtonDefaults.TextButtonContentPadding
                                ) {
                                    Text("Copiar Logs", fontSize = 10.sp, color = Color.White)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Black.copy(alpha = 0.4f))
                                    .padding(10.dp)
                            ) {
                                LazyColumn(modifier = Modifier.fillMaxSize()) {
                                    items(systemLogs) { log ->
                                        val levelColor = when (log.level) {
                                            "SUCCESS" -> Mint
                                            "WARN" -> Amber
                                            else -> Coral
                                        }
                                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                            Text(
                                                text = "${log.timestamp} ",
                                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
                                                color = Color.White.copy(alpha = 0.5f)
                                            )
                                            Text(
                                                text = "[${log.level}] ",
                                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                                color = levelColor
                                            )
                                            Text(
                                                text = log.message,
                                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
                                                color = Color.White.copy(alpha = 0.85f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun AdminKpiCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PaperCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderLight, BorderLight))),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 8.sp,
                    letterSpacing = 0.8.sp
                ),
                color = Muted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                ),
                color = color,
                maxLines = 1
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp),
                color = Muted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
