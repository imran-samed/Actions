package com.stpauls.dailyliturgy.popularHymns

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.stpauls.dailyliturgy.R
import com.stpauls.dailyliturgy.others.Callback

class CommonPrayerHeadingAdapter(val titles: List<String>?, private val callback: Callback<String>) :
    RecyclerView.Adapter<CommonPrayerHeadingAdapter.Holder>() {

    private val bgs = listOf<Int>(
        R.drawable.bg_common_prayer_1,
        R.drawable.bg_common_prayer_2,
        R.drawable.bg_common_prayer_3,
        R.drawable.bg_common_prayer_4,
        R.drawable.bg_common_prayer_5,
        R.drawable.bg_common_prayer_6,
        R.drawable.bg_common_prayer_7
    )

    private var selectedPos = 0;


    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rlBack = itemView.findViewById<RelativeLayout>(R.id.rlBack)
        val cvMain = itemView.findViewById<CardView>(R.id.cvMain)
        val tvPrayerHeading = itemView.findViewById<TextView>(R.id.tvPrayerHeading)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hymns_header, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return titles?.size ?: 0
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.tvPrayerHeading.background =
            ContextCompat.getDrawable(holder.itemView.context, bgs[position % 7])
        holder.tvPrayerHeading.text = titles?.get(position)
        holder.itemView.setOnClickListener {
            if (selectedPos == position) {
                return@setOnClickListener
            }
            selectedPos = position;
            notifyDataSetChanged()
            callback.onSuccess(titles?.get(position)?: "-1")
        }

        if (selectedPos == position) {
            holder.cvMain.setCardBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.transparentGrey
                )
            )
        } else {
            holder.cvMain.setCardBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.white
                )
            )
        }

    }
}