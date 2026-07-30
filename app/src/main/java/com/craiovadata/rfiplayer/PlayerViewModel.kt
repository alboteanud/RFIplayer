package com.craiovadata.rfiplayer

import android.app.Application
import android.content.ComponentName
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    val controllerFuture = MediaController.Builder(
        application,
        SessionToken(application, ComponentName(application, AudioService::class.java))
    ).buildAsync()

    val stations = listOf(
        RadioStation(R.string.rfi, "http://asculta.rfi.ro:9128/live.mp3"),
        RadioStation(R.string.inter, "http://icecast.radiofrance.fr/franceinter-midfi.mp3"),
//        RadioStation(R.string.inter, "http://icecast.radiofrance.fr/franceinter-hifi.aac"),
        RadioStation(R.string.itzy_bitzy, "http://live.itsybitsy.ro:8000/itsybitsy")
    )

    fun play(station: RadioStation) {
        viewModelScope.launch {
            val controller = controllerFuture.await()
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
