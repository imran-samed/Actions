package com.stpauls.dailyliturgy.commonPrayers.bean;

import androidx.annotation.Keep;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Keep
@Entity
public class PrayerBean {

    @SerializedName("id")
    @PrimaryKey(autoGenerate = false)
    public long mId;

    @SerializedName("category_name")
    public String mCategoryName;
    @SerializedName("category_icon")
    public String mCategoryIcon;
    @SerializedName("title")
    public String mTitle;
    @SerializedName("description")
    public String mDescription;


    public boolean isExpanded;

    public PrayerBean (){}
    public PrayerBean(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }
}
