package com.craiovadata.rfiplayer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class AudioService : Service() {
    private var player: Player? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_PLAY) {
            if (player == null) {
                player = ExoPlayer.Builder(this).build()
                player?.setAudioAttributes(AudioAttributes.DEFAULT, /* handleAudioFocus= */ true);
                startForeground(NOTIFICATION_ID, createNotification())
            } else {
                player?.stop()
            }
            player?.apply {
                val url = intent.getStringExtra(URL)
                setMediaItem(MediaItem.fromUri(Uri.parse(url)))
                prepare()
                play()
            }
        } else if (intent?.action == ACTION_STOP) {
            player?.stop()
            player?.release()
            player = null
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
        return START_STICKY
    }

    private fun createNotification(): Notification {
        val contentIntent = PendingIntent.getActivity(
            this, REQUEST_CODE,
            Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE
        )
        val stopActionIntent = PendingIntent.getService(
            this, REQUEST_CODE,
            Intent(this, AudioService::class.java).setAction(ACTION_STOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_play)
            .setContentIntent(contentIntent)
            .setOngoing(true)
            .addAction(R.drawable.ic_stop, "Stop", stopActionIntent)

        return builder.build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Radio Player Controls",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Controller for Radio Player"
        }
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.stop()
        player?.release()
        player = null
        stopForeground(STOP_FOREGROUND_REMOVE)
    }


    private val binder = LocalBinder()
    inner class LocalBinder : android.os.Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): AudioService = this@AudioService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    companion object {
        private const val ACTION_PLAY = "com.craiovadata.rfiplayer.action.PLAY"
        private const val ACTION_STOP = "com.craiovadata.rfiplayer.action.STOP"
        private val url_rfi = "http://asculta.rfi.ro:9128/live.mp3"
        private val url_france_inter = "http://icecast.radiofrance.fr/franceinter-midfi.mp3"
//            "http://icecast.radiofrance.fr/franceinter-hifi.aac"
        private val url_itsy_bitzy = "http://live.itsybitsy.ro:8000/itsybitsy"
        private const val CHANNEL_ID = "com.craiovadata.rfiplayer.notification.CHANNEL_ID"
        private const val NOTIFICATION_ID = 99
        private const val REQUEST_CODE = 9
        private const val URL = "url"

        fun startActionPlayRFI(context: Context) {
            val intent = Intent(context, AudioService::class.java)
            intent.action = ACTION_PLAY
            intent.putExtra(URL, url_rfi)
            context.startForegroundService(intent)
        }

        fun startActionPlayItzyBitzy(context: Context) {
            val intent = Intent(context, AudioService::class.java)
            intent.action = ACTION_PLAY
            intent.putExtra(URL, url_itsy_bitzy)
            context.startForegroundService(intent)
        }

        fun startActionPlayFranceInter(context: Context) {
            val intent = Intent(context, AudioService::class.java)
            intent.action = ACTION_PLAY
            intent.putExtra(URL, url_france_inter)
            context.startForegroundService(intent)
        }

        fun startActionStop(context: Context) {
            val intent = Intent(context, AudioService::class.java)
            intent.action = ACTION_STOP
            context.startService(intent)
        }
    }
}
