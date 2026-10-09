package com.petediano.peusic.ui.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.petediano.peusic.domain.model.Track
import com.petediano.peusic.service.PlaybackController
import com.petediano.peusic.service.PlaybackService
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Shared player state for MiniPlayer + NowPlaying + Music screen.
 */
class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val controller = PlaybackController(application)

    val isPlaying: StateFlow<Boolean> = controller.isPlaying
    val currentTitle: StateFlow<String?> = controller.currentTitle
    val currentArtist: StateFlow<String?> = controller.currentArtist
    val positionMs: StateFlow<Long> = controller.positionMs
    val durationMs: StateFlow<Long> = controller.durationMs
    val shuffle: StateFlow<Boolean> = controller.shuffle
    val repeatMode: StateFlow<Int> = controller.repeatMode
    val hasMedia: StateFlow<Boolean> = controller.hasMedia
    val isConnected: StateFlow<Boolean> = controller.isConnected

    init {
        controller.connect()
    }

    fun playTracks(tracks: List<Track>, startIndex: Int = 0) {
        viewModelScope.launch {
            try {
                getApplication<Application>().startForegroundService(
                    Intent(getApplication(), PlaybackService::class.java)
                )
            } catch (_: Exception) { }
            controller.playTracks(tracks, startIndex)
        }
    }

    fun playPause() = controller.playPause()
    fun next() = controller.next()
    fun previous() = controller.previous()
    fun seekTo(ms: Long) = controller.seekTo(ms)
    fun toggleShuffle() = controller.toggleShuffle()
    fun cycleRepeat() = controller.cycleRepeat()

    override fun onCleared() {
        controller.disconnect()
        super.onCleared()
    }

    companion object {
        fun formatTime(ms: Long): String {
            if (ms <= 0) return "0:00"
            val totalSec = ms / 1000
            val m = totalSec / 60
            val s = totalSec % 60
            return "%d:%02d".format(m, s)
        }
    }
}
