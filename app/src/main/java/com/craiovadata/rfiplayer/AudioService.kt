package com.craiovadata.rfiplayer

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.IBinder
import androidx.core.app.ServiceCompat.stopForeground
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ui.PlayerNotificationManager

class AudioService : Service() {
    private var player: ExoPlayer? = null
    private var notifManager: PlayerNotificationManager? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY_128 -> handleActionPlay(url_128)
            ACTION_PLAY_48 -> handleActionPlay(url_48)
            ACTION_STOP -> handleActionStop()
        }
        return START_STICKY
    }

    private fun handleActionPlay(newUrl: String) {
        initPlayerIfNeeded()
        player?.apply {
            if (currentUrl != newUrl || !isPlaying)
                setMediaItem(MediaItem.fromUri(Uri.parse(newUrl)))
            currentUrl = newUrl
            prepare()
            play()
            initNotifManagerIfNeeded()
        }
    }

    private fun initPlayerIfNeeded() {
        if (player != null) return
        player = ExoPlayer.Builder(this).build()
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()
        player?.setAudioAttributes(audioAttributes, true)
    }

    private fun handleActionStop() {
        player?.pause()
//        stopForeground(STOP_FOREGROUND_DETACH)
    }

    private fun getMediaDescriptorAdapter(): PlayerNotificationManager.MediaDescriptionAdapter {
        val mediaDescriptionAdapter: PlayerNotificationManager.MediaDescriptionAdapter = object :
            PlayerNotificationManager.MediaDescriptionAdapter {
            override fun getCurrentContentTitle(player: Player): String {
                return getString(R.string.notif_title)
            }

            override fun getCurrentContentText(player: Player): String? {
                return getCurrentContentText()?.let { getString(it) }
            }

            override fun getCurrentLargeIcon(
                player: Player,
                callback: PlayerNotificationManager.BitmapCallback
            ): Bitmap? {
                return null
            }

            override fun createCurrentContentIntent(player: Player): PendingIntent? {
                val notifyIntent = Intent(this@AudioService, MainActivity::class.java)
                notifyIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                return PendingIntent.getActivity(
                    this@AudioService,
                    0,
                    notifyIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            }
        }
        return mediaDescriptionAdapter
    }

    private fun initNotifManagerIfNeeded() {
        if (notifManager != null) return
        val notificationId = 9
        notifManager = PlayerNotificationManager.Builder(
            this,
            notificationId,
            chanel_id
        )
            .setChannelNameResourceId(R.string.playback_channel_name)
            .setChannelDescriptionResourceId(R.string.playback_channel_description)
            .setMediaDescriptionAdapter(getMediaDescriptorAdapter())
            .setNotificationListener(getNotifListener())
            .build()
            .apply {
                setUseNextAction(false)
                setUsePreviousAction(false)
                setUseFastForwardAction(false)
                setSmallIcon(R.drawable.ic_notif)
                setPlayer(player)
            }
    }

    private fun getNotifListener(): PlayerNotificationManager.NotificationListener {
        val notificationListener: PlayerNotificationManager.NotificationListener =
            object : PlayerNotificationManager.NotificationListener {
                override fun onNotificationCancelled(
                    notificationId: Int,
                    dismissedByUser: Boolean
                ) {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }

                override fun onNotificationPosted(
                    notificationId: Int,
                    notification: Notification,
                    ongoing: Boolean
                ) {
                    startForeground(notificationId, notification)
                }
            }
        return notificationListener
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    companion object {

        private const val ACTION_PLAY_48 = "com.craiovadata.rfiplayer.action.PLAY_48_kbps"
        private const val ACTION_PLAY_128 = "com.craiovadata.rfiplayer.action.PLAY_128_kbps"
        private const val ACTION_STOP = "com.craiovadata.rfiplayer.action.STOP"
        private const val chanel_id = "com.craiovadata.rfiplayer.notification.CHANNEL_ID"
        private val url_48 = "http://asculta.rfi.ro:9128/live.aac"
        private val url_128 = "http://asculta.rfi.ro:9128/live.mp3"
        private var currentUrl: String? = null

        fun startActionPlay128(context: Context) {
            val intent = Intent(context, AudioService::class.java)
            intent.action = ACTION_PLAY_128
            context.startForegroundService(intent)
        }

        fun startActionPlay48(context: Context) {
            val intent = Intent(context, AudioService::class.java)
            intent.action = ACTION_PLAY_48
            context.startForegroundService(intent)
        }

        fun getCurrentContentText(): Int? {
            return when (currentUrl) {
                url_128 -> R.string.text_128_kbps
                url_48 -> R.string.text_48_kbps
                else -> null
            }
        }

    }

}
