package com.petediano.peusic.ui.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.petediano.peusic.ui.theme.MiniPlayerShape
import com.petediano.peusic.ui.viewmodel.PlayerViewModel

@Composable
fun MiniPlayer(
    onOpenNowPlaying: () -> Unit,
    modifier: Modifier = Modifier,
    playerViewModel: PlayerViewModel = viewModel()
) {
    val hasMedia by playerViewModel.hasMedia.collectAsState()
    val isPlaying by playerViewModel.isPlaying.collectAsState()
    val title by playerViewModel.currentTitle.collectAsState()
    val artist by playerViewModel.currentArtist.collectAsState()
    val artworkUri by playerViewModel.currentArtworkUri.collectAsState()
    val position by playerViewModel.positionMs.collectAsState()
    val duration by playerViewModel.durationMs.collectAsState()

    if (!hasMedia) return

    val progress = if (duration > 0) (position.toFloat() / duration).coerceIn(0f, 1f) else 0f

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .clickable(onClick = onOpenNowPlaying),
        shape = MiniPlayerShape,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        shadowElevation = 10.dp
    ) {
        Column {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Album art thumbnail
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    if (!artworkUri.isNullOrBlank()) {
                        AsyncImage(
                            model = artworkUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                Icons.Filled.MusicNote,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        title ?: "Unknown",
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        artist?.takeIf { it.isNotBlank() && it != "<unknown>" } ?: "Unknown Artist",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = { playerViewModel.playPause() },
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = { playerViewModel.next() },
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        Icons.Filled.SkipNext,
                        contentDescription = "Next",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
