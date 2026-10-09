package com.petediano.peusic.ui.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.petediano.peusic.ui.theme.GradientBottom
import com.petediano.peusic.ui.theme.GradientMid1
import com.petediano.peusic.ui.theme.GradientMid2
import com.petediano.peusic.ui.theme.GradientMid3
import com.petediano.peusic.ui.theme.GradientTop
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1200)
        onFinished()
    }
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
            color = Color.White
        )
    }
}
