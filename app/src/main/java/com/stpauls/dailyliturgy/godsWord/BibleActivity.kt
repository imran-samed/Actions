package com.stpauls.dailyliturgy.godsWord

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.daycareteacher.others.Toaster
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.stpauls.dailyliturgy.Global
import com.stpauls.dailyliturgy.R
import com.stpauls.dailyliturgy.base.App
import com.stpauls.dailyliturgy.base.DateHelperActivity
import com.stpauls.dailyliturgy.base.MyProgressDialog
import com.stpauls.dailyliturgy.localDb.AppDataBase
import com.stpauls.dailyliturgy.localDb.tables.GodsWordBean
import com.stpauls.dailyliturgy.localDb.tables.ReadingBean
import com.stpauls.dailyliturgy.others.AppUtils
import com.stpauls.dailyliturgy.others.Callback
import com.stpauls.dailyliturgy.others.ResourceUtils
import com.stpauls.dailyliturgy.databinding.ActivityDiaryBinding
import com.stpauls.dailyliturgy.databinding.CustomTabBinding
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*
import kotlin.math.min


class BibleActivity : DateHelperActivity(), ViewPager.OnPageChangeListener {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, BibleActivity::class.java))
        }
    }

    private val DATE_FORMAT = Global.DATE_FORMAT

    private var viewPagerAdapter: MyPagerAdapter? = null
    private var textToSpeech: TextToSpeech? = null

    private val TAG = "BibleActivity"
    private lateinit var binding: ActivityDiaryBinding

    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout : TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        cal.time = Date()
        ivGifImage = binding.rlTopView.ivGifImage
        binding.ivHome.setOnClickListener { onBackPressed() }
        loadGif()

        tvTitleDate = binding.tvTitleDate
        // set current date to CALENDAR
        setDate(cal)
        viewPager = binding.viewPager
        tabLayout = binding.tabLayout
        viewPager.setOnPageChangeListener(this@BibleActivity)

        initClickEvents()

        // to scroll text horizontally
        binding.tvHeaderOfDay2.isSelected = true
        binding.tvHeaderOfDay.isSelected = true

        //Init text to speech
        initTextToSpeech()

        // get current date DATA
        getDataByDate(cal)

    }

    fun initClickEvents() {
        // share app link...
        /*tvShare.setOnClickListener {
            //AppUtils.shareText(this@BibleActivity, "https://godsword.page.link/naxz")
            AppUtils.shareText(this@BibleActivity, "https://godsword.page.link/dailyliturgy")
        }*/


        // Get Previous Data
        binding.tvPreviousDate.setOnClickListener {
            // remove one day from current day
            cal.add(Calendar.DAY_OF_YEAR, -1)
            setDate(cal)
            getDataByDate(cal)
        }

        // Get Next date data
        binding.tvNextDate.setOnClickListener {
            //one day to current date
            cal.add(Calendar.DAY_OF_YEAR, 1)
            setDate(cal)
            getDataByDate(cal)
        }

        // Open DatePickerDialog to select date
        binding.tvTitleDate?.setOnClickListener {
            showDatePicker(object : Callback<Calendar?>() {
                override fun onSuccess(selectedDate: Calendar?) {
                    if (selectedDate == null) {
                        return
                    }
                    setDate(selectedDate)
                    getDataByDate(selectedDate)
                }
            })
        }

        // play audio or Text To Speech
        binding.tvMusic.setOnClickListener {

            // if textToSpeech is playing
            if (textToSpeech?.isSpeaking == true) {
                //stop TextToSpeech
                textToSpeech?.stop()
                return@setOnClickListener
            }
            if (PlayAudioManager.isPlaying()) {
                // stop music player if playing
                PlayAudioManager.killMediaPlayer()
                return@setOnClickListener
            }

            val currentItem = viewPager.currentItem
            // if music file is available of @ViewPager.currentItem and device is connected to internet
            if (isMusicAvailable(currentItem) && App.get().isConnected()) {
                // Play audio file from internet  without downloading
                PlayAudioManager.playAudio(this@BibleActivity, readingBean[currentItem].audioFile)
                return@setOnClickListener
            }

            // if music file is not available then read(TEXT TO SPEECH) all available text of description
            if (!readingBean.isNullOrEmpty() && currentItem in 0..readingBean.indices.last) {
                val rawText = readingBean[currentItem].description
                if (AppUtils.isEmpty(rawText)) {
                    Toaster.shortToast("No text available to speech...")
                    return@setOnClickListener
                }
                val text = AppUtils.getHTMLString(rawText)?.toString()
                val maxLength = min(text?.length ?: 0, TextToSpeech.getMaxSpeechInputLength())
                val mText = text?.subSequence(0, maxLength)
                textToSpeech?.speak(mText?.toString(), TextToSpeech.QUEUE_FLUSH, null)
            }

        }
    }

    private fun initTextToSpeech() {
        textToSpeech = TextToSpeech(this@BibleActivity) {
            if (it != TextToSpeech.ERROR) {
                textToSpeech?.language = Locale.US
            }
        }
    }

    private val readingBean = mutableListOf<ReadingBean>()

    private fun getDataByDate(calendar: Calendar) {
        val time = System.currentTimeMillis()
        getDataByDate(AppUtils.clearTime(calendar), object : Callback<GodsWordBean?>() {
            override fun onSuccess(data: GodsWordBean?) {
                mainThread.execute {
                    if (data == null) {
                        AppUtils.viewVisible(binding.tvNoItem)
                        AppUtils.viewGone(binding.rlMainContent)
                        return@execute
                    }
                    AppUtils.viewGone(binding.tvNoItem)
                    AppUtils.viewVisible(binding.rlMainContent)

                    val bean = Gson().fromJson<List<ReadingBean>>(
                        data.readingsString,
                        object : TypeToken<List<ReadingBean>>(){}.type
                    ).toMutableList()

                    binding.tvHeaderOfDay?.text = data.today_word
                    AppUtils.setHTMLString(binding.tvHeaderOfDay2, data.today_description)

                    AppUtils.setPsalterWeek(binding.tvPsalter, data.plaster)
                    binding.llColors?.text = AppUtils.getColors(data.colorCode?.split(",") ?: listOf("4"))

                    val sortBean = mutableListOf<ReadingBean>()
                    for (item in bean) {
                        if (!AppUtils.isEmpty(item.description)) {
                            sortBean.add(item)
                        }
                    }

                    readingBean.clear()
                    readingBean.addAll(sortBean.sortedBy { it.readingOrderId })

                    if (viewPager.adapter != null) {
                        viewPagerAdapter?.notifyDataSetChanged()
                    } else {
                        viewPagerAdapter = MyPagerAdapter(supportFragmentManager, readingBean)
                        viewPager.adapter = viewPagerAdapter
                        viewPager.offscreenPageLimit = 7
                        tabLayout.setupWithViewPager(viewPager)
                    }
                    //createTabIcons()
                }
            }
        })
    }


    override fun setReadingFontSize(size: Float) {
        viewPagerAdapter?.notifyDataSetChanged()
        //createTabIcons()
    }

    override fun onPause() {
        super.onPause()
        Global.isLarge = false
    }

    private fun createTabIcons() {
        /*createTab(0, "1st Reading", R.drawable.ic_read_11)
          createTab(1, "Psalms", R.drawable.ic_read_22)
          createTab(2, "2nd Reading", R.drawable.ic_read_33)
          createTab(3, "Acclamation", R.drawable.ic_read_44)
          createTab(4, "Gospel", R.drawable.ic_read_55)
          createTab(5, "Reflection", R.drawable.ic_read_66)
          createTab(6, "Intercessions", R.drawable.ic_read_77)
          createTab(7, "Saint of\nthe Day", R.drawable.ic_read_88)*/

        /*val icons = listOf<Int>(
            R.drawable.ic_read_11,
            R.drawable.ic_read_22,
            R.drawable.ic_read_33,
            R.drawable.ic_read_44,
            R.drawable.ic_read_55,
            R.drawable.ic_read_66,
            R.drawable.ic_read_77,
            R.drawable.ic_read_88
        )*/
        //tabLayout.removeAllTabs()
        val titles = listOf<String>(
            "First\nReading",
            "Psalms",
            "Second\nReading",
            "Acclamation",
            "Gospel",
            "Reflection",
            "Intercessions",
            "Saint of\nthe Day"
        )

        val icons = listOf<Int>(
            R.drawable.ic_0,
            R.drawable.ic_1,
            R.drawable.ic_2,
            R.drawable.ic_3,
            R.drawable.ic_4,
            R.drawable.ic_5,
            R.drawable.ic_6,
            R.drawable.ic_7
        )

        var pos = 0
        readingBean.forEach {
            if (!AppUtils.isEmpty(it.description)) {
                createTab(
                    pos++,
                    titles[it.readingOrderId.toInt()],
                    icons[it.readingOrderId.toInt()]
                )
            }
        }
        updateTabBack()

    }

    private fun createTab(pos: Int, title: String, color: Int) {
        /*val tabOne =
            LayoutInflater.from(this).inflate(R.layout.custom_tab, tabLayout, false) as FrameLayout

        tabOne.tabInside.background = ResourceUtils.getDrawable(color)


        val param = tabOne.tabInside.layoutParams
        param.width = ResourceUtils.getDimen(if (pos == 7) R.dimen._120sdp else R.dimen._100sdp)
        tabOne.tabInside.layoutParams = param
        val tabItem = tabLayout.getTabAt(pos)
        if (tabItem != null) {
            tabItem.customView = tabOne
        }*/

        val tabOne =
            LayoutInflater.from(this).inflate(R.layout.custom_tab, tabLayout, false) as LinearLayout

        /*if (pos == selectedPosition) {
            tabOne.setBackgroundColor(ResourceUtils.getColor(R.color.transparentGrey))
        } else {
            tabOne.setBackgroundColor(ResourceUtils.getColor(R.color.transparent))
        }*/

        val tabInside = tabOne.findViewById<ImageView>(R.id.tabInside)
        val tabText = tabOne.findViewById<TextView>(R.id.tabText)

        tabInside.setImageDrawable(ResourceUtils.getDrawable(color))
        tabText.text = title

        val param = tabInside.layoutParams
        param.width =
            ResourceUtils.getDimen(if (pos == 7) R.dimen.readingIconSize else R.dimen.readingIconSize)
        tabInside.layoutParams = param
        val tabItem = tabLayout.getTabAt(pos)
        if (tabItem != null) {
            tabItem.customView = tabOne
        }
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    var selectedPosition = 0
    override fun onPageSelected(position: Int) {
        selectedPosition = position
        textToSpeech?.stop()
        PlayAudioManager.killMediaPlayer()
        isMusicAvailable(position)
        updateTabBack()
    }

    private fun updateTabBack() {
        val count = tabLayout.tabCount - 1
        for (item in 0..count) {
            if (item == selectedPosition) {
                tabLayout.getTabAt(item)?.customView
                    ?.background = ResourceUtils.getDrawable(R.drawable.bg_selectitem)
            } else {
                tabLayout.getTabAt(item)?.customView
                    ?.setBackgroundColor(ResourceUtils.getColor(R.color.transparent))
            }
        }
    }

    private fun isMusicAvailable(position: Int): Boolean {
        if (position in 0..readingBean.indices.last) {
            val isAvailable = !AppUtils.isEmpty(readingBean[position].audioFile)
                    || !AppUtils.isEmpty(readingBean[position].description)
            binding.tvMusic?.visibility = if (isAvailable) View.VISIBLE else View.INVISIBLE
            return !AppUtils.isEmpty(readingBean[position].audioFile)
        } else {
            binding.tvMusic?.visibility = View.INVISIBLE
            return false;
        }

    }

    override fun onStop() {
        super.onStop()
        textToSpeech?.stop()
        PlayAudioManager.killMediaPlayer()
    }

    private fun getDataByDate(date: String, callback: Callback<GodsWordBean?>) {
        backgroundThread.execute {
            callback.onSuccess(AppDataBase.get().godsWordDao().getItemByDate(date))
        }
    }

    object PlayAudioManager {
        private var mediaPlayer: MediaPlayer? = null

        fun playAudio(context: Context?, url: String?) {
            if (context == null) {
                return
            }

            val dialog = MyProgressDialog(context, "Loading audio...")

            try {
                mediaPlayer = MediaPlayer().apply {
                    setAudioStreamType(AudioManager.STREAM_MUSIC)
                    setDataSource(url)
                    prepareAsync()
                    setOnCompletionListener { killMediaPlayer() }
                    setOnPreparedListener {
                        dialog.dismiss()
                        it.start()
                    }
                    setOnErrorListener { mp, what, extra ->
                        killMediaPlayer()
                        Toaster.shortToast("Error in playing audio file...")
                        dialog.dismiss()
                        false
                    }
                }
            } catch (e: Exception) {
                when (e) {
                    is FileNotFoundException -> {
                        Toaster.shortToast("Audio file is not available to play...")
                    }
                    is IOException -> {
                        Toaster.shortToast("Can't play audio file...")
                    }
                    else -> {
                        Toaster.shortToast("Something went wrong...")
                    }
                }
                dialog.dismiss()
            }
        }

        fun isPlaying(): Boolean {
            return mediaPlayer?.isPlaying == true
        }


        fun killMediaPlayer() {
            mediaPlayer?.stop()
            mediaPlayer?.reset()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

}