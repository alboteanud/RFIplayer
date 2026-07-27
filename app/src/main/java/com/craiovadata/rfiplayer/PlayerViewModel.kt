package com.craiovadata.rfiplayer

import android.app.Application
import android.content.ComponentName
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private val controller: MediaController?
        get() = try {
            if (controllerFuture?.isDone == true) controllerFuture?.get() else null
        } catch (e: Exception) {
            null
        }

    private var pendingPlayUrl: String? = null

    init {
        val sessionToken = SessionToken(
            application,
            ComponentName(application, AudioService::class.java)
        )
        val future = MediaController.Builder(application, sessionToken).buildAsync()
        controllerFuture = future
        
        future.addListener({
            pendingPlayUrl?.let { url ->
                play(url)
                pendingPlayUrl = null
            }
        }, MoreExecutors.directExecutor())
    }

    fun play(url: String) {
        val currentController = controller
        if (currentController != null) {
            currentController.setMediaItem(MediaItem.fromUri(url))
            currentController.prepare()
            currentController.play()
        } else {
            pendingPlayUrl = url
        }
    }

    fun stop() {
        pendingPlayUrl = null
        controller?.stop()
    }

    override fun onCleared() {
        super.onCleared()
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
    }
}
