package com.craiovadata.rfiplayer

import android.net.Uri
import androidx.compose.runtime.*
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.guava.await

@Composable
fun rememberPlayerState(controllerFuture: ListenableFuture<MediaController>): PlayerState {
    var isPlaying by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var currentMediaUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(controllerFuture) {
        val pc = controllerFuture.await()
        isPlaying = pc.isPlaying
        isLoading = pc.playbackState == Player.STATE_BUFFERING
        currentMediaUri = pc.currentMediaItem?.localConfiguration?.uri
        
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
                if (playing) errorMessage = null
            }
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                currentMediaUri = mediaItem?.localConfiguration?.uri
            }
            override fun onPlaybackStateChanged(playbackState: Int) {
                isLoading = playbackState == Player.STATE_BUFFERING
            }
            override fun onPlayerError(error: PlaybackException) {
                errorMessage = error.localizedMessage ?: "Playback error"
                isLoading = false
            }
        }
        pc.addListener(listener)
    }

    return remember(isPlaying, isLoading, currentMediaUri, errorMessage) {
        PlayerState(isPlaying, isLoading, currentMediaUri, errorMessage)
    }
}

data class PlayerState(
    val isPlaying: Boolean,
    val isLoading: Boolean,
    val currentMediaUri: Uri?,
    val errorMessage: String? = null
)
