package com.stpauls.dailyliturgy

import android.graphics.Paint
import android.graphics.RectF
import com.stpauls.dailyliturgy.base.App
import com.stpauls.dailyliturgy.base.AppFontSize
import com.stpauls.dailyliturgy.others.ResourceUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object Global {
    val CALENDER: Calendar;
    var screenWidth: Int? = null
    var screenHeight: Int? = null
    val titles = mutableListOf<String>()
    val colorTitles = mutableListOf<Int>()
    val DATE_FORMAT = SimpleDateFormat("MMM dd, EEE", Locale.getDefault())
    val ONLY_YEAR = SimpleDateFormat("yyyy", Locale.getDefault())
    val DB_DATE_FORMAT = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val DATE_FORMAT_FILE = SimpleDateFormat("hh:mm:ss", Locale.getDefault())

    // keys
    const val ORDER_OF_MASS_DATA = "order.of.mass.data"
    const val ABOUT_US = "about.us"
    const val YEAR = "saved.year.data"
    const val LANDING_IMAGE = "LANDING_IMAGE"

    // static String values
    val APP_NAME_FOR_SHARING = ResourceUtils.getString(R.string.app_name)

    // screens
    const val HYMENS = 1
    const val ORDER_OF_MASS = 2
    const val ABOUT = 3
    const val SUPPORT = 4
    const val PRAYER_COLLECTION = 5
    const val ADDITIONAL_TIPS = 6
    const val GOD_WORD = 7;



    const val NO_POSITION = -2L
    var PLAYING_POSITION = NO_POSITION

    //MEDIA CURRENT PLAYING POSITION OF LIST
    var PLAYING_POSITION_OF_LIST_ITEM = -2

    val POPULAR_HYMNS_FOLDER_PATH: File = File("${App.get().filesDir}/${ResourceUtils.getString(R.string.app_name)}/");


    val bgs = intArrayOf(
        R.drawable.bg_common_prayer_1,
        R.drawable.bg_common_prayer_2,
        R.drawable.bg_common_prayer_3,
        R.drawable.bg_common_prayer_4,
        R.drawable.bg_common_prayer_5,
        R.drawable.bg_common_prayer_6,
        R.drawable.bg_common_prayer_7
    )

    init {
        /*titles.add("First Reading")
        titles.add("Responsorial Psalm")
        titles.add("Second Reading")
        titles.add("Gospel")
        titles.add("Reflection")
        titles.add("Saint of the Day")
        titles.add("Prayer of the Faithful")
        titles.add("Prayer of the Faithful")*/

        titles.add("1st Reading")
        titles.add("Psalm")
        titles.add("2nd Reading")
        titles.add("Acclamation")
        titles.add("Gospel")
        titles.add("Reflection")
        titles.add("Intercessions")
        titles.add("Saint of the Day")

        colorTitles.add(ResourceUtils.getColor(R.color.colorAccent))
        colorTitles.add(ResourceUtils.getColor(R.color.pink))
        colorTitles.add(ResourceUtils.getColor(R.color.magenta))
        colorTitles.add(ResourceUtils.getColor(R.color.dark_blue))
        colorTitles.add(ResourceUtils.getColor(R.color.blue_green))
        colorTitles.add(ResourceUtils.getColor(R.color.green))
        colorTitles.add(ResourceUtils.getColor(R.color.yellow))
        colorTitles.add(ResourceUtils.getColor(R.color.orange))

        CALENDER = Calendar.getInstance();
        CALENDER.time = Date()
    }

    var rectF: RectF? = null
    var paint: Paint? = null

    var appReadingTextSize: Int = 0;
    var isLarge: Boolean = false;

    var APP_FONT_SIZE = AppFontSize.NORMAL

    // Media notification ACTION
    const val MEDIA_BROADCAST_RECEIVER = "com.stpauls.godsword.others.play.song"
    const val KEY_PLAY_PAUSE= "key.play.pause"
    const val KEY_PLAY_PREV= "key.play.prev"
    const val KEY_PLAY_NEXT= "key.play.next"
}