package com.stpauls.dailyliturgy.aboutUs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.JsonObject
import com.stpauls.dailyliturgy.Global
import com.stpauls.dailyliturgy.R
import com.stpauls.dailyliturgy.base.BaseActivity
import com.stpauls.dailyliturgy.base.SharedPref
import com.stpauls.dailyliturgy.others.AppUtils
import com.stpauls.dailyliturgy.retrofit.RetrofitClient
import com.stpauls.dailyliturgy.databinding.ActivityOrderOfMassBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AboutUs : BaseActivity() {
    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, AboutUs::class.java))
        }
    }
    override fun setReadingFontSize(size: Float) {
        AppUtils.setFontSize(this@AboutUs, binding.tvReadableText)
    }

    private lateinit var binding: ActivityOrderOfMassBinding
    private var tvReadingText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderOfMassBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ivGifImage = binding.rlTopView.ivGifImage
        binding.ivHome.setOnClickListener { onBackPressed() }
        loadGif()
        binding.tvTitle.text = "About Us"
        tvReadingText = binding.tvReadableText
        setAboutUs(SharedPref.get().get<String>(Global.ABOUT_US) ?: "--")
        val fontToggle = binding.ivToogle
        fontToggle?.setOnClickListener {
            toggleFont()
        }
        /*tvShare?.setOnClickListener {
            val sharingText = "${Global.APP_NAME_FOR_SHARING}\n" +
                    "About Us\n\n" +
                    "${tvReadingText?.text}"
            AppUtils.shareText(this@AboutUs, sharingText)
        }*/
        loadGif()
        backgroundThread.execute {
            if (AppUtils.isTodayUpdated("about_us")){
                return@execute
            }
            RetrofitClient.getRetrofitApi().aboutUs.enqueue(object : Callback<JsonObject?> {
                override fun onFailure(call: Call<JsonObject?>, t: Throwable) {}
                override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>?) {
                    val code = response?.code() ?: 500
                    if (code == 200 && response?.isSuccessful == true) {
                        AppUtils.saveUpdatedDate("about_us")
                        val value = response.body()?.get("about_us")?.asString ?: "--"
                        mainThread.execute {
                            setAboutUs(value)
                            SharedPref.get().save(Global.ABOUT_US, value)
                        }
                    }
                }
            })
        }
    }


    private fun setAboutUs(orderOfMass: String?) {
        if (!TextUtils.isEmpty(orderOfMass) || !TextUtils.equals("--", orderOfMass)) {
            AppUtils.setHTMLString(tvReadingText, orderOfMass)
        }
    }
}