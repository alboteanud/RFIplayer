package com.craiovadata.rfiplayer

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory


private const val ACTION_PLAY = "com.craiovadata.rfiplayer.action.PLAY"
private const val ACTION_TOGGLE_PLAYER = "com.craiovadata.rfiplayer.action.TOGGLE"
private const val ACTION_STOP = "com.craiovadata.rfiplayer.action.STOP"
private const val EXTRA_HIGH_QUALITY_PLAY = "com.craiovadata.rfiplayer.extra.HQ"

class MyService : Service() {

    private var player: SimpleExoPlayer? = null
    private var playHQ: Boolean = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        when (action) {
            ACTION_PLAY, null -> {
                playHQ = intent?.getBooleanExtra(EXTRA_HIGH_QUALITY_PLAY, false) ?:false
                handleActionPlay()
            }
            ACTION_STOP -> {
                handleActionStop()
            }
            ACTION_TOGGLE_PLAYER -> {
                handleActionToggle()
            }
        }
        return START_STICKY
    }

    private fun handleActionToggle() {
        if (player==null){
            handleActionPlay()
        } else{
            handleActionStop()
        }
    }

    private fun handleActionPlay() {
        if (playHQ) {
            initializePlayer(getString(R.string.url_128))
            showNotification(getString(R.string.text_128_kbps))
        } else {
            initializePlayer(getString(R.string.url_48))
            showNotification(getString(R.string.text_48_kbps))
        }
    }

    private fun handleActionStop() {
        releasePlayer()
//        showNotification("")
//        stopForeground(STOP_FOREGROUND_DETACH)
        stopForeground(true)
        stopSelf()

    }

    private fun initializePlayer(url: String) {
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(this)
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
//            .addAction(
//                android.R.drawable.ic_media_play,
//                getString(R.string.notif_action_play),
//                getPendingIntentToService(ACTION_PLAY)
//            )
            .addAction(
                android.R.drawable.ic_media_pause,
                getString(R.string.notif_action_stop),
                buildPendingIntentStop(this)
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
        fun startActionPlay(context: Context, playHQ: Boolean) {

            val intent = Intent(context, MyService::class.java)
            intent.action = ACTION_PLAY
            intent.putExtra(EXTRA_HIGH_QUALITY_PLAY, playHQ)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        @JvmStatic
        fun buildPendingIntentToggle(context: Context): PendingIntent? {
            val intent = Intent(context, MyService::class.java)
            intent.action = ACTION_TOGGLE_PLAYER
//            intent.putExtra(EXTRA_HIGH_QUALITY_PLAY, playHQ)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return PendingIntent.getForegroundService(context, 0, intent, 0)
            }
            return PendingIntent.getService(context, 0, intent, 0)

        }

        @JvmStatic
        fun buildPendingIntentStop(context: Context): PendingIntent? {
            val intent = Intent(context, MyService::class.java)
            intent.action = ACTION_STOP
            return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }




    }




}
