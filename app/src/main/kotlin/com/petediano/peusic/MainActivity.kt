package com.petediano.peusic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.petediano.peusic.ui.navigation.PeusicNavHost
import com.petediano.peusic.ui.theme.PeusicTheme
import com.petediano.peusic.ui.theme.PeusicThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            PeusicAppRoot()
        }
    }
}

@Composable
fun PeusicAppRoot() {
    // Theme mode will later be driven by DataStore preference.
    PeusicTheme(themeMode = PeusicThemeMode.SYSTEM) {
        Surface(modifier = Modifier.fillMaxSize()) {
            PeusicNavHost()
        }
    }
}
