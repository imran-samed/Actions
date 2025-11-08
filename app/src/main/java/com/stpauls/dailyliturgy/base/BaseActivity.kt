package com.stpauls.dailyliturgy.base

import android.os.Bundle
import android.view.Window
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.ViewCompat
import com.stpauls.dailyliturgy.Global
import com.stpauls.dailyliturgy.R
import com.stpauls.dailyliturgy.others.AppUpdateUtil
import com.stpauls.dailyliturgy.threads.DefaultExecutorSupplier
import java.util.concurrent.Executor
import java.util.concurrent.ThreadPoolExecutor

abstract class BaseActivity : AppCompatActivity() {

    protected open var ivGifImage: ImageView? = null
    protected var fontToggle: ImageView? = null;
    protected val backgroundThread: ThreadPoolExecutor = DefaultExecutorSupplier.get().forBackgroundTasks()
    protected val mainThread: Executor = DefaultExecutorSupplier.get().forMainThreadTasks()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ivGifImage = findViewById(R.id.ivGifImage)
        ivGifImage?.setOnClickListener { onBackPressed() }
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        applyEdgeToEdgeInsets()
    }

    override fun setContentView(view: android.view.View?) {
        super.setContentView(view)
        applyEdgeToEdgeInsets()
        hideToolbar();
    }

    private fun hideToolbar() {

    }

    override fun setContentView(view: android.view.View?, params: android.view.ViewGroup.LayoutParams?) {
        super.setContentView(view, params)
        applyEdgeToEdgeInsets()
    }

    private fun applyEdgeToEdgeInsets() {
        val rootView = findViewById<android.view.View>(android.R.id.content)?.let {
            if (it is android.view.ViewGroup && it.childCount > 0) it.getChildAt(0) else it
        } ?: return
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun toggleFont() {
        when (Global.APP_FONT_SIZE) {
            AppFontSize.NORMAL -> {
                Global.APP_FONT_SIZE = AppFontSize.MEDIUM
            }
            AppFontSize.MEDIUM -> {
                Global.APP_FONT_SIZE = AppFontSize.LARGE
            }
            else -> {
                Global.APP_FONT_SIZE = AppFontSize.NORMAL;
            }
        }
        Global.isLarge = !Global.isLarge
        // fun param have no use, we just need to call that method
        setReadingFontSize(Global.appReadingTextSize.toFloat())

    }

    protected abstract fun setReadingFontSize(size: Float);

    protected fun loadGif() {
        /*Glide.with(this@BaseActivity)
            .load(Uri.parse("file:///android_asset/logo_home.png"))
            .into(ivGifImage);*/
    }

    override fun onStart() {
        super.onStart()
        AppUpdateUtil.checkUpdate(this@BaseActivity)
    }

    override fun onStop() {
        super.onStop()
        AppUpdateUtil.dismissDialog()
    }

}