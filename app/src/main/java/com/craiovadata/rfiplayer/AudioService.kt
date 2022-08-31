package com.craiovadata.rfiplayer

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import timber.log.Timber

class AudioService : Service() {

    private var player: ExoPlayer? = null
    private var notificationManager1: PlayerNotificationManager? = null
    private var mediaSession: MediaSessionCompat? = null
    private var notificationId: Int = 1
    private val logoLink =
        "https://www.rfi.ro/sites/all/themes/rfi/assets/img/logo-rfi-romania-baseline.png"
    private val url_48 = "http://asculta.rfi.ro:9128/live.aac"
    private val url_128 = "http://asculta.rfi.ro:9128/live.mp3"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY, null -> {
                handleActionPlay()
            }
            ACTION_STOP -> {
                handleActionStop()
            }
        }
        return START_STICKY
    }



    private var notification: Notification? = null

    private fun handleActionPlay() {

        if (player == null) {
            val shouldPlayHq = getSharedPreferences("_", Context.MODE_PRIVATE)
                .getBoolean(PREF_KEY_PLAY_HQ, true)
            val url: String =
                if (shouldPlayHq) url_128
                else url_48
            initializePlayer(url)
        }
        notifyAndStartForeground()
        player?.playWhenReady = true
    }

    private fun handleActionStop() {
        releasePlayer()
        stopForeground(true)
        stopSelf()
    }

    private fun initializePlayer(url: String) {
        player = ExoPlayer.Builder(this).build()
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()
        player?.setAudioAttributes(audioAttributes, true)

        player?.setMediaItem(
            MediaItem.fromUri(Uri.parse(url))
        )
        player?.prepare()
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }

    private fun notifyAndStartForeground() {
        if (notification != null) {
            startForeground(notificationId, notification)
        }

        val mediaDescriptionAdapter: PlayerNotificationManager.MediaDescriptionAdapter = object :
            PlayerNotificationManager.MediaDescriptionAdapter {
            override fun getCurrentContentTitle(player: Player): String {
                return getString(R.string.notif_title)
            }

            override fun getCurrentContentText(player: Player): String? {
                return "radio"
            }

            override fun getCurrentLargeIcon(
                player: Player,
                callback: PlayerNotificationManager.BitmapCallback
            ): Bitmap? {
//                val multi = MultiTransformation(CenterCrop())
                Glide.with(this@AudioService)
                    .asBitmap()
                    .load(logoLink)
//                    .apply(RequestOptions.bitmapTransform(multi))
                    .into(object : CustomTarget<Bitmap?>() {
                        override fun onLoadCleared(placeholder: Drawable?) {}
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap?>?
                        ) {
                            callback.onBitmap(resource)
                        }
                    })
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

        val notificationListener: PlayerNotificationManager.NotificationListener =
            object : PlayerNotificationManager.NotificationListener {
                override fun onNotificationCancelled(
                    notificationId: Int,
                    dismissedByUser: Boolean
                ) {
                    stopForeground(true)
                    stopSelf()
                    Timber.d(" onNotifCanceled()")
                }

                override fun onNotificationPosted(
                    notificationId: Int,
                    notification: Notification,
                    ongoing: Boolean
                ) {
                    this@AudioService.notification = notification
                    this@AudioService.notificationId = notificationId
                    startForeground(notificationId, notification)
                    Timber.d("onNotifPosted()")
                }
            }

        notificationManager1 = PlayerNotificationManager.Builder(
            this,
            notificationId,
            chanel_id
        )
            .setChannelNameResourceId(R.string.playback_channel_name)
            .setChannelDescriptionResourceId(R.string.playback_channel_description)
            .setMediaDescriptionAdapter(mediaDescriptionAdapter)
            .setNotificationListener(notificationListener)
            .build()

        notificationManager1?.apply {
            setUseNextAction(false)
            setUsePreviousAction(false)
            setUseFastForwardAction(false)
            //  setControlDispatcher(DefaultControlDispatcher(0,0) // Hide fast forward & rewind button );
            setSmallIcon(R.drawable.ic_notif)
            setPlayer(player)
            if (mediaSession != null && mediaSession!!.sessionToken != null) {
                setMediaSessionToken(mediaSession!!.sessionToken)
            }
        }
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
        private const val ACTION_PLAY = "com.craiovadata.rfiplayer.action.PLAY"
        private const val ACTION_STOP = "com.craiovadata.rfiplayer.action.STOP"
        private const val chanel_id = "com.craiovadata.rfiplayer.notification.CHANNEL_ID"

        fun startActionPlay(context: Context) {
            val intent = Intent(context, AudioService::class.java)
            intent.action = ACTION_PLAY

            context.startForegroundService(intent)
        }

    }

}
