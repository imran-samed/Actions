package com.stpauls.dailyliturgy.orderOfMass

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

class OrderOfMass : BaseActivity() {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, OrderOfMass::class.java))
        }
    }

    override fun setReadingFontSize(size: Float) {
        AppUtils.setFontSize(this@OrderOfMass, binding.tvReadableText)
    }

    private lateinit var binding: ActivityOrderOfMassBinding
    private var tvReadingText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityOrderOfMassBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        ivGifImage = binding.rlTopView.ivGifImage
        binding.ivHome.setOnClickListener { onBackPressed() }
        tvReadingText = binding.tvReadableText
        setOrderOfMass(SharedPref.get().get<String>(Global.ORDER_OF_MASS_DATA) ?: "--")
        val fontToggle = binding.ivToogle
        fontToggle?.setOnClickListener {
            toggleFont()
        }
       /* tvShare?.setOnClickListener {
            val sharingText = "${Global.APP_NAME_FOR_SHARING}\n" +
                    "Order of Mass\n\n" +
                    "${tvReadingText?.text}"
            AppUtils.shareText(this@OrderOfMass, sharingText)
        }*/
        loadGif()
        backgroundThread.execute {
            if (AppUtils.isTodayUpdated("order_of_mass")){
                return@execute
            }
            RetrofitClient.getRetrofitApi().orderOfMass.enqueue(object : Callback<JsonObject?> {
                override fun onFailure(call: Call<JsonObject?>, t: Throwable) {}
                override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>?) {
                    val code = response?.code() ?: 500
                    if (code == 200 && response?.isSuccessful == true) {
                        val value = response.body()?.get("order_of_mass")?.asString ?: "--"
                        AppUtils.saveUpdatedDate("order_of_mass")
                        mainThread.execute {
                            setOrderOfMass(value)
                            SharedPref.get().save(Global.ORDER_OF_MASS_DATA, value)
                        }
                    }
                }
            })
        }

    }

    private fun setOrderOfMass(orderOfMass: String?) {
        if (!TextUtils.isEmpty(orderOfMass) || !TextUtils.equals("--", orderOfMass)) {
            AppUtils.setHTMLString(tvReadingText, orderOfMass)
        }
    }
}