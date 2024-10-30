package com.craiovadata.rfiplayer

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.graphics.Bitmap
import android.net.Uri
import android.os.IBinder
import androidx.core.app.NotificationCompat
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
        if (intent?.action == ACTION_PLAY_128) {
            if (player == null) {  player = ExoPlayer.Builder(this).build().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(C.USAGE_MEDIA)
                            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                            .build(), true
                    )
                    setMediaItem(MediaItem.fromUri(Uri.parse(url_128)))
                    playWhenReady = true
                    prepare()
                    initNotifManager()
                }
                val notification = createNotification()
                startForeground(NOTIFICATION_ID, notification)
            }
        } else if (intent?.action == ACTION_STOP) {
            player?.stop()
            player?.release()
            player = null
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
        return START_STICKY
    }

    private fun getMediaDescriptorAdapter(): PlayerNotificationManager.MediaDescriptionAdapter {
        val mediaDescriptionAdapter: PlayerNotificationManager.MediaDescriptionAdapter = object :
            PlayerNotificationManager.MediaDescriptionAdapter {
            override fun getCurrentContentTitle(player: Player): String {
                return getString(R.string.notif_title)
            }

            override fun getCurrentContentText(player: Player) = getString(R.string.text_128_kbps)

            override fun getCurrentLargeIcon(
                player: Player,
                callback: PlayerNotificationManager.BitmapCallback
            ): Bitmap? {
                return null
            }

            override fun createCurrentContentIntent(player: Player): PendingIntent? {
                return PendingIntent.getActivity(
                    this@AudioService,
                    REQUEST_CODE,
                    Intent(this@AudioService, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
            }
        }
        return mediaDescriptionAdapter
    }

    private fun initNotifManager() {
        notifManager = PlayerNotificationManager.Builder(
            this,
            NOTIFICATION_ID,
            CHANNEL_ID
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
//                setSmallIcon(R.mipmap.ic_launcher)
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
                    stopSelf()
                }

                override fun onNotificationPosted(
                    notificationId: Int,
                    notification: Notification,
                    ongoing: Boolean
                ) {
                    if (player != null) {
                        startForeground(
                            notificationId,
                            notification,
                            FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                        )
                    }
                }
            }
        return notificationListener
    }

    private fun createNotification(): Notification {
        val contentIntent = PendingIntent.getActivity(
            this, REQUEST_CODE,
            Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE
        )
        val stopActionIntent = PendingIntent.getService(
            this, 0,
            Intent(this, AudioService::class.java).setAction(ACTION_STOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("RFI Player")
            .setContentText("Playing...")
            .setSmallIcon(R.drawable.ic_play)
            .setContentIntent(contentIntent)
            .setOngoing(true)
            .addAction(R.drawable.ic_stop, "Stop", stopActionIntent)

        return builder.build()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.stop()
        player?.release()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    companion object {
        private const val ACTION_PLAY_128 = "com.craiovadata.rfiplayer.action.PLAY"
        private const val ACTION_STOP = "com.craiovadata.rfiplayer.action.STOP"
        private val url_128 = "http://asculta.rfi.ro:9128/live.mp3"
        private const val CHANNEL_ID = "com.craiovadata.rfiplayer.notification.CHANNEL_ID"
        private const val NOTIFICATION_ID = 99
        private const val REQUEST_CODE = 0

        fun startActionPlay128(context: Context) {
            val intent = Intent(context, AudioService::class.java)
            intent.action = ACTION_PLAY_128
            context.startForegroundService(intent)
        }

        fun startActionStop(context: Context) {
            val intent = Intent(context, AudioService::class.java)
            intent.action = ACTION_STOP
            context.startService(intent)
        }
    }

}
