package com.stpauls.dailyliturgy.base;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;
@Keep
public class BaseResponse<T> {
    @SerializedName("success")
    public boolean success;

    @SerializedName("message")
    public String message;

    @SerializedName("data")
    public T mData;
}
