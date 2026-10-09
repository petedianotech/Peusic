package com.petediano.peusic.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.petediano.peusic.ui.screens.discover.DiscoverScreen
import com.petediano.peusic.ui.screens.downloads.DownloadsScreen
import com.petediano.peusic.ui.screens.music.MusicScreen
import com.petediano.peusic.ui.screens.player.NowPlayingScreen
import com.petediano.peusic.ui.screens.settings.SettingsScreen
import com.petediano.peusic.ui.screens.splash.SplashScreen
import com.petediano.peusic.ui.theme.AuroraBlue

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Splash : Screen("splash", "", Icons.Filled.Home)
    data object Discover : Screen("discover", "Discover", Icons.Filled.Home)
    data object Downloads : Screen("downloads", "Downloads", Icons.Filled.List)
    data object Music : Screen("music", "Music", Icons.Filled.PlayArrow)
    data object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
    data object NowPlaying : Screen("now_playing", "Now Playing", Icons.Filled.PlayArrow)
}

private val bottomDestinations = listOf(Screen.Discover, Screen.Downloads, Screen.Music, Screen.Settings)

@Composable
fun PeusicNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = bottomDestinations.any { dest ->
        currentDestination?.hierarchy?.any { it.route == dest.route } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomDestinations.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AuroraBlue,
                                selectedTextColor = AuroraBlue,
                                indicatorColor = AuroraBlue.copy(alpha = 0.15f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Splash.route
            ) {
                composable(Screen.Splash.route) {
                    SplashScreen(onFinished = {
                        navController.navigate(Screen.Discover.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    })
                }
                composable(Screen.Discover.route) {
                    DiscoverScreen(onOpenNowPlaying = { navController.navigate(Screen.NowPlaying.route) })
                }
                composable(Screen.Downloads.route) { DownloadsScreen() }
                composable(Screen.Music.route) {
                    MusicScreen(onOpenNowPlaying = { navController.navigate(Screen.NowPlaying.route) })
                }
                composable(Screen.Settings.route) { SettingsScreen() }
                composable(Screen.NowPlaying.route) {
                    NowPlayingScreen(onBack = { navController.popBackStack() })
                }
            }
        }
    }
}
