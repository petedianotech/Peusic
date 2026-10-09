package com.petediano.peusic.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.petediano.peusic.data.library.MediaStoreLibraryRepository
import com.petediano.peusic.domain.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MusicViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = MediaStoreLibraryRepository(application)
    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks.asStateFlow()
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun loadLibrary() {
        viewModelScope.launch {
            try {
                val list = repo.getAllTracks()
                _tracks.value = list
                _message.value = if (list.isEmpty()) "No music found on this device." else "${list.size} tracks"
            } catch (e: Exception) {
                _message.value = e.message ?: "Failed to scan library"
            }
        }
    }

    fun setMessage(msg: String) { _message.value = msg }
}
