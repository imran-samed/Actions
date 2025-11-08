package com.stpauls.dailyliturgy.mp3Downloader

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.daycareteacher.others.Toaster
import com.stpauls.dailyliturgy.Global
import com.stpauls.dailyliturgy.base.App
import com.stpauls.dailyliturgy.others.Callback
import com.stpauls.dailyliturgy.others.NotificationUtils
import com.stpauls.dailyliturgy.popularHymns.SongAdapter
import com.stpauls.dailyliturgy.popularHymns.bean.PopularHymnsBean
import java.io.File

object PlayMedia {

    private var mediaPlayer: MediaPlayer? = MediaPlayer()
    private var mediaListener: MyPlayerListener? = null


    //MEDIA SESSION
    private var mediaSessionCompact: MediaSessionCompat? = null
    private var stateBuilder: PlaybackStateCompat.Builder? = null
    var playingSong: PopularHymnsBean?= null

    private val items: MutableList<PopularHymnsBean> = mutableListOf()


    fun createMediaSession(context: Context, audioName: String) {
        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
        val pendingItent = PendingIntent.getBroadcast(
            context,
            0, mediaButtonIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        mediaSessionCompact = MediaSessionCompat(context, "PrayerCollection", null, pendingItent).also {
            it.isActive = true
        }
        stateBuilder = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PAUSE
                        or PlaybackStateCompat.ACTION_PLAY
                        or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                        or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            )
        mediaSessionCompact?.setMediaButtonReceiver(null)
        mediaSessionCompact?.setPlaybackState(stateBuilder?.build())
        mediaSessionCompact?.setCallback(object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                super.onPlay()
                //NotificationUtils.createNotification(context, audioName, mediaSessionCompact)
            }

            override fun onPause() {
                super.onPause()
                //NotificationUtils.clearNotification(context)
            }
        })
    }



    fun play(path: PopularHymnsBean, callback: Callback<MEDIA_STATE>) {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            mediaPlayer?.stop()
            mediaPlayer?.reset()
        }
        mediaListener = MyPlayerListener(callback)
        try {
            this.playingSong = path
            mediaPlayer?.setDataSource(App.get(), Uri.fromFile(File(path.getLocalFilePath())));
            mediaPlayer?.prepareAsync(); // might take long! (for buffering, etc)
            mediaPlayer?.setOnPreparedListener(mediaListener)
            mediaPlayer?.setOnErrorListener(mediaListener)
            mediaPlayer?.setOnCompletionListener(mediaListener)
            mediaPlayer?.setOnSeekCompleteListener(mediaListener)
        } catch (e: Exception) {
            playingSong = null
            reset()
            Toaster.shortToast("Error in playing audio...")
            callback.onSuccess(MEDIA_STATE.ERROR)
        }

    }


    enum class MEDIA_STATE {
        START, ERROR, COMPLETE
    }

    class MyPlayerListener(private var callback: Callback<MEDIA_STATE>) :
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnSeekCompleteListener {

        override fun onPrepared(player: MediaPlayer?) {
            player?.start()
            android.os.Handler(Looper.myLooper()!!).postDelayed({
                callback.onSuccess(MEDIA_STATE.START)
            }, 500)

        }

        override fun onError(player: MediaPlayer?, p1: Int, p2: Int): Boolean {
            resetPlayer(player)
            callback.onSuccess(MEDIA_STATE.ERROR)
            return false
        }

        override fun onCompletion(player: MediaPlayer?) {
            resetPlayer(player)
            callback.onSuccess(MEDIA_STATE.COMPLETE)
        }

        override fun onSeekComplete(player: MediaPlayer?) {
            resetPlayer(player)
            callback.onSuccess(MEDIA_STATE.COMPLETE)
        }

    }

    private fun resetPlayer(player: MediaPlayer?) {
        Global.PLAYING_POSITION = Global.NO_POSITION
        Global.PLAYING_POSITION_OF_LIST_ITEM = -2
        player?.reset()
    }

    fun stop() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            mediaPlayer?.stop()
        }
        resetPlayer(mediaPlayer)
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }

    fun getMediaSession(): MediaSessionCompat? {
        return mediaSessionCompact
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun play() {
        mediaPlayer?.start()
    }


    fun playNext(context: Context) {
        Global.PLAYING_POSITION_OF_LIST_ITEM += 1
        playCurrentPositionSong(Global.PLAYING_POSITION_OF_LIST_ITEM, context, true)
    }

    fun playPrev(context: Context) {
        Global.PLAYING_POSITION_OF_LIST_ITEM -= 1
        playCurrentPositionSong(Global.PLAYING_POSITION_OF_LIST_ITEM, context, false)
    }

    private fun playCurrentPositionSong(
        currentPositionToPLay: Int,
        context: Context,
        playNext: Boolean) {
        if (!items.isNullOrEmpty() && currentPositionToPLay in 0..items.indices.last) {
            val song = items[currentPositionToPLay]
            if (song.isFileAvailable) {
                Global.PLAYING_POSITION_OF_LIST_ITEM = currentPositionToPLay;
                Global.PLAYING_POSITION = song.mId
                play(song, object : Callback<MEDIA_STATE>() {
                    override fun onSuccess(t: MEDIA_STATE) {
                        when (t) {
                            MEDIA_STATE.COMPLETE -> {
                                playNext(context)
                            }
                            MEDIA_STATE.ERROR -> {
                                resetPlayer(mediaPlayer)
                                NotificationUtils.clearNotification(context)
                            }
                            MEDIA_STATE.START -> {
                                NotificationUtils.createNotification(
                                    context,
                                    song.mAudioName,
                                    getMediaSession()
                                )
                            }
                        }

                    }
                })
            } else {
                if (playNext) {
                    playNext(context)
                } else {
                    playPrev(context)
                }

            }
        } else {
            resetPlayer(mediaPlayer)
            NotificationUtils.clearNotification(context)
        }
    }

    fun songList(songs: ArrayList<PopularHymnsBean>) {
        this.items.clear()
        this.items.addAll(ArrayList(songs));
    }

    private var mAdapter: SongAdapter? = null
    fun setAdapter(adapter: SongAdapter?) {
        mAdapter = adapter
    }

    fun notifyAdapter() {
        mAdapter?.notifyDataSetChanged()
    }

    fun reset() {
        resetPlayer(mediaPlayer)
    }
}