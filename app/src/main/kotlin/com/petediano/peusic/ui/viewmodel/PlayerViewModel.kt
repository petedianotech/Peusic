package com.petediano.peusic.ui.viewmodel

import android.app.Application
import android.content.Intent
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.petediano.peusic.domain.model.Track
import com.petediano.peusic.service.PlaybackController
import com.petediano.peusic.service.PlaybackService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Shared player state — Phase 1: queue, speed, sleep timer.
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
    val queueSize: StateFlow<Int> = controller.queueSize
    val currentIndex: StateFlow<Int> = controller.currentIndex
    val playbackSpeed: StateFlow<Float> = controller.playbackSpeed

    private val _sleepRemainingSec = MutableStateFlow(0)
    val sleepRemainingSec: StateFlow<Int> = _sleepRemainingSec.asStateFlow()

    private var sleepTimer: CountDownTimer? = null

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

    fun playNext(track: Track) = controller.playNext(track)
    fun clearQueue() = controller.clearQueue()
    fun playPause() = controller.playPause()
    fun next() = controller.next()
    fun previous() = controller.previous()
    fun seekTo(ms: Long) = controller.seekTo(ms)
    fun toggleShuffle() = controller.toggleShuffle()
    fun cycleRepeat() = controller.cycleRepeat()
    fun setPlaybackSpeed(speed: Float) = controller.setPlaybackSpeed(speed)

    /** minutes: 0 cancels, >0 starts countdown then pauses */
    fun setSleepTimerMinutes(minutes: Int) {
        sleepTimer?.cancel()
        if (minutes <= 0) {
            _sleepRemainingSec.value = 0
            return
        }
        val totalMs = minutes * 60_000L
        _sleepRemainingSec.value = minutes * 60
        sleepTimer = object : CountDownTimer(totalMs, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                _sleepRemainingSec.value = (millisUntilFinished / 1000).toInt()
            }
            override fun onFinish() {
                _sleepRemainingSec.value = 0
                if (controller.isPlaying.value) controller.playPause()
            }
        }.start()
    }

    override fun onCleared() {
        sleepTimer?.cancel()
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
