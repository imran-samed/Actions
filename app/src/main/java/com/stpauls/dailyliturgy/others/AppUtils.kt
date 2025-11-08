package com.stpauls.dailyliturgy.others

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.Build
import android.text.Spanned
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ShareCompat
import androidx.core.text.HtmlCompat
import com.stpauls.dailyliturgy.Global
import com.stpauls.dailyliturgy.R
import com.stpauls.dailyliturgy.base.AppFontSize
import com.stpauls.dailyliturgy.base.BaseActivity
import com.stpauls.dailyliturgy.base.SharedPref
import com.stpauls.dailyliturgy.threads.DefaultExecutorSupplier
import org.apache.commons.lang3.StringEscapeUtils
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ThreadPoolExecutor


object AppUtils {


    var changeImage : Boolean = true
    private const val TAG = "AppUtils"
    init {
        val compareDate = setDate(2021, Calendar.DECEMBER, 31)
        val onlyDate = getOnlyDate().time
        Log.d(TAG, "DATE CHECK: ${compareDate == onlyDate} ::: ${compareDate.before(onlyDate)}")
        changeImage = (compareDate == onlyDate || compareDate.before(onlyDate))
    }
    fun setFontSize(activity: BaseActivity, tvView: TextView?) {
        tvView?.post {
            when (Global.APP_FONT_SIZE) {
                AppFontSize.MEDIUM -> {
                    set(activity, tvView, R.style.MediumReadingFont)
                }
                AppFontSize.LARGE -> {
                    set(activity, tvView, R.style.LargeReadingFont)
                }
                else -> {
                    set(activity, tvView, R.style.NormalReadingFont)
                }
            }

            tvView.setTextColor(ResourceUtils.getColor(R.color.black))
            //tvView.typeface = tvView.typeface
        }
    }

