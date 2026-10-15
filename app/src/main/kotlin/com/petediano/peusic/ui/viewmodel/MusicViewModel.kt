package com.petediano.peusic.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.petediano.peusic.data.library.MediaStoreLibraryRepository
import com.petediano.peusic.domain.model.Album
import com.petediano.peusic.domain.model.Artist
import com.petediano.peusic.domain.model.Folder
import com.petediano.peusic.domain.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class LibraryTab { Songs, Albums, Artists, Folders, Playlists }

class MusicViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = MediaStoreLibraryRepository(application)

    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks.asStateFlow()

    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums.asStateFlow()

    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists: StateFlow<List<Artist>> = _artists.asStateFlow()

    private val _folders = MutableStateFlow<List<Folder>>(emptyList())
    val folders: StateFlow<List<Folder>> = _folders.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _tab = MutableStateFlow(LibraryTab.Songs)
    val tab: StateFlow<LibraryTab> = _tab.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _filtered = MutableStateFlow<List<Track>>(emptyList())
    val filteredTracks: StateFlow<List<Track>> = _filtered.asStateFlow()

    fun setTab(t: LibraryTab) { _tab.value = t }

    fun setQuery(q: String) {
        _query.value = q
        applyFilter()
    }

    private fun applyFilter() {
        val q = _query.value.trim().lowercase()
        _filtered.value = if (q.isEmpty()) _tracks.value
        else _tracks.value.filter {
            it.title.lowercase().contains(q) ||
                it.displayArtist.lowercase().contains(q) ||
                it.displayAlbum.lowercase().contains(q)
        }
    }

    fun loadLibrary() {
        viewModelScope.launch {
            try {
                val list = repo.getAllTracks()
                _tracks.value = list
                applyFilter()
                _albums.value = repo.getAlbums()
                _artists.value = repo.getArtists()
                _folders.value = repo.getFolders()
                _message.value = if (list.isEmpty()) "No music found on this device." else "${list.size} tracks"
            } catch (e: Exception) {
                _message.value = e.message ?: "Failed to scan library"
            }
        }
    }

    fun setMessage(msg: String) { _message.value = msg }

    fun tracksForAlbum(album: Album): List<Track> =
        _tracks.value.filter { it.displayAlbum == album.title }

    fun tracksForArtist(artist: Artist): List<Track> =
        _tracks.value.filter { it.displayArtist == artist.name }

    fun tracksForFolder(folder: Folder): List<Track> =
        _tracks.value.filter { it.folderPath == folder.path }
}
