package com.petediano.peusic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.petediano.peusic.ui.navigation.PeusicNavHost
import com.petediano.peusic.ui.theme.PeusicTheme
import com.petediano.peusic.ui.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent { PeusicAppRoot() }
    }
}

@Composable
fun PeusicAppRoot(settingsViewModel: SettingsViewModel = viewModel()) {
    val themeMode by settingsViewModel.themeMode.collectAsState()
    PeusicTheme(themeMode = themeMode) {
        Surface(modifier = Modifier.fillMaxSize()) {
            PeusicNavHost()
        }
    }
}
