package com.example.reelstudio

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.reelstudio.ui.components.CommandPaletteDialog
import com.example.reelstudio.ui.components.NewProjectDialog
import com.example.reelstudio.ui.components.ReelStudioBottomNav
import com.example.reelstudio.ui.components.ReelStudioTopBar
import com.example.reelstudio.ui.screens.AdminScreen
import com.example.reelstudio.ui.screens.EditorScreen
import com.example.reelstudio.ui.screens.HomeScreen
import com.example.reelstudio.ui.screens.SettingsScreen
import com.example.reelstudio.ui.screens.TemplatesScreen
import com.example.reelstudio.ui.screens.VoicesScreen
import com.example.reelstudio.ui.theme.ReelStudioTheme
import com.example.reelstudio.ui.viewmodel.AdminViewModel
import com.example.reelstudio.ui.viewmodel.EditorViewModel
import com.example.reelstudio.ui.viewmodel.HomeViewModel
import com.example.reelstudio.ui.viewmodel.SettingsViewModel
import com.example.reelstudio.ui.viewmodel.VoicesViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as ReelStudioApplication
        val repository = app.repository

        setContent {
            ReelStudioTheme {
                ReelStudioApp(app = app)
            }
        }
    }
}

@Composable
fun ReelStudioApp(app: ReelStudioApplication) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"

    var showCommandPalette by remember { mutableStateOf(false) }
    var showNewProjectModal by remember { mutableStateOf(false) }

    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.Factory(app.repository)
    )
    val voicesViewModel: VoicesViewModel = viewModel(
        factory = VoicesViewModel.Factory(app.repository)
    )
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(app.repository)
    )

    val isEditorScreen = currentRoute.startsWith("editor/") || currentRoute == "admin"

    Scaffold(
        topBar = {
            if (!isEditorScreen) {
                ReelStudioTopBar(
                    onOpenSearch = { showCommandPalette = true },
                    onNewProject = { showNewProjectModal = true },
                    onNotificationClick = {
                        Toast.makeText(app, "Nenhuma notificação nova no momento", Toast.LENGTH_SHORT).show()
                    },
                    onAdminClick = {
                        navController.navigate("admin")
                    }
                )
            }
        },
        bottomBar = {
            if (!isEditorScreen) {
                ReelStudioBottomNav(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") {
                    HomeScreen(
                        viewModel = homeViewModel,
                        onOpenEditor = { projectId ->
                            navController.navigate("editor/$projectId")
                        },
                        onNavigateToTemplates = {
                            navController.navigate("templates")
                        }
                    )
                }

                composable(
                    route = "editor/{projectId}",
                    arguments = listOf(navArgument("projectId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val projectId = backStackEntry.arguments?.getString("projectId") ?: "product-launch"
                    val editorViewModel: EditorViewModel = viewModel(
                        factory = EditorViewModel.Factory(app.repository, projectId),
                        key = "editor_$projectId"
                    )

                    EditorScreen(
                        viewModel = editorViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable("templates") {
                    TemplatesScreen(
                        onUseTemplate = { presetName ->
                            homeViewModel.createProject(
                                name = "$presetName Reel",
                                preset = presetName,
                                brief = ""
                            ) { newId ->
                                navController.navigate("editor/$newId")
                            }
                        }
                    )
                }

                composable("voices") {
                    VoicesScreen(viewModel = voicesViewModel)
                }

                composable("settings") {
                    SettingsScreen(viewModel = settingsViewModel)
                }

                composable("admin") {
                    val adminViewModel: AdminViewModel = viewModel(
                        factory = AdminViewModel.Factory(app.repository)
                    )
                    AdminScreen(
                        viewModel = adminViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }

        if (showCommandPalette) {
            CommandPaletteDialog(
                onDismiss = { showCommandPalette = false },
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo("home") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        if (showNewProjectModal) {
            NewProjectDialog(
                onDismiss = { showNewProjectModal = false },
                onCreate = { name, preset, brief ->
                    homeViewModel.createProject(name, preset, brief) { id ->
                        showNewProjectModal = false
                        navController.navigate("editor/$id")
                    }
                }
            )
        }
    }
}
