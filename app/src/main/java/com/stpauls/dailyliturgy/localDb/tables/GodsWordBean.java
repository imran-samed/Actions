package com.stpauls.dailyliturgy.localDb.tables;

import android.text.TextUtils;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.Arrays;
import java.util.List;

@Keep
@Entity
public class GodsWordBean {

    @SerializedName("date")
    public long date;
    @PrimaryKey
    @SerializedName("date_in_normal_form")
    @NonNull public String date_in_normal_form;
    @SerializedName("additional_homely_tip_title")
    public String additional_homely_tip_title;
    @SerializedName("additional_homely_tips")
    public String additional_homely_tips;
    @SerializedName("today_word")
    public String today_word;
    @SerializedName("today_description")
    public String today_description;
    @SerializedName("plaster")
    public String plaster;
    @Ignore
    @SerializedName("color")
    public List<String> color;
    public String colorCode;
    @SerializedName("readings")
    @Ignore
    public List<ReadingBean> readingBeans;
    public String readingsString;
    public String year;

    public GodsWordBean() {
    }

    @Ignore
    public GodsWordBean(GodsWordBean bean, List<ReadingBean> read2) {
        this.date = bean.date;
        this.date_in_normal_form = bean.date_in_normal_form;
        this.additional_homely_tips = bean.additional_homely_tips;
        this.additional_homely_tip_title = bean.additional_homely_tip_title;
        this.today_word = bean.today_word;
        this.today_description = bean.today_description;
        this.plaster = bean.plaster;
        this.color = bean.color;
        this.readingBeans = read2;
        this.readingsString = bean.getReadingsString();
        this.colorCode = TextUtils.join(",", bean.color);
        this.color = Arrays.asList(colorCode.split(","));
    }

    @Ignore
    public String getReadingsString() {
        return new Gson().toJson(readingBeans);
    }


    @Ignore
    public List<ReadingBean> getReadingBeans() {
        return new Gson().fromJson(readingsString, new TypeToken<List<ReadingBean>>() {
        }.getType());
    }
}
