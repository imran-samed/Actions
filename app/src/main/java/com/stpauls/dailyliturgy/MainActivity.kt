package com.stpauls.dailyliturgy

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowInsets
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.daycareteacher.others.Toaster
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.stpauls.dailyliturgy.aboutUs.AboutUs
import com.stpauls.dailyliturgy.base.App.Companion.get
import com.stpauls.dailyliturgy.base.BaseActivity
import com.stpauls.dailyliturgy.base.BaseResponse
import com.stpauls.dailyliturgy.base.MyProgressDialog
import com.stpauls.dailyliturgy.base.SharedPref
import com.stpauls.dailyliturgy.commonPrayers.PrayerCollection
import com.stpauls.dailyliturgy.godsWord.BibleActivity
import com.stpauls.dailyliturgy.homelyTips.HomelyTips
import com.stpauls.dailyliturgy.localDb.AppDataBase
import com.stpauls.dailyliturgy.localDb.tables.GodsWordBean
import com.stpauls.dailyliturgy.moreApps.MoreApps
import com.stpauls.dailyliturgy.orderOfMass.NewOrderOfMass
import com.stpauls.dailyliturgy.others.ArcDrawable
import com.stpauls.dailyliturgy.others.DrawingImageView
import com.stpauls.dailyliturgy.others.DrawingImageView.ImageListener
import com.stpauls.dailyliturgy.popularHymns.PopularHymnsActivity
import com.stpauls.dailyliturgy.popularHymns.dismissDialog
import com.stpauls.dailyliturgy.popularHymns.showDialog
import com.stpauls.dailyliturgy.retrofit.RetrofitClient.getRetrofitApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import com.stpauls.dailyliturgy.databinding.ActivityCircle2Binding


class MainActivity : BaseActivity(), ImageListener {

    private val savedYear = SharedPref.get().get<String>(Global.YEAR)
    private val currentYear = Global.ONLY_YEAR.format(Date())
    private lateinit var binding: ActivityCircle2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCircle2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        val image1 = binding.image1
        val image = binding.image
        val iv5 = binding.iv5

        image.setImageDrawable(ArcDrawable())
        image.setImageSectorListener(this)

