package com.craiovadata.rfiplayer

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl

@UnstableApi
class AudioService : MediaSessionService() {
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                60_000,
                180_000,
                5_000,
                10_000
            )
            .build()
        val player = ExoPlayer.Builder(this)
            .setLoadControl(loadControl)
            .build().apply {
                setAudioAttributes(AudioAttributes.DEFAULT, /* handleAudioFocus= */ true)
            }
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(pendingIntent)
            .build()

        player.addListener(object : Player.Listener {

            override fun onPlaybackStateChanged(state: Int) {
                Log.d("PLAYER", "state=$state")
            }

            override fun onPlayWhenReadyChanged(
                playWhenReady: Boolean,
                reason: Int
            ) {
                Log.d("PLAYER", "playWhenReady=$playWhenReady reason=$reason")
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e("PLAYER", "error", error)
            }

            override fun onMediaItemTransition(
                mediaItem: MediaItem?,
                reason: Int
            ) {
                Log.d("PLAYER", "transition")
            }
        })
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player
        if (player != null) {
            if (!player.playWhenReady || player.mediaItemCount == 0) {
                stopSelf()
            }
        }
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
        }
        mediaSession = null
        super.onDestroy()
    }
}
