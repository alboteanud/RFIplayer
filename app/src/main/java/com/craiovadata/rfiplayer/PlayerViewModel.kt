package com.craiovadata.rfiplayer

import android.app.Application
import android.content.ComponentName
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    val controllerFuture = MediaController.Builder(
        application,
        SessionToken(application, ComponentName(application, AudioService::class.java))
    ).buildAsync()

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
            controllerFuture.await().stop()
        }
    }

    override fun onCleared() {
        super.onCleared()
        MediaController.releaseFuture(controllerFuture)
    }
}
