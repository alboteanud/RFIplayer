package com.craiovadata.rfiplayer

import android.net.Uri
import androidx.compose.runtime.*
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.guava.await

@Composable
fun rememberPlayerState(controllerFuture: ListenableFuture<MediaController>): PlayerState {
    var controller by remember { mutableStateOf<MediaController?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var currentMediaUri by remember { mutableStateOf<Uri?>(null) }
    var title by remember { mutableStateOf<String?>(null) }
    var subtitle by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(controllerFuture) {
        val pc = controllerFuture.await()
        controller = pc
        
        fun syncState() {
            isPlaying = pc.isPlaying
            isLoading = pc.playbackState == Player.STATE_BUFFERING
            
            val metadata = pc.mediaMetadata
            title = metadata.title?.toString() ?: metadata.displayTitle?.toString()
            subtitle = metadata.subtitle?.toString() ?: metadata.artist?.toString() ?: metadata.albumArtist?.toString()

            // In STATE_IDLE (after stop), we want to clear the selection highlight
            currentMediaUri = if (pc.playbackState == Player.STATE_IDLE) {
                null
            } else {
                pc.currentMediaItem?.localConfiguration?.uri
            }
        }

        // Initial sync
        syncState()

        val listener = object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                syncState()
            }

            override fun onPlayerError(error: PlaybackException) {
                errorMessage = error.localizedMessage ?: "Playback error"
                isLoading = false
            }

            override fun onIsPlayingChanged(playing: Boolean) {
                if (playing) errorMessage = null
            }
        }
        
        pc.addListener(listener)
    }

    return remember(controller, isPlaying, isLoading, currentMediaUri, title, subtitle, errorMessage) {
        PlayerState(controller, isPlaying, isLoading, currentMediaUri, title, subtitle, errorMessage)
    }
}

data class PlayerState(
    val player: Player?,
    val isPlaying: Boolean,
    val isLoading: Boolean,
    val currentMediaUri: Uri?,
    val title: String? = null,
    val subtitle: String? = null,
    val errorMessage: String? = null
)
