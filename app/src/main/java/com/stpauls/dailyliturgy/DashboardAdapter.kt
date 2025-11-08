package com.stpauls.dailyliturgy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stpauls.dailyliturgy.godsWord.BibleActivity

class DashboardAdapter : RecyclerView.Adapter<DashboardAdapter.MyHolder>() {
    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardAdapter.MyHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dashboard, parent, false)
        return MyHolder(view)
    }

    override fun getItemCount(): Int{
        return 10
    }

    override fun onBindViewHolder(holder: DashboardAdapter.MyHolder, position: Int) {
        holder.itemView.setOnClickListener {
            BibleActivity.start(holder.itemView.context)
        }
    }
}