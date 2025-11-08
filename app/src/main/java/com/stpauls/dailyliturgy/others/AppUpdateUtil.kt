package com.stpauls.dailyliturgy.others

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.pm.PackageManager

import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AlertDialog
import com.stpauls.dailyliturgy.base.App
import com.stpauls.dailyliturgy.retrofit.RetrofitClient


@SuppressLint("StaticFieldLeak")
object AppUpdateUtil {

    private lateinit var mContext : Context
    private var alertDialog: AlertDialog.Builder?= null

    fun checkUpdate(context: Context) {
        mContext = context
        var versionName = ""
        var versionNum = -1L
        try {
            val pInfo: PackageInfo = App.get().packageManager.getPackageInfo(App.get().packageName, 0)
            versionName = pInfo.versionName ?: ""
            versionNum = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pInfo.longVersionCode
            }else {
                pInfo.versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        RetrofitClient.getRetrofitApi().setting.enqueue(object :
                Callback<JsonObject?> {
                override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                    if (response.isSuccessful && response.code() == 200) {
                        /**
                         * "data": {
                                    "android_app_version": "2",
                                    "android_force_update": false,
                                    "ios_app_version": "3",
                                    "ios_force_update": false
                             }
                         */

                        val JSON = JSONObject(
                            response.body()?.get("data")?.asJsonObject?.toString()
                                ?: JSONObject().toString())
                        if (JSON.optLong("android_app_version", 0L) > versionNum
                            && JSON.optBoolean("android_force_update")){
                            showForceUpdateDialog()
                        }


                    }
                }

                override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                }
            })
    }

    private fun showForceUpdateDialog() {
        /*An updated version with new features and bug fixes is available now.
        Please update the application to continue.
            Thanks,
            Team GW*/
        try {
             alertDialog = AlertDialog.Builder(mContext)
                .setTitle("Update App")
                .setMessage(
                    "An updated version with new features and bug fixes is available now. Please update the application to continue.\n" +
                            "\n\n\nThanks,\nTeam GW"
                )
                .setCancelable(false)
                .setPositiveButton("UPDATE") { dialog, which ->
                    dialog.cancel()
                    val packageName = mContext.packageName
                    try {
                        mContext.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=$packageName")
                            )
                        )
                    } catch (e: ActivityNotFoundException) {
                        mContext.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                            )
                        )
                    }
                }
                alertDialog?.show()
        }catch (e: Exception){}
    }

    fun dismissDialog(){
        try {
            alertDialog?.create()?.dismiss()
        }catch (e:Exception){}
    }
}