package com.stpauls.dailyliturgy.moreApps

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.stpauls.dailyliturgy.R
import com.stpauls.dailyliturgy.base.BaseActivity
import com.stpauls.dailyliturgy.databinding.ActivityAboutUsBinding

class MoreApps : BaseActivity() {

    companion object{
        fun start(context: Context){
            context.startActivity(Intent(context, MoreApps::class.java))
        }
    }
    private lateinit var binding: ActivityAboutUsBinding
    override fun setReadingFontSize(size: Float) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.ivHome.setOnClickListener { onBackPressed() }
        loadGif()

        // APP URLs
        //https://play.google.com/store/apps/details?id=in.wi.ncb
        //https://play.google.com/store/apps/details?id=com.ninestars.stpauls

        binding.tvApp1.setOnClickListener { launchUrl("https://www.stpauls.ie/", true) }
        binding.tvApp2.setOnClickListener { launchUrl("in.wi.ncb", false) }
    }

    private fun launchUrl(appPackageName: String,isWebUrl: Boolean) {
        if (!isWebUrl) {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$appPackageName")
                    )
                )
            } catch (anfe: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                    )
                )
            }
        }else{
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("$appPackageName")
                )
            )
        }
    }
}