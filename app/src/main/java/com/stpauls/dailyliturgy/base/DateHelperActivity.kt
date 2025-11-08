package com.stpauls.dailyliturgy.base

import android.app.DatePickerDialog
import android.widget.TextView
import com.stpauls.dailyliturgy.Global
import com.stpauls.dailyliturgy.R
import com.stpauls.dailyliturgy.localDb.AppDataBase
import com.stpauls.dailyliturgy.localDb.tables.GodsWordBean
import com.stpauls.dailyliturgy.others.Callback
import com.stpauls.dailyliturgy.threads.DefaultExecutorSupplier
import java.util.*

abstract class DateHelperActivity : BaseActivity() {


    protected var  cal = Global.CALENDER
    protected var tvTitleDate: TextView?= null
    protected var tvReadingText: TextView?= null


    protected fun showDatePicker(callback: Callback<Calendar?>) {
        cal = Global.CALENDER
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val month = cal.get(Calendar.MONTH)
        val year = cal.get(Calendar.YEAR)

        val yearCal = Calendar.getInstance();
        yearCal.time = Date()

        val minDate = Calendar.getInstance();
        minDate.set(yearCal.get(Calendar.YEAR), Calendar.JANUARY, 1)

        val maxDate = Calendar.getInstance()
        maxDate.set(yearCal.get(Calendar.YEAR), Calendar.DECEMBER, 31)

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, y, m, d ->
            cal.set(Calendar.YEAR, y)
            cal.set(Calendar.MONTH, m)
            cal.set(Calendar.DAY_OF_MONTH, d)
            callback.onSuccess(cal)

        }
        val datePicker =
            DatePickerDialog(
                this,
                R.style.datepicker, dateSetListener, year, month, day
            )
        datePicker.datePicker.minDate = minDate.timeInMillis
        datePicker.datePicker.maxDate = maxDate.timeInMillis
        datePicker.show()

    }

    protected fun setDate(cal: Calendar) {
        tvTitleDate?.text = Global.DATE_FORMAT.format(cal.time)
    }



    private fun getDataByDate(date: String, callback: Callback<GodsWordBean?>) {
        DefaultExecutorSupplier.get().forLightWeightBackgroundTasks().execute {
            callback.onSuccess(AppDataBase.get().godsWordDao().getAdditionHomelyTips(date))
        }
    }
}