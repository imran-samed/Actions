package com.stpauls.dailyliturgy.commonPrayers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.stpauls.dailyliturgy.Global;
import com.stpauls.dailyliturgy.R;

public class CommonPrayerAdapter extends RecyclerView.Adapter<CommonPrayerAdapter.Holder> {

   /* private int[] bgs = new int[]{R.drawable.bg_common_prayer_1,
            R.drawable.bg_common_prayer_2,
            R.drawable.bg_common_prayer_3,
            R.drawable.bg_common_prayer_4,
            R.drawable.bg_common_prayer_5,
            R.drawable.bg_common_prayer_6,
            R.drawable.bg_common_prayer_7};*/

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_common_prayer_header, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.ivIcon.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), Global.INSTANCE.getBgs()[7 % position]));
    }

    @Override
    public int getItemCount() {
        return Global.INSTANCE.getBgs().length;
    }

    public class Holder extends RecyclerView.ViewHolder {
        private final ImageView ivIcon;

        public Holder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivIcon);
        }
    }
}
