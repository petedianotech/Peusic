package com.petediano.peusic.ui.screens.music

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.petediano.peusic.domain.model.Album
import com.petediano.peusic.domain.model.Artist
import com.petediano.peusic.domain.model.Folder
import com.petediano.peusic.domain.model.Track
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
        Text(
            "Music",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        OutlinedTextField(
            value = query,
            onValueChange = { musicViewModel.setQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            placeholder = { Text("Search title, artist, album") },
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )

        val tabs = LibraryTab.entries
        ScrollableTabRow(
            selectedTabIndex = tabs.indexOf(tab),
            edgePadding = 12.dp,
            modifier = Modifier.padding(top = 8.dp),
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEach { t ->
                Tab(
                    selected = tab == t,
                    onClick = { musicViewModel.setTab(t) },
                    text = {
                        Text(
                            t.name,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (tab == t) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                when (tab) {
                    LibraryTab.Songs -> "${tracks.size} tracks"
                    LibraryTab.Albums -> "${albums.size} albums"
                    LibraryTab.Artists -> "${artists.size} artists"
                    LibraryTab.Folders -> "${folders.size} folders"
                    LibraryTab.Playlists -> "Playlists"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = { musicViewModel.loadLibrary() }) {
                Text("Scan")
            }
        }

        message?.let {
            Text(
                it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            when (tab) {
                LibraryTab.Songs -> {
                    if (tracks.isEmpty()) {
                        item {
                            Text(
                                "No tracks. Grant permission and Scan.",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                    itemsIndexed(tracks, key = { _, t -> t.id }) { index, track ->
                        TrackRow(
                            track = track,
                            onPlay = {
                                playerViewModel.playTracks(tracks, index)
                                onOpenNowPlaying()
                            },
                            onPlayNext = { playerViewModel.playNext(track) }
                        )
                    }
                }
                LibraryTab.Albums -> {
                    items(albums, key = { it.id to it.title }) { album ->
                        SimpleRow(album.title, "${album.artist ?: "Unknown"} · ${album.trackCount} tracks") {
                            val list = musicViewModel.tracksForAlbum(album)
                            if (list.isNotEmpty()) {
                                playerViewModel.playTracks(list, 0)
                                onOpenNowPlaying()
                            }
                        }
                    }
                }
                LibraryTab.Artists -> {
                    items(artists, key = { it.name }) { artist ->
                        SimpleRow(artist.name, "${artist.trackCount} tracks · ${artist.albumCount} albums") {
                            val list = musicViewModel.tracksForArtist(artist)
                            if (list.isNotEmpty()) {
                                playerViewModel.playTracks(list, 0)
                                onOpenNowPlaying()
                            }
                        }
                    }
                }
                LibraryTab.Folders -> {
                    items(folders, key = { it.path }) { folder ->
                        SimpleRow(folder.name, "${folder.trackCount} tracks") {
                            val list = musicViewModel.tracksForFolder(folder)
                            if (list.isNotEmpty()) {
                                playerViewModel.playTracks(list, 0)
                                onOpenNowPlaying()
                            }
                        }
                    }
                }
                LibraryTab.Playlists -> {
                    item {
                        Text(
                            "Playlists: M3U import/export coming soon.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TrackRow(
    track: Track,
    onPlay: () -> Unit,
    onPlayNext: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onPlay)
            .padding(vertical = 6.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Album art thumbnail
        Surface(
            modifier = Modifier.size(52.dp),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            val art = track.albumArtUri
            if (art != null) {
                AsyncImage(
                    model = art,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        Icons.Filled.MusicNote,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                track.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "${track.displayArtist} · ${track.durationFormatted}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Box {
            IconButton(
                onClick = { menuExpanded = true },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Filled.MoreVert,
                    contentDescription = "More",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Play next") },
                    onClick = {
                        onPlayNext()
                        menuExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Play now") },
                    onClick = {
                        onPlay()
                        menuExpanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun SimpleRow(title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    Icons.Filled.MusicNote,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
