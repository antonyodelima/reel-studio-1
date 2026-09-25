package com.example.reelstudio.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.reelstudio.data.model.ReelDefaults
import com.example.reelstudio.data.model.VoiceInfo
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
import com.example.reelstudio.ui.viewmodel.VoicesViewModel

@Composable
fun VoicesScreen(
    viewModel: VoicesViewModel
) {
    val context = LocalContext.current
    val voices by viewModel.combinedVoices.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val activeVoiceId by viewModel.activeVoiceId.collectAsState()
    val playingVoiceId by viewModel.playingVoiceId.collectAsState()
    val previewText by viewModel.previewText.collectAsState()

    val speechHelper = remember(context) { SpeechHelper(context) }
    DisposableEffect(Unit) {
        onDispose {
            speechHelper.shutdown()
        }
    }

    // Clone state
    val isRecording by viewModel.isRecording.collectAsState()
    val recordingSeconds by viewModel.recordingSeconds.collectAsState()
    val recordedAudioSample by viewModel.recordedAudioSample.collectAsState()
    val cloneName by viewModel.cloneName.collectAsState()
    val cloneTagline by viewModel.cloneTagline.collectAsState()
    val cloneLanguage by viewModel.cloneLanguage.collectAsState()
    val consentGiven by viewModel.consentGiven.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startRecording()
        } else {
            Toast.makeText(context, "Permissão de microfone necessária para gravar a voz", Toast.LENGTH_SHORT).show()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Header
            Column {
                Text(
                    text = "BIBLIOTECA CARTESIA",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.4.sp
                    ),
                    color = Coral
                )
                Text(
                    text = "Dê voz à sua ideia.",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Escolha uma voz ultra-realista do Cartesia Sonic ou crie um clone privado com amostra de microfone.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = Muted,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Search and Language Bar
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Buscar vozes por nome, tom ou idioma…", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Muted,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("voices_search_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = PaperCard,
                        unfocusedContainerColor = PaperCard,
                        focusedIndicatorColor = Coral,
                        unfocusedIndicatorColor = BorderLight
                    )
                )
            }
        }

        // Voice Cards List
        items(voices, key = { it.id }) { voice ->
            VoiceCardItem(
                voice = voice,
                isActive = activeVoiceId == voice.id,
                isPlaying = playingVoiceId == voice.id,
                onSelect = {
                    viewModel.setActiveVoiceId(voice.id)
                    Toast.makeText(context, "${voice.name} definida como voz padrão", Toast.LENGTH_SHORT).show()
                },
                onTogglePlay = {
                    val willPlay = playingVoiceId != voice.id
                    viewModel.toggleVoicePreview(voice.id)
                    if (willPlay) {
                        val pitch = when (voice.name) {
                            "Sofia" -> 1.1f
                            "Maya" -> 1.25f
                            "Theo" -> 0.85f
                            "Nina" -> 1.05f
                            else -> 1.0f
                        }
                        speechHelper.speak(previewText, pitch = pitch) {
                            viewModel.stopVoicePreview()
                        }
                    } else {
                        speechHelper.stop()
                    }
                },
                onDelete = if (voice.isCustomClone) {
                    {
                        val cloneId = voice.id.removePrefix("clone-").toLongOrNull()
                        if (cloneId != null) {
                            viewModel.deleteClone(cloneId)
                            Toast.makeText(context, "${voice.name} removida", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else null
            )
        }

        // Live Audio Preview Card
        item {
            VoicePreviewSideCard(
                activeVoice = voices.find { it.id == activeVoiceId } ?: voices.firstOrNull(),
                previewText = previewText,
                onTextChange = { viewModel.setPreviewText(it) },
                isPlaying = playingVoiceId == activeVoiceId,
                onPlay = {
                    val willPlay = playingVoiceId != activeVoiceId
                    viewModel.toggleVoicePreview(activeVoiceId)
                    if (willPlay) {
                        val currentActiveVoice = voices.find { it.id == activeVoiceId }
                        val pitch = when (currentActiveVoice?.name) {
                            "Sofia" -> 1.1f
                            "Maya" -> 1.25f
                            "Theo" -> 0.85f
                            "Nina" -> 1.05f
                            else -> 1.0f
                        }
                        speechHelper.speak(previewText, pitch = pitch) {
                            viewModel.stopVoicePreview()
                        }
                    } else {
                        speechHelper.stop()
                    }
                }
            )
        }

        // Voice Cloning Section
        item {
            VoiceCloningSection(
                cloneName = cloneName,
                onNameChange = { viewModel.setCloneName(it) },
                cloneTagline = cloneTagline,
                onTaglineChange = { viewModel.setCloneTagline(it) },
                cloneLanguage = cloneLanguage,
                onLanguageChange = { viewModel.setCloneLanguage(it) },
                isRecording = isRecording,
                recordingSeconds = recordingSeconds,
                recordedSample = recordedAudioSample,
                consentGiven = consentGiven,
                onConsentChange = { viewModel.setConsentGiven(it) },
                onToggleRecord = {
                    if (isRecording) {
                        viewModel.stopRecording()
                    } else {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED

                        if (hasPermission) {
                            viewModel.startRecording()
                        } else {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                },
                onClearRecording = { viewModel.clearRecording() },
                onCreateClone = {
                    viewModel.createClone(
                        onSuccess = {
                            Toast.makeText(context, "Voz exclusiva criada e adicionada à biblioteca!", Toast.LENGTH_LONG).show()
                        },
                        onError = { msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun VoiceCardItem(
    voice: VoiceInfo,
    isActive: Boolean,
    isPlaying: Boolean,
    onSelect: () -> Unit,
    onTogglePlay: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val color = ReelDefaults.getColor(voice.colorName)

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PaperCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                if (isActive) listOf(Coral, Coral) else listOf(BorderLight, BorderLight)
            )
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("voice_card_${voice.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Initials Avatar
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = voice.initials,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    ),
                    color = color
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = voice.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (isActive) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MintLight)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Mint,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "Padrão",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    ),
                                    color = Mint
                                )
                            }
                        }
                    }
                    if (voice.isCustomClone) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(VioletLight)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Clone",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 9.sp
                                ),
                                color = Violet
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${voice.role} · ${voice.language}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = Muted
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Animated Equalizer Waveform bars
                AnimatedEqualizer(isPlaying = isPlaying, tintColor = color)
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Play preview button
            IconButton(
                onClick = onTogglePlay,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(BorderLight)
                    .testTag("play_voice_${voice.id}")
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Ouvir prévia",
                    tint = Ink,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Use Voice button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isActive) BorderLight else Ink)
                    .clickable { onSelect() }
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Text(
                    text = if (isActive) "Ativa" else "Usar",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    color = if (isActive) Muted else Color.White
                )
            }

            if (onDelete != null) {
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Coral.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Excluir clone",
                        tint = Coral,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedEqualizer(isPlaying: Boolean, tintColor: Color) {
    val transition = rememberInfiniteTransition(label = "wave")
    val heights = (0..12).map { index ->
        val duration = 300 + (index * 70) % 400
        val anim by transition.animateFloat(
            initialValue = 4f,
            targetValue = if (isPlaying) 18f else 6f,
            animationSpec = infiniteRepeatable(
                animation = tween(duration),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar_$index"
        )
        anim
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.height(20.dp)
    ) {
        heights.forEach { h ->
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(h.dp)
                    .clip(CircleShape)
                    .background(if (isPlaying) tintColor else Muted.copy(alpha = 0.3f))
            )
        }
    }
}

@Composable
private fun VoicePreviewSideCard(
    activeVoice: VoiceInfo?,
    previewText: String,
    onTextChange: (String) -> Unit,
    isPlaying: Boolean,
    onPlay: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = PaperDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PRÉVIA DA VOZ",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 9.sp,
                            letterSpacing = 1.sp
                        ),
                        color = Color.White.copy(alpha = 0.5f)
                    )
                    Text(
                        text = activeVoice?.name ?: "Sofia",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        ),
                        color = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Coral),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = previewText,
                onValueChange = onTextChange,
                minLines = 2,
                maxLines = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("voices_preview_text_input"),
                shape = RoundedCornerShape(14.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.08f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.08f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedIndicatorColor = Coral,
                    unfocusedIndicatorColor = Color.White.copy(alpha = 0.15f)
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onPlay,
                colors = ButtonDefaults.buttonColors(containerColor = Coral),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("voices_play_preview_button")
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isPlaying) "Pausar prévia" else "Ouvir prévia real (Cartesia WAV)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun VoiceCloningSection(
    cloneName: String,
    onNameChange: (String) -> Unit,
    cloneTagline: String,
    onTaglineChange: (String) -> Unit,
    cloneLanguage: String,
    onLanguageChange: (String) -> Unit,
    isRecording: Boolean,
    recordingSeconds: Int,
    recordedSample: String?,
    consentGiven: Boolean,
    onConsentChange: (Boolean) -> Unit,
    onToggleRecord: () -> Unit,
    onClearRecording: () -> Unit,
    onCreateClone: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = VioletLight),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Violet.copy(alpha = 0.2f), Violet.copy(alpha = 0.2f)))),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "VOZ EXCLUSIVA",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 9.sp,
                    letterSpacing = 1.sp
                ),
                color = Violet
            )
            Text(
                text = "Crie seu timbre proprietário.",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Grave pelo microfone de 10 a 60 segundos com uma única pessoa falando de forma natural. O Cartesia Sonic gera um modelo acústico privado que fica disponível para qualquer cena.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 16.sp),
                color = Muted,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Form Fields
            OutlinedTextField(
                value = cloneName,
                onValueChange = onNameChange,
                placeholder = { Text("Nome da voz (ex.: Voz do Fundador)", fontSize = 12.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = PaperCard,
                    unfocusedContainerColor = PaperCard,
                    focusedIndicatorColor = Violet,
                    unfocusedIndicatorColor = BorderLight
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = cloneTagline,
                onValueChange = onTaglineChange,
                placeholder = { Text("Descrição curta (ex.: Calma e confiante)", fontSize = 12.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = PaperCard,
                    unfocusedContainerColor = PaperCard,
                    focusedIndicatorColor = Violet,
                    unfocusedIndicatorColor = BorderLight
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Microphone Recording Area
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onToggleRecord,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRecording) Coral else Violet
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isRecording) "Parar gravação" else "Gravar pelo microfone",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color.White
                    )
                }
            }

            if (isRecording) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CoralLight)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Coral)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Gravando… fale naturalmente",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = Coral
                            )
                        }

                        Text(
                            text = String.format("00:%02d / 01:00", recordingSeconds),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            ),
                            color = Coral
                        )
                    }
                }
            }

            if (recordedSample != null && !isRecording) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(PaperCard)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = Mint,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$recordedSample",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = Ink
                        )
                    }

                    Text(
                        text = "Remover",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = Coral,
                        modifier = Modifier.clickable { onClearRecording() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Consent Confirmation
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = consentGiven,
                    onCheckedChange = onConsentChange,
                    colors = CheckboxDefaults.colors(checkedColor = Violet)
                )
                Text(
                    text = "Confirmo que tenho autorização da pessoa gravada para criar e usar este clone de voz.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
                    color = Muted
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onCreateClone,
                colors = ButtonDefaults.buttonColors(containerColor = Violet),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("submit_create_clone_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Criar voz exclusiva",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
}
