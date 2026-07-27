package com.craiovadata.rfiplayer

import android.net.Uri
import androidx.compose.runtime.*
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.guava.await

@Composable
fun rememberPlayerState(controllerFuture: ListenableFuture<MediaController>): PlayerState {
    var controller by remember { mutableStateOf<MediaController?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentMediaUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(controllerFuture) {
        val pc = controllerFuture.await()
        controller = pc
        isPlaying = pc.isPlaying
        currentMediaUri = pc.currentMediaItem?.localConfiguration?.uri
        
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                currentMediaUri = mediaItem?.localConfiguration?.uri
            }
        }
        pc.addListener(listener)
    }

    return remember(isPlaying, currentMediaUri) {
        PlayerState(isPlaying, currentMediaUri)
    }
}

data class PlayerState(
    val isPlaying: Boolean,
    val currentMediaUri: Uri?
)
