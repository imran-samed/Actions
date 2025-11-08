package com.stpauls.dailyliturgy.popularHymns

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.daycareteacher.others.Toaster
import com.stpauls.dailyliturgy.Global
import com.stpauls.dailyliturgy.R
import com.stpauls.dailyliturgy.base.BaseDialog
import com.stpauls.dailyliturgy.base.MyProgressDialog
import com.stpauls.dailyliturgy.localDb.AppDataBase
import com.stpauls.dailyliturgy.mp3Downloader.FetchHelper
import com.stpauls.dailyliturgy.mp3Downloader.PlayMedia
import com.stpauls.dailyliturgy.others.Callback
import com.stpauls.dailyliturgy.others.NotificationUtils
import com.stpauls.dailyliturgy.popularHymns.bean.PopularHymnsBean

class SongAdapter(
    val context: Context,
    val callback: Callback<PopularHymnsBean?>
) :

    RecyclerView.Adapter<SongAdapter.Holder>() {


    private var data: MutableList<PopularHymnsBean> = arrayListOf()
    val dialog: MyProgressDialog? = MyProgressDialog(context, "Downloading audio...")
    private val TAG = "SongAdapter"

    private val ICON_DONWLOAD = R.drawable.ic_down_arrow
    private val ICON_PAUSE = R.drawable.ic_pause
    private val ICON_PLAY = R.drawable.ic_play

    val icons = listOf(ICON_DONWLOAD, ICON_PLAY, ICON_PAUSE)

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivDownload = itemView.findViewById<ImageView>(R.id.ivDownload)
        val tvSongName = itemView.findViewById<TextView>(R.id.tvSongName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)
        return Holder(view)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return data.size ?: 0
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = data[position]
        holder.tvSongName.text = item.mAudioName

        holder.tvSongName.isSelected = true

        holder.ivDownload.setImageResource(if (item.isFileAvailable) ICON_PLAY else ICON_DONWLOAD)
        holder.ivDownload.setOnClickListener {
            // if song is already downloaded and available in local storage
            if (item.isFileAvailable) {
                val adapPos = holder.adapterPosition
                // check is list is not empty
                if (!data.isNullOrEmpty() && adapPos < data.size ?: -1) {

                    // check if this song is already playing
                    if (Global.PLAYING_POSITION == item.mId) {
                        PlayMedia.stop()
                        NotificationUtils.clearNotification(context)
                        notifyItemChanged(adapPos)
                        // return from here
                        return@setOnClickListener
                    }
                    PlayMedia.createMediaSession(context, item.mAudioName)
                    // update playing position
                    Global.PLAYING_POSITION = item.mId


                    PlayMedia.play(data[adapPos], object : Callback<PlayMedia.MEDIA_STATE>() {
                        override fun onSuccess(t: PlayMedia.MEDIA_STATE) {
                            // update list

                            when(t){
                                // audio start playing
                                PlayMedia.MEDIA_STATE.START->{

                                    //show notification
                                    NotificationUtils.createNotification(
                                        context,
                                        item.mAudioName,
                                        PlayMedia.getMediaSession()
                                    )
                                    Global.PLAYING_POSITION_OF_LIST_ITEM = adapPos
                                }

                                // some error while playing audio file
                                PlayMedia.MEDIA_STATE.ERROR->{
                                    PlayMedia.reset()
                                    NotificationUtils.clearNotification(context)
                                }


                                // audio playing is complete
                                PlayMedia.MEDIA_STATE.COMPLETE -> {
                                    PlayMedia.playNext(context)
                                }
                            }

                            notifyDataSetChanged()

                        }
                    })
                }
            }
            // song is not downloaded yet
            // so download song
            else {
                // show dialog, so user cant do anything on screen
                dialog?.showDialog()
                // param @mAudioFileUrl remote path of audio file
                // param @callback to get callback when song is successfully downloaded or ny other issue

                FetchHelper.getInstance()
                    .downloadUrl(context, item.mAudioFileUrl, object : Callback<Boolean>() {
                        override fun onSuccess(isSuccess: Boolean) {
                            dialog?.dismissDialog()
                            if(isSuccess){
                                AppDataBase.saveDownloadFileDetail(item)
                                callback.onSuccess(item)
                                notifyDataSetChanged()
                            } else {
                                callback.onSuccess(null)
                                Toaster.shortToast("Something went wrong !!")
                            }
                        }
                    })
            }
        }

        if (item.isFileAvailable) {
            if (Global.PLAYING_POSITION == item.mId) {
                holder.ivDownload.setImageResource(ICON_PAUSE)
            } else {
                holder.ivDownload.setImageResource(ICON_PLAY)
            }
        }
    }

    fun notifyData(mutableList: MutableList<PopularHymnsBean>?) {
        if (mutableList.isNullOrEmpty()) {
            return
        }
        val items = ArrayList(mutableList)
        PlayMedia.songList(items)
        data.clear()
        data.addAll(items)
        notifyDataSetChanged()
    }
}


fun BaseDialog?.dismissDialog(){
    try{
        this?.dismiss()
    }catch (e: Exception){}
}

fun BaseDialog?.showDialog(){
    try{
        this?.show()
    }catch (e: Exception){}
}