package com.stpauls.dailyliturgy.splash

import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import com.stpauls.dailyliturgy.MainActivity
import com.stpauls.dailyliturgy.R
import com.stpauls.dailyliturgy.base.BaseActivity
import com.stpauls.dailyliturgy.databinding.ActivityCircle2Binding

class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivityCircle2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCircle2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        val imageView = ImageView(this@SplashActivity)
        imageView.setImageResource(R.drawable.splash_img)
        Handler().postDelayed({
            MainActivity.start(this@SplashActivity)
            finishAffinity()
        }, 1000);
    }

    override fun setReadingFontSize(size: Float) {
        // nothing to do here
    }
}