package com.petediano.peusic.service

import android.content.ComponentName
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.petediano.peusic.domain.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Thin MediaController wrapper — Phase 1: queue, speed, gapless Media3 */
class PlaybackController(private val context: Context) {

    private var controller: MediaController? = null

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentTitle = MutableStateFlow<String?>(null)
    val currentTitle: StateFlow<String?> = _currentTitle.asStateFlow()

    private val _currentArtist = MutableStateFlow<String?>(null)
    val currentArtist: StateFlow<String?> = _currentArtist.asStateFlow()

    private val _positionMs = MutableStateFlow(0L)
    val positionMs: StateFlow<Long> = _positionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _shuffle = MutableStateFlow(false)
    val shuffle: StateFlow<Boolean> = _shuffle.asStateFlow()

    private val _repeatMode = MutableStateFlow(Player.REPEAT_MODE_OFF)
    val repeatMode: StateFlow<Int> = _repeatMode.asStateFlow()

    private val _hasMedia = MutableStateFlow(false)
    val hasMedia: StateFlow<Boolean> = _hasMedia.asStateFlow()

    private val _queueSize = MutableStateFlow(0)
    val queueSize: StateFlow<Int> = _queueSize.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private val mainHandler = Handler(Looper.getMainLooper())
    private val positionTicker = object : Runnable {
        override fun run() {
            updatePosition()
            if (_isPlaying.value) mainHandler.postDelayed(this, 500)
        }
    }

    private val listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
            if (isPlaying) {
                mainHandler.removeCallbacks(positionTicker)
                mainHandler.post(positionTicker)
            }
        }
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            _currentTitle.value = mediaItem?.mediaMetadata?.title?.toString()
            _currentArtist.value = mediaItem?.mediaMetadata?.artist?.toString()
            val c = controller
            _hasMedia.value = (c?.mediaItemCount ?: 0) > 0
            _queueSize.value = c?.mediaItemCount ?: 0
            _currentIndex.value = c?.currentMediaItemIndex?.coerceAtLeast(0) ?: 0
            updatePosition()
        }
        override fun onPlaybackStateChanged(playbackState: Int) {
            updatePosition()
            val c = controller
            _hasMedia.value = (c?.mediaItemCount ?: 0) > 0
            _queueSize.value = c?.mediaItemCount ?: 0
            _currentIndex.value = c?.currentMediaItemIndex?.coerceAtLeast(0) ?: 0
        }
        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            _shuffle.value = shuffleModeEnabled
        }
        override fun onRepeatModeChanged(repeatMode: Int) {
            _repeatMode.value = repeatMode
        }
        override fun onPlaybackParametersChanged(playbackParameters: androidx.media3.common.PlaybackParameters) {
            _playbackSpeed.value = playbackParameters.speed
        }
    }

    fun connect() {
        if (controller != null) return
        val token = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val future = MediaController.Builder(context, token).buildAsync()
        future.addListener({
            try {
                val c = future.get()
                controller = c
                c.addListener(listener)
                _isConnected.value = true
                _isPlaying.value = c.isPlaying
                _currentTitle.value = c.mediaMetadata.title?.toString()
                _currentArtist.value = c.mediaMetadata.artist?.toString()
                _shuffle.value = c.shuffleModeEnabled
                _repeatMode.value = c.repeatMode
                _hasMedia.value = c.mediaItemCount > 0
                _queueSize.value = c.mediaItemCount
                _currentIndex.value = c.currentMediaItemIndex.coerceAtLeast(0)
                _playbackSpeed.value = c.playbackParameters.speed
                updatePosition()
            } catch (_: Exception) {
                _isConnected.value = false
            }
        }, { r -> mainHandler.post(r) })
    }

    fun disconnect() {
        mainHandler.removeCallbacks(positionTicker)
        controller?.removeListener(listener)
        controller?.release()
        controller = null
        _isConnected.value = false
        _hasMedia.value = false
    }

    fun playTracks(tracks: List<Track>, startIndex: Int = 0) {
        val c = controller ?: return
        if (tracks.isEmpty()) return
        val items = tracks.map { track ->
            MediaItem.Builder()
                .setUri(track.contentUri)
                .setMediaId(track.id.toString())
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(track.title)
                        .setArtist(track.displayArtist)
                        .setAlbumTitle(track.displayAlbum)
                        .build()
                )
                .build()
        }
        c.setMediaItems(items, startIndex.coerceIn(0, items.lastIndex), 0L)
        c.prepare()
        c.play()
        _hasMedia.value = true
        _queueSize.value = items.size
        _currentIndex.value = startIndex.coerceIn(0, items.lastIndex)
    }

    fun playNext(track: Track) {
        val c = controller ?: return
        val item = MediaItem.Builder()
            .setUri(track.contentUri)
            .setMediaId(track.id.toString())
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(track.title)
                    .setArtist(track.displayArtist)
                    .setAlbumTitle(track.displayAlbum)
                    .build()
            )
            .build()
        val insertIndex = (c.currentMediaItemIndex + 1).coerceAtMost(c.mediaItemCount)
        c.addMediaItem(insertIndex, item)
        _queueSize.value = c.mediaItemCount
        _hasMedia.value = true
    }

    fun clearQueue() {
        val c = controller ?: return
        c.clearMediaItems()
        _hasMedia.value = false
        _queueSize.value = 0
        _currentIndex.value = 0
        _currentTitle.value = null
        _currentArtist.value = null
    }

    fun playPause() {
        val c = controller ?: return
        if (c.isPlaying) c.pause() else c.play()
    }

    fun seekTo(positionMs: Long) {
        controller?.seekTo(positionMs.coerceAtLeast(0L))
        updatePosition()
    }

    fun next() { controller?.seekToNextMediaItem() }
    fun previous() { controller?.seekToPreviousMediaItem() }

    fun toggleShuffle() {
        val c = controller ?: return
        c.shuffleModeEnabled = !c.shuffleModeEnabled
    }

    fun cycleRepeat() {
        val c = controller ?: return
        c.repeatMode = when (c.repeatMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            else -> Player.REPEAT_MODE_OFF
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        val s = speed.coerceIn(0.5f, 2.0f)
        controller?.setPlaybackSpeed(s)
        _playbackSpeed.value = s
    }

    fun updatePosition() {
        val c = controller ?: return
        _positionMs.value = c.currentPosition.coerceAtLeast(0L)
        val d = c.duration
        _durationMs.value = if (d > 0) d else 0L
    }
}