        image1.setImageResource(R.drawable.home_screen_new)
        iv5.setOnClickListener { onSectorClick(9.0) }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                if (shouldRequestNotificationPermission()) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
                    markNotificationPermissionRequested()
                }
            }
        }

        checkIfDataExist()
        initFirebase()
    }

    private fun initFirebase() {
        // android_liturgy, ios_liturgy, all_liturgy
        FirebaseApp.initializeApp(this@MainActivity)
        FirebaseMessaging.getInstance().subscribeToTopic("android_liturgy")
        FirebaseMessaging.getInstance().subscribeToTopic("all_liturgy")
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
        })
    }

    private fun checkIfDataExist() {
        val dialog = MyProgressDialog(this@MainActivity, "Loading data for current year...")

        backgroundThread.execute {
            val count = AppDataBase.get().godsWordDao().getCount(currentYear)
            if (count <= 0 || (TextUtils.isEmpty(savedYear) || !TextUtils.equals(
                    savedYear,
                    currentYear
                ))
            ) {

                if (!get().isConnected()) {
                    mainThread.execute {
                        Toaster.shortToast("1 Please connect to internet..")
                    }
                    return@execute
                }


                showDialog(dialog, true)
                getRetrofitApi().getGodsWord(currentYear).enqueue(object :
                    Callback<BaseResponse<List<GodsWordBean?>?>?> {
                    override fun onResponse(
                        call: Call<BaseResponse<List<GodsWordBean?>?>?>,
                        response: Response<BaseResponse<List<GodsWordBean?>?>?>?
                    ) {
                        showDialog(dialog, false)
                        val code = response?.code() ?: 500
                        if (code == 200 && response?.isSuccessful == true) {
                            //AppDataBase.saveData(response.body()?.mData)
                            SharedPref.get().save(Global.YEAR, currentYear)
                            loadJson(
                                response.body()?.mData as MutableList<GodsWordBean?>?,
                                true,
                                currentYear
                            )
                        } else {
                            Toaster.shortToast("2 No data found...\nPlease try again")
                        }
                    }

                    override fun onFailure(
                        call: Call<BaseResponse<List<GodsWordBean?>?>?>,
                        t: Throwable
                    ) {
                        showDialog(dialog, false)
                    }
                })
            } else if (count > 0) {
                getUpdatedData();
            }
        }
    }

    private fun getUpdatedData() {
        getRetrofitApi().getUpdateData(currentYear, SharedPref.getUUid(), "1").enqueue(object :
            Callback<BaseResponse<List<GodsWordBean?>?>?> {
            override fun onResponse(
                call: Call<BaseResponse<List<GodsWordBean?>?>?>,
                response: Response<BaseResponse<List<GodsWordBean?>?>?>?
            ) {

                val code = response?.code() ?: 500
                if (code == 200 && response?.isSuccessful == true && response.body()?.mData?.isNullOrEmpty() == false) {
                    //AppDataBase.saveData(response.body()?.mData)
                    loadJson(
                        response.body()?.mData as MutableList<GodsWordBean?>?,
                        false,
                        currentYear
                    )
                }
            }

            override fun onFailure(
                call: Call<BaseResponse<List<GodsWordBean?>?>?>, t: Throwable
            ) {

            }
        })
    }

    private fun showDialog(dialog: MyProgressDialog?, show: Boolean) {
        mainThread.execute {
            if (show) {
                dialog?.showDialog()
            } else {
                dialog?.dismissDialog()
            }
        }
    }

    private fun loadJson(
        mList: MutableList<GodsWordBean?>?,
        showToast: Boolean,
        currentYear: String
    ) {
        try {
            if (mList.isNullOrEmpty()) {
                if (showToast) {
                    Toaster.shortToast("3 No data found for this year...")
                }
                return
            }
            val beans: MutableList<GodsWordBean> = ArrayList()
            for (item in mList) {
                item?.readingsString = item?.getReadingsString()
                item?.colorCode = TextUtils.join(",", item?.color ?: listOf("0"))
                item?.year = currentYear
                beans.add(item!!)
            }
            AppDataBase.saveData(beans)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onSectorClick(pos: Double) {
        Log.d(TAG, "onSectorClick: $pos")
        fun checkRange(a: Double, b: Double, c: Double): Boolean {
            return b in a..c
        }

        if (checkRange(4.5, pos, 5.6)) {
            MoreApps.start(this@MainActivity)
        } else if (checkRange(4.01, pos, 4.51) || checkRange(0.0, pos, 0.7)) {
            PopularHymnsActivity.start(this@MainActivity)
        } else if (checkRange(1.52, pos, 2.47)) {
            NewOrderOfMass.start(this@MainActivity)
        } else if (checkRange(3.2, pos, 4.0) || checkRange(7.53, pos, 8.0)) {
            PrayerCollection.start(this@MainActivity)
        } else if (checkRange(6.4, pos, 7.5)) {
            AboutUs.start(this@MainActivity)
        } else if (checkRange(5.7, pos, 6.25)) {
            HomelyTips.start(this@MainActivity)
        } else if (pos == 9.0) {
            BibleActivity.start(this@MainActivity)
        }
    }

    override fun setReadingFontSize(size: Float) {}

    private fun shouldRequestNotificationPermission(): Boolean {
        val lastRequestDate = SharedPref.get().get<String>("last_request_date")
        val today = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
        return lastRequestDate != today
    }

    private fun markNotificationPermissionRequested() {
        val today = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
        SharedPref.get().save("last_request_date", today)
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }

        private const val TAG = "MainActivity"
    }
}