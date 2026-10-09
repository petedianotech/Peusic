package com.petediano.peusic.ui.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.petediano.peusic.data.library.MediaStoreLibraryRepository
import com.petediano.peusic.domain.model.Track
import com.petediano.peusic.service.PlaybackService
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

    private var controller: MediaController? = null

    init { connectController() }

    private fun connectController() {
        val token = SessionToken(
            getApplication(),
            ComponentName(getApplication(), PlaybackService::class.java)
        )
        val future = MediaController.Builder(getApplication(), token).buildAsync()
        future.addListener({
            try { controller = future.get() } catch (_: Exception) { }
        }, { runnable -> Handler(Looper.getMainLooper()).post(runnable) })
    }

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

    fun play(track: Track) {
        val c = controller ?: return
        c.setMediaItem(MediaItem.fromUri(track.contentUri))
        c.prepare()
        c.play()
        try {
            getApplication<Application>().startForegroundService(
                Intent(getApplication(), PlaybackService::class.java)
            )
        } catch (_: Exception) { }
    }

    fun setMessage(msg: String) { _message.value = msg }

    override fun onCleared() {
        controller?.release()
        super.onCleared()
    }
}
