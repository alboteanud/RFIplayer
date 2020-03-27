package com.craiovadata.rfiplayer

//import com.google.android.exoplayer2.ExoPlayerFactory

//import com.google.android.exoplayer2.ExoPlayerFactory
//import com.google.android.exoplayer2.source.ExtractorMediaSource

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util


private const val ACTION_PLAY = "com.craiovadata.rfiplayer.action.PLAY"
private const val ACTION_TOGGLE_PLAYER = "com.craiovadata.rfiplayer.action.TOGGLE"
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
            ACTION_TOGGLE_PLAYER -> {
                handleActionTogglePlayerState()
            }
        }
        return START_STICKY
    }

    private fun handleActionTogglePlayerState() {
        if (player == null) {
            handleActionPlay()
        } else {
            handleActionStop()
        }
    }

    private fun handleActionPlay() {
        val shouldPlayHq = getSharedPreferences("_", Context.MODE_PRIVATE)
            .getBoolean(PREF_KEY_PLAY_HQ, true)
        val url: String
        url = if (shouldPlayHq) getString(R.string.url_128)
        else getString(R.string.url_48)
        initializePlayer(url)
        buildNotification(shouldPlayHq)
    }

    private fun handleActionStop() {
        releasePlayer()
        stopForeground(true)
        stopSelf()
    }

    private fun initializePlayer(url: String) {
        if (player == null) {
            player = SimpleExoPlayer.Builder(this).build()
//            player = ExoPlayerFactory.newSimpleInstance(this)
            player?.playWhenReady = true
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .build()
            player?.setAudioAttributes(audioAttributes, true)
        }
        val mediaSource = buildMediaSource(Uri.parse(url))
        player?.prepare(mediaSource)
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }

    private fun buildMediaSource(uri: Uri): MediaSource {


        // Produces DataSource instances through which media data is loaded.
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
            this,
            Util.getUserAgent(this, "yourApplicationName")
        )
// This is the MediaSource representing the media to be played.
        // This is the MediaSource representing the media to be played.
        val videoSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(uri)

        return videoSource

// Prepare the player with the source.
        // Prepare the player with the source.
//        player!!.prepare(videoSource)


//        return ExtractorMediaSource.Factory(
//            DefaultHttpDataSourceFactory("rfi_player")
//        ).createMediaSource(uri)
    }

    private var stopReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Stop the service when the notification is tapped
            unregisterReceiver(this)
            stopSelf()
        }
    }

    private fun buildNotification(shouldPlayHq: Boolean) {
        val stop = "stop"
        registerReceiver(stopReceiver, IntentFilter(stop))
        val broadcastIntent = PendingIntent.getBroadcast(
            this, 0, Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notifTitle = getString(R.string.notif_title)
        val playFormat = if (shouldPlayHq) getString(R.string.text_128_kbps)
        else getString(R.string.text_48_kbps)

        val notifContent = String.format(
            getString(R.string.notif_text),
            playFormat
        )

//        val largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.logo_rfi)
        val chanel_id = getString(R.string.norif_channel_id)

        val builder = NotificationCompat.Builder(this, chanel_id)
            .setSmallIcon(R.drawable.ic_notif)
//            .setLargeIcon(largeIcon)
            .setContentTitle(notifTitle)
            .setContentText(notifContent)
            .setColor(getColor(R.color.colorPrimary))
            .setAutoCancel(true)
//            .setOngoing(true)
            .setContentIntent(broadcastIntent)
//            .addAction(
//                android.R.drawable.ic_media_play,
//                getString(R.string.notif_action_play),
//                getPendingIntentToService(ACTION_PLAY)
//            )

        val notificationManager =
            getSystemService(Activity.NOTIFICATION_SERVICE) as NotificationManager

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


    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    companion object {

        const val PREF_KEY_PLAY_HQ = "key_play_hq"

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

        @JvmStatic
        fun getPendingIntentTogglePlayerState(context: Context): PendingIntent? {
            val intent = Intent(context, MyService::class.java)
            intent.action = ACTION_TOGGLE_PLAYER
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return PendingIntent.getForegroundService(context, 0, intent, 0)
            }
            return PendingIntent.getService(context, 0, intent, 0)
        }
    }

}
