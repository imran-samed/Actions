package com.stpauls.dailyliturgy.orderOfMass;

import androidx.annotation.Keep;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Keep
@Entity
public class OrderOfMassBean {

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

    @Ignore
    public boolean isExpanded;

    public OrderOfMassBean(){}

    public OrderOfMassBean(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }
}
