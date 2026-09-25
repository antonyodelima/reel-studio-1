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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.VideoSettings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reelstudio.data.model.ReelDefaults
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
import com.example.reelstudio.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToAdmin: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()
    var selectedSection by remember { mutableStateOf("Geral") }

    val sections = listOf("Geral", "Renderização", "Legendas", "Integrações")

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
                    text = "CONFIGURAÇÕES DO ESPAÇO",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.4.sp
                    ),
                    color = Coral
                )
                Text(
                    text = "Deixe o estúdio com a sua cara.",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Ajuste os parâmetros padrão que orientam cada nova produção e exportação.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = Muted,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Section Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sections.forEach { section ->
                    val isSelected = selectedSection == section
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Ink else PaperCard)
                            .border(1.dp, if (isSelected) Ink else BorderLight, RoundedCornerShape(12.dp))
                            .clickable { selectedSection = section }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = section,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = if (isSelected) Color.White else Muted
                        )
                    }
                }
            }
        }

        // Section Content Card
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = PaperCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderLight, BorderLight))),
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
                                text = selectedSection,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "As preferências são salvas localmente neste dispositivo.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = Muted
                            )
                        }

                        val sectionIcon = when (selectedSection) {
                            "Geral" -> Icons.Default.Palette
                            "Renderização" -> Icons.Default.VideoSettings
                            "Legendas" -> Icons.Default.Subtitles
                            else -> Icons.Default.Cloud
                        }

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CoralLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = sectionIcon,
                                contentDescription = null,
                                tint = Coral,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = BorderLight)
                    Spacer(modifier = Modifier.height(16.dp))

                    when (selectedSection) {
                        "Geral" -> GeneralSettingsSection(viewModel, settings?.workspaceName ?: "Northstar Studio", settings?.autosave ?: true)
                        "Renderização" -> RenderingSettingsSection(viewModel, settings?.defaultFormat ?: "9:16 Portrait", settings?.engine ?: "Remotion")
                        "Legendas" -> CaptionsSettingsSection(viewModel, settings?.captions ?: true)
                        "Integrações" -> IntegrationsSettingsSection()
                    }
                }
            }
        }

        // Save Confirmation Pill
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(PaperCard)
                    .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = Mint,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "As configurações estão ativas e sincronizadas.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = Muted
                        )
                    }

                    Button(
                        onClick = {
                            Toast.makeText(context, "Configurações salvas!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Ink),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Salvar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Help & Support Card
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CoralLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Precisa de ajuda com o estúdio?",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Leia o guia do criador ou consulte os atalhos de teclado.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = Muted
                        )
                    }

                    Text(
                        text = "Suporte →",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        ),
                        color = Coral,
                        modifier = Modifier.clickable {
                            Toast.makeText(context, "Suporte: hello@reelstudio.local", Toast.LENGTH_LONG).show()
                        }
                    )
                }
            }
        }

        // Admin Environment Control Card
        if (onNavigateToAdmin != null) {
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = PaperDark),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "PAINEL DE CONTROLE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 9.sp,
                                        letterSpacing = 1.sp
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
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Ambiente de Controle do Estúdio",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                ),
                                color = Color.White
                            )
                            Text(
                                text = "Telemetria de pipeline, motores de render, filtros Cartesia e logs do sistema.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        Button(
                            onClick = onNavigateToAdmin,
                            colors = ButtonDefaults.buttonColors(containerColor = Coral),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Abrir", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Danger Zone: Reset to Demo Data
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = PaperCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Coral.copy(alpha = 0.3f), Coral.copy(alpha = 0.3f)))),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Zona de perigo",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = Coral
                        )
                    )
                    Text(
                        text = "Restaure os projetos e dados iniciais de demonstração caso queira recomeçar do zero.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = Muted,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.resetToDemo()
                            Toast.makeText(context, "Dados de demonstração restaurados!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Coral.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = Coral,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Restaurar dados de demonstração",
                            fontWeight = FontWeight.Bold,
                            color = Coral,
                            fontSize = 11.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun GeneralSettingsSection(
    viewModel: SettingsViewModel,
    workspaceName: String,
    autosave: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Workspace Name
        Column {
            Text(
                text = "Nome do espaço de trabalho",
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Identificador usado nos cabeçalhos e metadados de saída.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = Muted
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = workspaceName,
                onValueChange = { viewModel.updateWorkspaceName(it) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedIndicatorColor = Coral,
                    unfocusedIndicatorColor = BorderLight
                )
            )
        }

        // Autosave
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Salvar rascunhos automaticamente",
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Mantenha as edições salvas no banco Room enquanto avança no roteiro.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = Muted
                )
            }

            Switch(
                checked = autosave,
                onCheckedChange = { viewModel.toggleAutosave() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Coral
                )
            )
        }
    }
}

@Composable
private fun RenderingSettingsSection(
    viewModel: SettingsViewModel,
    defaultFormat: String,
    engine: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Default Format
        Column {
            Text(
                text = "Formato padrão",
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "A proporção de tela usada ao criar um novo projeto.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = Muted
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ReelDefaults.formatOptions.forEach { format ->
                    val isSelected = defaultFormat == format
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) CoralLight else MaterialTheme.colorScheme.background)
                            .border(1.dp, if (isSelected) Coral else BorderLight, RoundedCornerShape(10.dp))
                            .clickable { viewModel.updateDefaultFormat(format) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = format,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = if (isSelected) Coral else Ink
                        )
                    }
                }
            }
        }

        // Engine
        Column {
            Text(
                text = "Motor de renderização",
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Escolha o renderizador para exportação final dos vídeos.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = Muted
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ReelDefaults.engineOptions.forEach { eng ->
                    val isSelected = engine == eng
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) VioletLight else MaterialTheme.colorScheme.background)
                            .border(1.dp, if (isSelected) Violet else BorderLight, RoundedCornerShape(10.dp))
                            .clickable { viewModel.updateEngine(eng) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = eng,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = if (isSelected) Violet else Ink
                        )
                    }
                }
            }
        }

        // Privacy indicator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MintLight)
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Mint,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Privacidade da saída: Renders permanecem locais até você baixar.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    color = Mint
                )
            }
        }
    }
}

@Composable
private fun CaptionsSettingsSection(
    viewModel: SettingsViewModel,
    captionsEnabled: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Gerar legendas automáticas",
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Criar faixa sincronizada em SRT e VTT em toda produção.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = Muted
                )
            }

            Switch(
                checked = captionsEnabled,
                onCheckedChange = { viewModel.toggleCaptions() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Coral
                )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(BorderLight)
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Área segura de leitura no celular",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Ink
                )
                Text(
                    text = "Ativada (M3 SafeInsets)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = Mint
                    )
                )
            }
        }
    }
}

@Composable
private fun IntegrationsSettingsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        val integrations = listOf(
            Triple("Cartesia Sonic", "Narração real em português e 40+ idiomas", "Conectado"),
            Triple("Unsplash Media", "Biblioteca de imagens e vídeos para fundos", "Conectado"),
            Triple("Ollama / IA Local", "Geração local de roteiros e ideias de cenas", "Opcional")
        )

        integrations.forEach { (name, desc, status) ->
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = Muted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (status == "Conectado") MintLight else PaperCard)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = status,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            ),
                            color = if (status == "Conectado") Mint else Muted
                        )
                    }
                }
            }
        }
    }
}
