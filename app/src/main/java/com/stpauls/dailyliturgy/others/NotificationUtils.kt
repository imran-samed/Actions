package com.stpauls.dailyliturgy.others

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaMetadata
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import com.stpauls.dailyliturgy.R
import com.stpauls.dailyliturgy.mp3Downloader.PlayMedia
import com.stpauls.dailyliturgy.popularHymns.PopularHymnsActivity


object NotificationUtils {

    fun createNotification(
        context: Context?,
        mAudioName: String?,
        mediaSessionCompact: MediaSessionCompat?
    ) {
        if (context == null){
            return
        }
        val intent = Intent(context, PopularHymnsActivity::class.java)
        val pi: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
        }else{
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }


        val icon: Int
        val playPause: String
        if (PlayMedia.isPlaying()) {
            icon = R.drawable.ic_pause
            playPause = "Pause"
        } else {
            icon = R.drawable.ic_play
            playPause = "Play"
        }
        val playPauseAction = NotificationCompat.Action(
            icon, playPause,
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                context,
                PlaybackStateCompat.ACTION_PLAY_PAUSE
            )
        )

        val next = NotificationCompat.Action(
            R.drawable.ic_next, "Next",
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                context,
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT
            )
        )

        val prev = NotificationCompat.Action(
            R.drawable.ic_previous, "Previous",
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                context,
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            )
        )


        //These two lines work
        // to remove Seekbar and Time from Progress Bar
        val mediaMetadata =
            MediaMetadata.Builder().putLong(MediaMetadata.METADATA_KEY_DURATION, -1L).build()
        mediaSessionCompact?.setMetadata(MediaMetadataCompat.fromMediaMetadata(mediaMetadata))

        val channelId = "com.godsword.notifications"
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(mAudioName)
            .setContentIntent(pi)
            .setOngoing(true)
            .setVibrate(null)
            .setAutoCancel(false)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(playPauseAction)
            .addAction(prev)
            .addAction(next)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSessionCompact?.sessionToken)
                    .setShowActionsInCompactView(0)
            )
            .setColorized(false)


        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Prayer's",
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description = "To play audio of Prayer"
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

    fun clearNotification(context: Context) {
        // Clear all notification
        val nMgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        nMgr?.cancelAll()
    }


}