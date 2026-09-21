package com.craiovadata.rfiplayer

import android.app.Application
import android.content.ComponentName
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch

data class PlayerUiState(
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val currentMediaUri: Uri? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val errorMessage: String? = null,
)

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val AUTOPLAY_INITIAL_VOLUME = 0.35f
    }

    private val controllerFuture = MediaController.Builder(
        application,
        SessionToken(application, ComponentName(application, AudioService::class.java)),
    ).buildAsync()

    private var activeController: MediaController? = null

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    val stations = listOf(
        RadioStation(R.string.rfi, "http://asculta.rfi.ro:9128/live.aac"),
        RadioStation(R.string.inter, "http://icecast.radiofrance.fr/franceinter-midfi.mp3"),
        RadioStation(R.string.itzy_bitzy, "http://live.itsybitsy.ro:8000/itsybitsy"),
    )

    private val playerListener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            if (events.containsAny(
                    Player.EVENT_PLAYBACK_STATE_CHANGED,
                    Player.EVENT_PLAY_WHEN_READY_CHANGED,
                    Player.EVENT_MEDIA_ITEM_TRANSITION,
                    Player.EVENT_IS_PLAYING_CHANGED,
                    Player.EVENT_MEDIA_METADATA_CHANGED,
                )
            ) {
                syncState(player)
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            _uiState.update {
                it.copy(
                    errorMessage = error.localizedMessage ?: "Playback error",
                    isLoading = false,
                )
            }
        }

        override fun onIsPlayingChanged(playing: Boolean) {
            if (playing) {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }

    init {
        viewModelScope.launch {
            try {
                val controller = controllerFuture.await()
                activeController = controller
                controller.addListener(playerListener)
                syncState(controller)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.localizedMessage) }
            }
        }
    }

    private fun syncState(player: Player) {
        val metadata = player.mediaMetadata
        val itemMetadata = player.currentMediaItem?.mediaMetadata
        val title = metadata.title?.toString()
            ?: metadata.displayTitle?.toString()
            ?: itemMetadata?.title?.toString()
            ?: itemMetadata?.displayTitle?.toString()
        val subtitle = metadata.subtitle?.toString()
            ?: metadata.artist?.toString()
            ?: metadata.albumArtist?.toString()
            ?: itemMetadata?.subtitle?.toString()
            ?: itemMetadata?.artist?.toString()
            ?: itemMetadata?.albumArtist?.toString()

        val currentMediaUri = if (player.playbackState == Player.STATE_IDLE) {
            null
        } else {
            player.currentMediaItem?.localConfiguration?.uri
        }

        _uiState.update {
            it.copy(
                isPlaying = player.isPlaying,
                isLoading = player.playbackState == Player.STATE_BUFFERING,
                currentMediaUri = currentMediaUri,
                title = title,
                subtitle = subtitle,
            )
        }
    }

    fun play(station: RadioStation) {
        viewModelScope.launch {
            val controller = controllerFuture.await()
            playStation(controller, station)
        }
    }

    fun autoPlayDefaultStationIfIdle() {
        viewModelScope.launch {
            val controller = controllerFuture.await()
            if (controller.mediaItemCount == 0 && controller.playbackState == Player.STATE_IDLE) {
                controller.volume = AUTOPLAY_INITIAL_VOLUME
                playStation(controller, stations.first())
            }
        }
    }

    private fun playStation(controller: MediaController, station: RadioStation) {
        val metadata = MediaMetadata.Builder()
            .setTitle(getApplication<Application>().getString(station.nameResId))
            .build()
        val mediaItem = MediaItem.Builder()
            .setUri(station.url.toUri())
            .setMediaMetadata(metadata)
            .build()
        controller.setMediaItem(mediaItem)
        controller.prepare()
        controller.play()
    }

    fun togglePlayPause() {
        viewModelScope.launch {
            val controller = controllerFuture.await()
            if (controller.isPlaying) {
                controller.pause()
            } else {
                controller.play()
            }
        }
    }

    fun stop() {
        viewModelScope.launch {
            val controller = controllerFuture.await()
            controller.stop()
            controller.clearMediaItems()
        }
    }

    override fun onCleared() {
        super.onCleared()
        activeController?.removeListener(playerListener)
        MediaController.releaseFuture(controllerFuture)
    }
}
