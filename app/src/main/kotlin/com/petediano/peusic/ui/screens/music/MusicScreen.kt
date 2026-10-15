package com.petediano.peusic.ui.screens.music

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.petediano.peusic.domain.model.Album
import com.petediano.peusic.domain.model.Artist
import com.petediano.peusic.domain.model.Folder
import com.petediano.peusic.domain.model.Track
import com.petediano.peusic.ui.theme.CardShape
import com.petediano.peusic.ui.viewmodel.LibraryTab
import com.petediano.peusic.ui.viewmodel.MusicViewModel
import com.petediano.peusic.ui.viewmodel.PlayerViewModel

@Composable
fun MusicScreen(
    onOpenNowPlaying: () -> Unit = {},
    musicViewModel: MusicViewModel = viewModel(),
    playerViewModel: PlayerViewModel = viewModel()
) {
    val tracks by musicViewModel.filteredTracks.collectAsState()
    val albums by musicViewModel.albums.collectAsState()
    val artists by musicViewModel.artists.collectAsState()
    val folders by musicViewModel.folders.collectAsState()
    val message by musicViewModel.message.collectAsState()
    val tab by musicViewModel.tab.collectAsState()
    val query by musicViewModel.query.collectAsState()
    val context = LocalContext.current

    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) musicViewModel.loadLibrary()
        else musicViewModel.setMessage("Audio permission denied")
    }

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(context, permission) ==
            android.content.pm.PackageManager.PERMISSION_GRANTED
        if (granted) musicViewModel.loadLibrary() else launcher.launch(permission)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Music", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp))
        OutlinedTextField(
            value = query,
            onValueChange = { musicViewModel.setQuery(it) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            placeholder = { Text("Search title, artist, album") },
            singleLine = true
        )
        val tabs = LibraryTab.entries
        ScrollableTabRow(selectedTabIndex = tabs.indexOf(tab), edgePadding = 12.dp, modifier = Modifier.padding(top = 8.dp)) {
            tabs.forEach { t ->
                Tab(selected = tab == t, onClick = { musicViewModel.setTab(t) }, text = { Text(t.name) })
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            message?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            TextButton(onClick = { musicViewModel.loadLibrary() }) { Text("Scan") }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when (tab) {
                LibraryTab.Songs -> {
                    if (tracks.isEmpty()) item { Text("No tracks. Grant permission and Scan.", style = MaterialTheme.typography.bodyMedium) }
                    itemsIndexed(tracks, key = { _, t -> t.id }) { index, track ->
                        TrackRow(track = track, onPlay = {
                            playerViewModel.playTracks(tracks, index)
                            onOpenNowPlaying()
                        }, onPlayNext = { playerViewModel.playNext(track) })
                    }
                }
                LibraryTab.Albums -> {
                    items(albums, key = { it.id to it.title }) { album ->
                        SimpleRow(album.title, "${album.artist} · ${album.trackCount} tracks") {
                            val list = musicViewModel.tracksForAlbum(album)
                            if (list.isNotEmpty()) { playerViewModel.playTracks(list, 0); onOpenNowPlaying() }
                        }
                    }
                }
                LibraryTab.Artists -> {
                    items(artists, key = { it.name }) { artist ->
                        SimpleRow(artist.name, "${artist.trackCount} tracks · ${artist.albumCount} albums") {
                            val list = musicViewModel.tracksForArtist(artist)
                            if (list.isNotEmpty()) { playerViewModel.playTracks(list, 0); onOpenNowPlaying() }
                        }
                    }
                }
                LibraryTab.Folders -> {
                    items(folders, key = { it.path }) { folder ->
                        SimpleRow(folder.name, "${folder.trackCount} tracks") {
                            val list = musicViewModel.tracksForFolder(folder)
                            if (list.isNotEmpty()) { playerViewModel.playTracks(list, 0); onOpenNowPlaying() }
                        }
                    }
                }
                LibraryTab.Playlists -> {
                    item { Text("Playlists: M3U import/export and multi-queue in Phase 2 polish.", style = MaterialTheme.typography.bodyMedium) }
                }
            }
        }
    }
}

@Composable
private fun TrackRow(track: Track, onPlay: () -> Unit, onPlayNext: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onPlay), shape = CardShape, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text(track.title, style = MaterialTheme.typography.titleSmall)
                Text("${track.displayArtist} · ${track.durationFormatted}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            TextButton(onClick = onPlayNext) { Text("Next") }
        }
    }
}

@Composable
private fun SimpleRow(title: String, subtitle: String, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = CardShape, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
