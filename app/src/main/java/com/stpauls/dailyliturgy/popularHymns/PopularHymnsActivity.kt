package com.stpauls.dailyliturgy.popularHymns

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.widget.Toolbar
import androidx.media.session.MediaButtonReceiver
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.stpauls.dailyliturgy.Global.POPULAR_HYMNS_FOLDER_PATH
import com.stpauls.dailyliturgy.R
import com.stpauls.dailyliturgy.base.BaseActivity
import com.stpauls.dailyliturgy.base.BaseResponse
import com.stpauls.dailyliturgy.base.SharedPref
import com.stpauls.dailyliturgy.commonPrayers.CommonPrayerHeadingAdapter
import com.stpauls.dailyliturgy.commonPrayers.PrayerCollection
import com.stpauls.dailyliturgy.localDb.AppDataBase
import com.stpauls.dailyliturgy.mp3Downloader.FetchHelper
import com.stpauls.dailyliturgy.mp3Downloader.PlayMedia
import com.stpauls.dailyliturgy.others.NotificationUtils
import com.stpauls.dailyliturgy.popularHymns.bean.PopularHymnsBean
import com.stpauls.dailyliturgy.retrofit.RetrofitClient
import com.stpauls.dailyliturgy.databinding.ActivitySongListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PopularHymnsActivity : BaseActivity() {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, PopularHymnsActivity::class.java))
        }
    }

    val map = hashMapOf<String, MutableList<PopularHymnsBean>?>()

    private var swLayout: SwipeRefreshLayout? = null
    private lateinit var binding: ActivitySongListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySongListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        ivGifImage = binding.rlTopView.ivGifImage
        binding.ivHome.setOnClickListener { onBackPressed() }
        loadGif()
        val toolbar = binding.appbar
        toolbar.setNavigationOnClickListener { onBackPressed() }

        backgroundThread.execute {
            val list = AppDataBase.get().popularHymnDao().allHymns
            parseListToMap(list)
            mainThread.execute {
                getSong(!list.isNullOrEmpty())
            }

        }


    }

    private var mAdapter: SongAdapter? = null
    private fun setAdapter(headers: MutableList<PrayerCollection.PrayerHeadingBean>) {
        val items = ArrayList(map[headers[0].name])
        mainThread.execute {

            (binding.rvPrayerHeading.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            (binding.rvPrayer.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

            mAdapter = SongAdapter(
                this@PopularHymnsActivity,
                object : com.stpauls.dailyliturgy.others.Callback<PopularHymnsBean?>() {
                    override fun onSuccess(t: PopularHymnsBean?) {
                        mAdapter?.notifyDataSetChanged()
                    }
                })
            binding.rvPrayer.adapter = mAdapter
            mAdapter?.notifyData(items)
            PlayMedia.setAdapter(mAdapter)
            binding.rvPrayerHeading.adapter = CommonPrayerHeadingAdapter(
                headers, object : com.stpauls.dailyliturgy.others.Callback<String>() {
                    override fun onSuccess(title: String) {
                        mAdapter?.notifyData(map[title])
                    }
                })
        }


    }

    private val TAG = "PopularHymnsActivity"
    private fun getSong(needUpdate: Boolean) {
        val files = FetchHelper.getInstance().getListFiles(POPULAR_HYMNS_FOLDER_PATH)
        Log.e(TAG, "getSong: ${files?.toString()}")
        RetrofitClient.getRetrofitApi().getSong(SharedPref.getUUid(), if (needUpdate) "1" else "0").enqueue(object :
            Callback<BaseResponse<MutableList<PopularHymnsBean>>?> {
            override fun onFailure(
                call: Call<BaseResponse<MutableList<PopularHymnsBean>>?>,
                t: Throwable
            ) {

            }

            override fun onResponse(
                call: Call<BaseResponse<MutableList<PopularHymnsBean>>?>,
                response: Response<BaseResponse<MutableList<PopularHymnsBean>>?>?
            ) {
                val code = response?.code() ?: 500
                if (code == 200 && response?.isSuccessful == true && !response.body()?.mData.isNullOrEmpty()) {
                    val mData = response.body()?.mData
                    AppDataBase.savePopularHymns(mData, object : com.stpauls.dailyliturgy.others.Callback<Void?>() {
                        override fun onSuccess(t: Void?) {
                            val list = AppDataBase.get().popularHymnDao().allHymns
                            parseListToMap(list)
                        }
                    })

                }
            }
        })
    }

    private fun parseListToMap(mData: MutableList<PopularHymnsBean>?) {

        if (mData.isNullOrEmpty()) {
            return
        }

        val linkedMap = linkedMapOf<String, String>()
        map.clear()
        mData.forEach {
            linkedMap[it.mCategoryName] = it.mCategoryIcon ?: ""
            if (map.containsKey(it.mCategoryName) && !map[it.mCategoryName].isNullOrEmpty()) {
                val list = map[it.mCategoryName]
                list?.add(it)
                map[it.mCategoryName] = list
            } else {
                val list = mutableListOf<PopularHymnsBean>()
                list.add(it)
                map[it.mCategoryName] = list
            }
        }
        val headers = mutableListOf<PrayerCollection.PrayerHeadingBean>()
        linkedMap.forEach {
            headers.add(PrayerCollection.PrayerHeadingBean(it.key, it.value))
        }
        headers.sortWith(compareBy{ it.name })
        setAdapter(headers)
    }

    class MediaActionReceiver() : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {

            val ke = intent?.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
            when (ke?.keyCode ?: -12541) { // if null then using any random value
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                    if (PlayMedia.isPlaying()) {
                        PlayMedia.pause()
                    } else {
                        PlayMedia.play();
                    }
                    NotificationUtils.createNotification(
                        context,
                        PlayMedia.playingSong?.mAudioName,
                        PlayMedia.getMediaSession()
                    )
                };
                KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                    if (context != null) {
                        PlayMedia.playPrev(context)
                    }
                };
                KeyEvent.KEYCODE_MEDIA_NEXT -> {
                    if (context != null) {
                        PlayMedia.playNext(context)
                    }

                };
            }
            MediaButtonReceiver.handleIntent(PlayMedia.getMediaSession(), intent)
            PlayMedia.notifyAdapter()
        }

    }


    override fun setReadingFontSize(size: Float) {

    }

}