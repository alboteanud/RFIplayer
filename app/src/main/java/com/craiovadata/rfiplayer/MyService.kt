package com.craiovadata.rfiplayer

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory


private const val ACTION_PLAY = "com.craiovadata.rfiplayer.action.PLAY"
private const val ACTION_PLAY_LOW = "com.craiovadata.rfiplayer.action.PLAY_LOW"
private const val ACTION_STOP = "com.craiovadata.rfiplayer.action.STOP"
private const val EXTRA_URL = "com.craiovadata.rfiplayer.extra.URL"
private const val PREF_LOW_Q_KEY = "low_pref"

class MyService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (intent?.action) {
            ACTION_PLAY -> {
                handleActionPlay()
            }
            ACTION_PLAY_LOW -> {
                handleActionPlayLow()
            }
            ACTION_STOP -> {
                handleActionStop()
            }
        }


        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    private fun handleActionPlay() {
        initializePlayer(url_128)
        createAndShowForegroundNotification("128 kbps")
        getSharedPreferences("_", Context.MODE_PRIVATE).edit().putBoolean(PREF_LOW_Q_KEY, false).apply()
    }

    private fun handleActionPlayLow() {
        initializePlayer(url_48)
        createAndShowForegroundNotification("48 kbps")
        getSharedPreferences("_", Context.MODE_PRIVATE).edit().putBoolean(PREF_LOW_Q_KEY, true).apply()
    }

    private fun handleActionStop() {
        releasePlayer()
    }

    private var player: SimpleExoPlayer? = null

    private fun initializePlayer(url: String) {
//        if (player != null) return

        releasePlayer()

        player = ExoPlayerFactory.newSimpleInstance(
            DefaultRenderersFactory(this),
            DefaultTrackSelector(),
            DefaultLoadControl()
        )
        player?.setPlayWhenReady(true)

        val mediaSource = buildMediaSource(Uri.parse(url))
        player?.prepare(mediaSource)
    }

    private fun releasePlayer() {
        if (player == null) return

        player?.release()
        player = null

    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        return ExtractorMediaSource.Factory(
            DefaultHttpDataSourceFactory("rfi_player")
        ).createMediaSource(uri)
    }


    private fun createAndShowForegroundNotification(notifTxt: String) {

        val intentDetailsActivity = Intent(this, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntentOpenActivity: PendingIntent? = TaskStackBuilder.create(this)
            // add all of DetailsActivity's parents to the stack,
            .addNextIntentWithParentStack(intentDetailsActivity)
            .getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT)

        val intentPlay = Intent(this, MyService::class.java).apply {
            action = ACTION_PLAY
        }
        val intentPlayLow = Intent(this, MyService::class.java).apply {
            action = ACTION_PLAY_LOW
        }

        val intentStop = Intent(this, MyService::class.java).apply {
            action = ACTION_STOP
        }

        val pendingIntentPlay: PendingIntent? = PendingIntent.getService(
            this, 0, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val pendingIntentPlayLow: PendingIntent? = PendingIntent.getService(
            this, 0, intentPlayLow, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val pendingIntentStop: PendingIntent? = PendingIntent.getService(
            this, 0, intentStop, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(this, channelId)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo_rfi))
//            .setContentTitle(getString(R.string.app_name))
//            .setContentTitle(notifTxt)
            .setContentText(notifTxt)
            .setContentIntent(pendingIntentOpenActivity)
            .addAction(android.R.drawable.ic_media_pause, "STOP", pendingIntentStop)
            .addAction(android.R.drawable.ic_media_play, "PLAY", pendingIntentPlay)
            .addAction(android.R.drawable.ic_media_play, "LOW", pendingIntentPlayLow)

        val notificationManager = getSystemService(Activity.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = builder.build()
        startForeground(1, notification)
    }


    companion object {

        @JvmStatic
        fun startActionPlay(context: Context) {
            val intent = Intent(context, MyService::class.java).apply {
                val isLowPlay = context.getSharedPreferences("_", Context.MODE_PRIVATE).getBoolean(PREF_LOW_Q_KEY, true)
                if (isLowPlay)
                    action = ACTION_PLAY_LOW
                else
                    action = ACTION_PLAY
            }

            //Start service:
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        @JvmStatic
        fun startActionStop(context: Context) {
            val intent = Intent(context, MyService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }

        val TAG = "MyService"
        val channelId = "com.craiovadata.rfiplayer.notification.CHANNEL_ID_FOREGROUND"

        private val url_48 = "http://asculta.rfi.ro:9128/live.aac"
        private val url_128 = "http://asculta.rfi.ro:9128/live.mp3"

    }


}
