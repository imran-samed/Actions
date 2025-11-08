package com.stpauls.dailyliturgy.godsWord

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.stpauls.dailyliturgy.Global
import com.stpauls.dailyliturgy.localDb.tables.ReadingBean
import java.util.*


class MyPagerAdapter(
    fragmentManager: FragmentManager,
    val bean: List<ReadingBean>
) : FragmentStatePagerAdapter(
    fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {


    private val mObservers: Observable = FragmentObserver()


    override fun getItem(position: Int): Fragment {
        mObservers.deleteObservers()
        val frag = TextFragment.newInstance(bean[position])
        if (frag is Observer) {
            mObservers.addObserver(frag as Observer)
        }
        return frag
    }

    override fun getCount(): Int {
        return bean.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return try {
            Global.titles[bean[position].readingOrderId.toInt()]
        } catch (e: Exception) {
            "  --  "
        }
    }

    fun updateFragments(i: Float) {
        mObservers.notifyObservers()
    }

    override fun getItemPosition(data: Any): Int {
        return PagerAdapter.POSITION_NONE
    }


}