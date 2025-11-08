package com.stpauls.dailyliturgy.commonPrayers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.stpauls.dailyliturgy.R
import com.stpauls.dailyliturgy.base.BaseActivity
import com.stpauls.dailyliturgy.commonPrayers.bean.PrayerBean
import com.stpauls.dailyliturgy.others.AppUtils
import com.stpauls.dailyliturgy.others.setVisible

class PrayerAdapter(val mCtx: BaseActivity, private var prayer: MutableList<PrayerBean>?, private val fromOrderOfMass : Boolean) :
    RecyclerView.Adapter<PrayerAdapter.Holder>() {

    private val isNewOrderOfMassSection =  fromOrderOfMass
    private var selectedPosition = if (fromOrderOfMass) 0 else -2
    private val openIcon = R.drawable.ic_baseline_keyboard_arrow_down_24
    private val closeIcon = R.drawable.ic_baseline_keyboard_arrow_up_24


    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDescription = itemView.findViewById<TextView>(R.id.tvDescription)
        val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_prayer_expand_item, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return prayer?.size ?: 0
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = prayer?.get(position)
        holder.tvTitle?.setVisible(!isNewOrderOfMassSection)
        holder.tvTitle.text = item?.mTitle
        AppUtils.setHTMLString(holder.tvDescription, item?.mDescription)

        holder.tvTitle.setOnClickListener {
            selectedPosition = when {
                isNewOrderOfMassSection -> {
                    0
                }
                selectedPosition == position -> {
                    -2;
                }
                else -> {
                    position;
                }
            }
            notifyDataSetChanged()
        }
        if (selectedPosition == position) {
            holder.tvDescription.visibility = View.VISIBLE
            setICon(holder.tvTitle, closeIcon)
        } else {
            holder.tvDescription.visibility = View.GONE
            setICon(holder.tvTitle, openIcon)

        }
        AppUtils.setFontSize(mCtx, holder.tvTitle)
        AppUtils.setFontSize(mCtx, holder.tvDescription)

    }

    private fun setICon(view: TextView?, icon: Int){
        view?.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            icon,
            0
        );
    }


    fun notifyData(mutableList: MutableList<PrayerBean>?) {
        if (mutableList.isNullOrEmpty()) {
            return
        }
        val items = ArrayList(mutableList)
        prayer?.clear()
        prayer?.addAll(items)
        notifyDataSetChanged()
    }

    fun notifyDataWithSelectedPosition(mutableList: MutableList<PrayerBean>?) {
        notifyData(mutableList)
        selectedPosition = 0
    }
}