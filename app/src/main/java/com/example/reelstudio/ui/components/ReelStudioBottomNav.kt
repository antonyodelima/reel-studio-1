package com.example.reelstudio.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reelstudio.ui.theme.Coral
import com.example.reelstudio.ui.theme.Ink
import com.example.reelstudio.ui.theme.Muted
import com.example.reelstudio.ui.theme.PaperCard

enum class NavDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    HOME("home", "Projetos", Icons.Default.GridView),
    TEMPLATES("templates", "Templates", Icons.Default.AutoAwesome),
    VOICES("voices", "Vozes", Icons.Default.Mic),
    SETTINGS("settings", "Configurações", Icons.Default.Settings)
}

@Composable
fun ReelStudioBottomNav(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = PaperCard,
        tonalElevation = 8.dp
    ) {
        NavDestination.values().forEach { destination ->
            val isSelected = currentRoute == destination.route ||
                (destination == NavDestination.HOME && currentRoute.startsWith("editor/"))

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(destination.route) },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.label
                    )
                },
                label = {
                    Text(
                        text = destination.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                            fontSize = 10.sp
                        )
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Coral,
                    selectedTextColor = Ink,
                    indicatorColor = Coral.copy(alpha = 0.12f),
                    unselectedIconColor = Muted,
                    unselectedTextColor = Muted
                ),
                modifier = Modifier.testTag("nav_item_${destination.route}")
            )
        }
    }
}