    fun setDate(year:Int, month: Int, date:Int):Date {
        val c = Calendar.getInstance(TimeZone.getDefault())
        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, month)
        c.set(Calendar.DAY_OF_MONTH, date)
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)
        return c.time
    }

    fun getTimeTake(time: Long) {
        val timeTaken = ((System.currentTimeMillis() - time) / 1000)
        Log.d("TIME TAKEN: ", "getTimeTake:  $timeTaken ")
    }


    fun set(activity: BaseActivity, view: TextView, style: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setTextAppearance(style)
        } else {
            view.setTextAppearance(activity, style)
        }
    }

    fun clearTime(cal: Calendar): String {
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        val date = SimpleDateFormat("dd").format(cal.time)
        val vMonth = SimpleDateFormat("MMM").format(cal.time)
        val year = SimpleDateFormat("yyyy").format(cal.time)

        val month = if (vMonth.length >= 4) vMonth.substring(0, 3) else vMonth
        return "$date $month $year"
    }


   const val HTML_FILTER: Int = HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_DIV

    fun setHTMLString(textView: TextView?, value: String?) {

        if (value == null || TextUtils.isEmpty(value) || textView == null) {
            return
        }

        // extracting HTML string took a-lot of time which causing of UI blocking,
        // so used thread to make process smooth and it really works.
        DefaultExecutorSupplier.get().forLightWeightBackgroundTasks().execute {
            val decodeString = StringEscapeUtils.unescapeHtml4(value)
            Log.d("App Utils: ", "setHTMLString: $decodeString")


            // set text on TextView on UI thread
            textView.post {
                textView.text = HtmlCompat.fromHtml(decodeString, HTML_FILTER)
            }
        }
    }

    fun getHTMLString(content: String?): Spanned? {
        return if (isEmpty(content)) {
            null
        } else {
            HtmlCompat.fromHtml(content!!, HTML_FILTER)
        }
    }

    fun viewGone(view: View?) {
        view?.visibility = View.GONE
    }

    fun viewVisible(view: View?) {
        view?.visibility = View.VISIBLE
    }

    fun getColors(value: List<String>?): String {
        val sb: StringBuilder? = StringBuilder()
        value?.forEach {
            sb?.append(" | ")?.append(getCode(it))
        }
        return sb?.toString()?.substring(3, sb.length) ?: ""
    }

    private fun getCode(value: String?): String {
        return when (value) {
            "4" -> "White"
            "1" -> "Red"
            "2" -> "Green"
            "3" -> "Violet"
            else -> ""
        }
    }

    fun shareText(context: BaseActivity, content: String) {
        ShareCompat.IntentBuilder.from(context)
            .setType("text/plain")
            .setChooserTitle("Share...")
            .setText(content)
            .startChooser()
    }

    fun showDialog(context: BaseActivity, msg: String): AlertDialog? {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setCancelable(false) // if you want user to wait for some process to finish,

        builder.setView(R.layout.layout_loading_dialog)
        builder.setMessage(msg)
        return builder.create()
    }

    fun dismiss(dialog: AlertDialog?) {
        dialog?.dismiss()
        dialog?.cancel()
    }

    fun isEmpty(content: String?): Boolean {
        return TextUtils.isEmpty(content?.trim() ?: "") || TextUtils.equals("null", content)
    }



    fun setPsalterWeek(tvPsalter: TextView?, plaster: String?) {
        if (tvPsalter == null || TextUtils.isEmpty(plaster?.trim())) {
            return
        }
        val value: String = when (plaster) {
            "1" -> "Psalter Week I"
            "2" -> "Psalter Week II"
            "3" -> "Psalter Week III"
            "4" -> "Psalter Week IV"
            "5" -> "Proper"
            else -> plaster ?: ""
        }
        tvPsalter.text = value
    }





    fun BitMapToString(bitmap: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b: ByteArray = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    fun StringToBitMap(encodedString: String?): Bitmap? {
        return try {
            val encodeByte = Base64.decode(encodedString, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
        } catch (e: Exception) {
            e.message
            null
        }
    }

    fun StringToBitMap(encodedString: String?, thread: ThreadPoolExecutor, callback: Callback<Bitmap?>) {
        thread.execute {
            try {
                val encodeByte = Base64.decode(encodedString, Base64.DEFAULT)
                callback.onSuccess(BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size))
            } catch (e: Exception) {
                e.message
                callback.onSuccess(null)
            }
        }
    }


    fun isTodayUpdated(key:String): Boolean {
        val lastUpdateDate = SharedPref.get().get(key, 0L)
        // currentDate > lastUpdateDate
        if (getOnlyDate().time.time > lastUpdateDate){
            return  false
        }
        return true
    }

    fun getOnlyDate(): Calendar {
        val c = Calendar.getInstance()
        c.time = Date()
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c
    }

    fun saveUpdatedDate(key:String) {
        SharedPref.get().save(key, getOnlyDate().time.time)
    }


    const val AUTH_KEY =
        "Authorization:Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiNGVlMjY1YWQ0OGI3NTQyMjFhZWM4NzE0Y2Y5ZDc2ZDUzZjAyNjY0OTNhZDI0M2U3YzI4NzgyNmMyZGQwMTljMzhiNWM3ZWQ0ZDkwZjZjNjIiLCJpYXQiOjE2MDA3NzE3NTAsIm5iZiI6MTYwMDc3MTc1MCwiZXhwIjoxNjMyMzA3NzUwLCJzdWIiOiIxIiwic2NvcGVzIjpbXX0.m4seNl7IOJEpiqOeNSmWRGNsbG5N7zEkx6AlE-VeY6oEEu1HbLlaMTW-3SVUaK-SWDucer60jlLjHsUDzMjFJ6cV2dj-YD0EzbgQyNmeZQvP-TLEfaYBLFFPUoDcunDh-Q3yCH9osAnPLJE6laMheE7S_URd4T8wMqqt1iKOd4fcsXfhVN307u2spUcFcLbeOrJ-dZ6RszWSfc2HoKg01wskKAeIrVnld77LoJlTiO9SnOhN-ShucdeReQQ2wDT3d7nzp_K4P62PmMWdb7rxmvlWRKsXHrxJNFtQBtKeXXbPaP4pnu3A6GUeXgJCuvdOkAkZBfEBetvPfO8JzrWiSpuNo3LNrUZwUQ2mHy5PdCZfXhQOf_U7eIVpFoABg-ME_G64ZgVw4CdIZjvLY8jHsUrnkIAZk6-OfSG3JhyRpw_GWLMCte8cX07pXEiAzCv-oRF3WtvuxQaUqWbW4926THG8xgJZ7DlJYtuvxmyCWPSpvvaPaMHud-rtIiOaTz9tMCNNC89Ee9iHI5Mk9gSpd_zw1oxEoHYZg-CNoX3UZfZVmibJrtTfJxp0Bj4jJb4tZLTGXE6KINiD102u3Eu4wexcfRJIXtPQZ5vNA1ZCpxaw2l_eENzUPJ6n_hbw4ZzRrRusUQCCa1jQFfCmNv2J7YHVchEdpG0JsURDOIIU-dk"
}

fun View?.setVisible(visible:Boolean){
    this?.visibility = if (visible) View.VISIBLE else View.GONE
}