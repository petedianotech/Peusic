package com.petediano.peusic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.petediano.peusic.ui.theme.GradientBottom
import com.petediano.peusic.ui.theme.GradientMid1
import com.petediano.peusic.ui.theme.GradientMid2
import com.petediano.peusic.ui.theme.GradientMid3
import com.petediano.peusic.ui.theme.GradientTop
import com.petediano.peusic.ui.theme.PeusicTheme
import com.petediano.peusic.ui.theme.PeusicThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            PeusicTheme(themeMode = PeusicThemeMode.SYSTEM) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PeusicHome()
                }
            }
        }
    }
}

@Composable
fun PeusicHome() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(GradientTop, GradientMid3, GradientMid2, GradientMid1, GradientBottom)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Peusic",
            style = MaterialTheme.typography.displayMedium,
            color = Color.White,
            modifier = Modifier.padding(24.dp)
        )
    }
}
