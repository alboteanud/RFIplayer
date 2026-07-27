package com.craiovadata.rfiplayer

import android.app.Application
import android.content.ComponentName
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val controllerFuture = MediaController.Builder(
        application,
        SessionToken(application, ComponentName(application, AudioService::class.java))
    ).buildAsync()

    var isPlaying by mutableStateOf(false)
        private set

    var currentStationUrl by mutableStateOf<String?>(null)
        private set

    init {
        viewModelScope.launch {
            val controller = controllerFuture.await()
            controller.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(playing: Boolean) {
                    isPlaying = playing
                    if (!playing) {
                        currentStationUrl = null
                    }
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    currentStationUrl = mediaItem?.localConfiguration?.uri?.toString()
                }
            })
            // Update initial state
            isPlaying = controller.isPlaying
            currentStationUrl = controller.currentMediaItem?.localConfiguration?.uri?.toString()
        }
    }

    fun play(url: String) {
        viewModelScope.launch {
            val controller = controllerFuture.await()
            controller.setMediaItem(MediaItem.fromUri(url))
            controller.prepare()
            controller.play()
        }
    }

    fun stop() {
        viewModelScope.launch {
            val controller = controllerFuture.await()
            controller.stop()
        }
    }

    override fun onCleared() {
        super.onCleared()
        MediaController.releaseFuture(controllerFuture)
    }
}
