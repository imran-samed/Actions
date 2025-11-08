package com.stpauls.dailyliturgy.localDb.tables;

import androidx.annotation.Keep;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
@Keep
@Entity
public class ReadingBean implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public long id;


    public long date;
    @SerializedName("order_id")
    public long readingOrderId;
    @SerializedName("title")
    public String title;
    @SerializedName("description")
    public String description;
    @SerializedName("audio")
    public String audioFile;

}
