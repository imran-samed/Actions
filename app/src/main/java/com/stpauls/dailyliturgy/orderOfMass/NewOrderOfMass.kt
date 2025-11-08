package com.stpauls.dailyliturgy.orderOfMass

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.daycareteacher.others.Toaster
import com.stpauls.dailyliturgy.R
import com.stpauls.dailyliturgy.base.App
import com.stpauls.dailyliturgy.base.BaseActivity
import com.stpauls.dailyliturgy.base.BaseResponse
import com.stpauls.dailyliturgy.base.SharedPref
import com.stpauls.dailyliturgy.commonPrayers.CommonPrayerHeadingAdapter
import com.stpauls.dailyliturgy.commonPrayers.PrayerAdapter
import com.stpauls.dailyliturgy.commonPrayers.PrayerCollection
import com.stpauls.dailyliturgy.commonPrayers.bean.PrayerBean
import com.stpauls.dailyliturgy.localDb.AppDataBase
import com.stpauls.dailyliturgy.retrofit.RetrofitClient
import com.stpauls.dailyliturgy.databinding.ActivityNewOrderOfMassBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NewOrderOfMass : BaseActivity() {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, NewOrderOfMass::class.java))
        }
    }

    private lateinit var binding: ActivityNewOrderOfMassBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityNewOrderOfMassBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        //ivFontToggle?.visibility = View.GONE
        ivGifImage = binding.rlTopView.ivGifImage
        binding.ivHome.setOnClickListener { onBackPressed() }
        val fontToggle = findViewById<ImageView>(R.id.ivToogle)
        fontToggle?.setOnClickListener {
            toggleFont()
        }
        loadGif()
        (binding.rvPrayer.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = true

        getAllPrayer()

        binding.rvPrayer.setHasFixedSize(true);

    }

    private fun getAllPrayer() {
        backgroundThread.execute {
            val items = AppDataBase.get().orderOfMass().allPrayer
            parseListToMap(items)
            if (items.isNullOrEmpty() && !App.get().isConnected()) {
                mainThread.execute {
                    Toaster.shortToast("Please connect to internet...")
                }
                return@execute
            }

            RetrofitClient.getRetrofitApi().newOrderOfMass(SharedPref.getUUid(),/*if (items.isNullOrEmpty()) "0" else "1"*/ "0").enqueue(object :
                Callback<BaseResponse<MutableList<OrderOfMassBean>>?> {
                override fun onFailure(
                    call: Call<BaseResponse<MutableList<OrderOfMassBean>>?>,
                    t: Throwable
                ) {
                }

                override fun onResponse(
                    call: Call<BaseResponse<MutableList<OrderOfMassBean>>?>,
                    response: Response<BaseResponse<MutableList<OrderOfMassBean>>?>
                ) {
                    val code = response.code() ?: 500
                    if (code == 200 && response.isSuccessful && !response.body()?.mData.isNullOrEmpty()) {
                        val newItems = response.body()?.mData
                        AppDataBase.saveOrderOfMass(newItems, object : com.stpauls.dailyliturgy.others.Callback<Void?>() {
                            override fun onSuccess(t: Void?) {
                                val mItems = AppDataBase.get().orderOfMass().allPrayer
                                parseListToMap(mItems)
                            }
                        })

                    }

                }
            })

        }

    }

    private var map = hashMapOf<String, MutableList<PrayerBean>?>()

    private fun parseListToMap(newData: MutableList<OrderOfMassBean>?) {
        if (newData.isNullOrEmpty()) {
            return
        }
        val mData = mutableListOf<PrayerBean>()
        newData.forEach {
            val m = PrayerBean()
            m.mCategoryIcon = it.mCategoryIcon
            m.mCategoryName = it.mCategoryName
            m.mTitle = it.mTitle
            m.mDescription = it.mDescription
            mData.add(m)
        }


        map.clear()
        val linkedMap = linkedMapOf<String, String>()

        mData.forEach {
            linkedMap[it.mCategoryName] = it.mCategoryIcon
            if (map.containsKey(it.mCategoryName) && !map[it.mCategoryName].isNullOrEmpty()) {
                val list = map[it.mCategoryName]
                list?.add(it)
                map[it.mCategoryName] = list
            } else {
                val list = mutableListOf<PrayerBean>()
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

    // prayer header model class
    //data class PrayerHeadingBean(var name: String, var icon: String)

    private var mAdapter: PrayerAdapter? = null
    private fun setAdapter(headers: List<PrayerCollection.PrayerHeadingBean>) {
        val items = ArrayList(map[headers[0].name])
        mainThread.execute {

            (binding.rvPrayerHeading.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            (binding.rvPrayer.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

            mAdapter = PrayerAdapter(this@NewOrderOfMass, items, true)
            binding.rvPrayer?.adapter = mAdapter
            binding.rvPrayerHeading.adapter = CommonPrayerHeadingAdapter(
                headers, object : com.stpauls.dailyliturgy.others.Callback<String>() {
                    override fun onSuccess(title: String) {
                        mAdapter?.notifyDataWithSelectedPosition(map[title])
                    }
                })
        }
    }

    override fun setReadingFontSize(size: Float) {
        mAdapter?.notifyDataSetChanged()
    }
}