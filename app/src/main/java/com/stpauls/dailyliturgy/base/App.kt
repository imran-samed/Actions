package com.stpauls.dailyliturgy.base

import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.stpauls.dailyliturgy.others.NotificationUtils
import java.io.IOException

class App : Application() {

    companion object {
        private lateinit var instance: App
        @JvmStatic
        fun get(): App {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        registerActivityLifecycleCallbacks(object : MyActivityLifecycleCallbacks() {
            override fun onActivityDestroyed(activity: Activity) {
                NotificationUtils.clearNotification(activity)
            }
        })
    }

    @Throws(IOException::class)
    fun Context.readJsonAsset(fileName: String): String {
        val inputStream = assets.open(fileName)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        return String(buffer, Charsets.UTF_8)
    }

    fun isConnected(): Boolean {
        val cm = instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (cm != null) {
            if (Build.VERSION.SDK_INT < 23) {
                val ni = cm.activeNetworkInfo
                if (ni != null) {
                    return (ni.isConnected && (ni.type == ConnectivityManager.TYPE_WIFI
                            || ni.type == ConnectivityManager.TYPE_MOBILE
                            || ni.type == ConnectivityManager.TYPE_BLUETOOTH))
                }
            } else {
                val n = cm.activeNetwork
                if (n != null) {
                    val nc = cm.getNetworkCapabilities(n)
                    if (nc != null) {
                        return (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                                || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                                || nc.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH))
                    }
                }
            }
        }

        return false
    }
}