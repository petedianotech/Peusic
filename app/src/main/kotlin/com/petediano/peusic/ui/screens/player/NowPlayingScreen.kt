package com.petediano.peusic.ui.screens.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.Player
import com.petediano.peusic.ui.viewmodel.PlayerViewModel

@Composable
fun NowPlayingScreen(
    onBack: () -> Unit,
    playerViewModel: PlayerViewModel = viewModel()
) {
    val isPlaying by playerViewModel.isPlaying.collectAsState()
    val title by playerViewModel.currentTitle.collectAsState()
    val artist by playerViewModel.currentArtist.collectAsState()
    val position by playerViewModel.positionMs.collectAsState()
    val duration by playerViewModel.durationMs.collectAsState()
    val shuffle by playerViewModel.shuffle.collectAsState()
    val repeatMode by playerViewModel.repeatMode.collectAsState()
    val queueSize by playerViewModel.queueSize.collectAsState()
    val currentIndex by playerViewModel.currentIndex.collectAsState()
    val speed by playerViewModel.playbackSpeed.collectAsState()
    val sleepSec by playerViewModel.sleepRemainingSec.collectAsState()

    val progress = if (duration > 0) position.toFloat() / duration else 0f
    val repeatLabel = when (repeatMode) {
        Player.REPEAT_MODE_ONE -> "Repeat one"
        Player.REPEAT_MODE_ALL -> "Repeat all"
        else -> "Repeat off"
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.Close, contentDescription = "Close") }
            Text("Now Playing", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.size(48.dp))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(120.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(20.dp))
        Text(title ?: "No track", style = MaterialTheme.typography.headlineSmall, maxLines = 2, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
        Text(artist ?: "", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 6.dp))
        if (queueSize > 0) {
            Text("Track ${currentIndex + 1} of $queueSize", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 6.dp))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Slider(
            value = progress.coerceIn(0f, 1f),
            onValueChange = { f -> if (duration > 0) playerViewModel.seekTo((f * duration).toLong()) },
            modifier = Modifier.fillMaxWidth()
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(PlayerViewModel.formatTime(position), style = MaterialTheme.typography.labelMedium)
            Text(PlayerViewModel.formatTime(duration), style = MaterialTheme.typography.labelMedium)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = { playerViewModel.previous() }) { Text("Prev") }
            Button(onClick = { playerViewModel.playPause() }) { Text(if (isPlaying) "Pause" else "Play") }
            TextButton(onClick = { playerViewModel.next() }) { Text("Next") }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            TextButton(onClick = { playerViewModel.toggleShuffle() }) { Text(if (shuffle) "Shuffle on" else "Shuffle off") }
            TextButton(onClick = { playerViewModel.cycleRepeat() }) { Text(repeatLabel) }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf(0.75f, 1.0f, 1.25f, 1.5f).forEach { s ->
                TextButton(onClick = { playerViewModel.setPlaybackSpeed(s) }) {
                    Text(if (kotlin.math.abs(speed - s) < 0.01f) "• ${s}x" else "${s}x")
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            TextButton(onClick = { playerViewModel.setSleepTimerMinutes(15) }) { Text("Sleep 15m") }
            TextButton(onClick = { playerViewModel.setSleepTimerMinutes(30) }) { Text("Sleep 30m") }
            TextButton(onClick = { playerViewModel.setSleepTimerMinutes(0) }) { Text("Cancel") }
        }
        if (sleepSec > 0) {
            Text("Sleep in ${sleepSec / 60}:${"%02d".format(sleepSec % 60)}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { playerViewModel.clearQueue() }) { Text("Clear queue") }
    }
}
