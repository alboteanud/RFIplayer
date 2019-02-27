package com.craiovadata.rfiplayer

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory

private const val ACTION_PLAY = "com.craiovadata.rfiplayer.action.PLAY"
private const val ACTION_TOGGLE_STATE = "com.craiovadata.rfiplayer.action.TOGGLE_STATE"
private const val ACTION_STOP = "com.craiovadata.rfiplayer.action.STOP"

class MyService : Service() {

    private var player: SimpleExoPlayer? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action

        when (action) {
            ACTION_PLAY, null -> {
                handleActionPlay()
            }
            ACTION_STOP -> {
                handleActionStop()
            }
            ACTION_TOGGLE_STATE -> {
                handleActionToggleState()
            }
        }

        return START_STICKY
    }

    private fun handleActionPlay() {
        val startLowPlay = getSavedPlayMode(this)

        if (startLowPlay) {
            initializePlayer(getString(R.string.url_48))
            showNotification(getString(R.string.text_48_kbps))
        } else {
            initializePlayer(getString(R.string.url_128))
            showNotification(getString(R.string.text_128_kbps))
        }
    }

    private fun handleActionStop() {
        releasePlayer()
        showNotification("")
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
    }

    private fun handleActionToggleState() {
        if (player == null) {
            handleActionPlay()
        } else {
            handleActionStop()
        }
    }

    private fun initializePlayer(url: String) {
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(this)
            player?.setPlayWhenReady(true)
        }

        val mediaSource = buildMediaSource(Uri.parse(url))
        player?.prepare(mediaSource)
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        return ExtractorMediaSource.Factory(
            DefaultHttpDataSourceFactory("rfi_player")
        ).createMediaSource(uri)
    }

    private fun showNotification(txt: String) {

//        val largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.logo_rfi)
        val chanel_id = getString(R.string.norif_channel_id)

        val builder = NotificationCompat.Builder(this, chanel_id)
            .setSmallIcon(R.drawable.ic_notif)
//            .setLargeIcon(largeIcon)
            .setContentTitle(getString(R.string.notif_title))
            .setContentText(txt)
            .setColor(getColor(R.color.colorPrimary))
//            .setAutoCancel(true)
//            .setOngoing(true)
            .setContentIntent(getPendingIntentToActivity())
            .addAction(
                android.R.drawable.ic_media_play,
                getString(R.string.notif_action_play),
                getPendingIntentToService(ACTION_PLAY)
            )
            .addAction(
                android.R.drawable.ic_media_pause,
                getString(R.string.notif_action_stop),
                getPendingIntentToService(ACTION_STOP)
            )

        val notificationManager = getSystemService(Activity.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                chanel_id,
                getString(R.string.notif_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = builder.build()
        startForeground(1, notification)
    }

    private fun getPendingIntentToService(action_name: String): PendingIntent {
        val intent = Intent(this, MyService::class.java)
        intent.action = action_name

        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getPendingIntentToActivity(): PendingIntent? {
        val intentToActivity = Intent(this, MainActivity::class.java)

        val pendingIntentToActivity: PendingIntent? = TaskStackBuilder.create(this)
            .addNextIntentWithParentStack(intentToActivity)
            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        return pendingIntentToActivity
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    companion object {

        @JvmStatic
        fun startActionTogglePlay(context: Context) {
            val intent = Intent(context, MyService::class.java)
            intent.action = ACTION_TOGGLE_STATE

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        @JvmStatic
        fun savePlayMode(context: Context, isLowMode: Boolean) {
            val key = context.getString(R.string.key_pref_low_mode)
            val editor = context.getSharedPreferences("_", Context.MODE_PRIVATE).edit()
            editor.putBoolean(key, isLowMode).apply()
        }

        @JvmStatic
        fun getSavedPlayMode(context: Context): Boolean {
            val pref = context.getSharedPreferences("_", Context.MODE_PRIVATE)
            val key = context.getString(R.string.key_pref_low_mode)
            return pref.getBoolean(key, true)
        }

        @JvmStatic
        fun startActionPlay(context: Context) {

            val intent = Intent(context, MyService::class.java)
            intent.action = ACTION_PLAY

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }

        }

    }

}
