
package com.stpauls.dailyliturgy.popularHymns.bean;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.stpauls.dailyliturgy.Global;

import java.io.File;


@Keep
@Entity
public class PopularHymnsBean {

    @SerializedName("audio_file_url")
    public String mAudioFileUrl;
    @SerializedName("audio_name")
    public String mAudioName;
    @SerializedName("category_name")
    public String mCategoryName;
    @SerializedName("category_icon")
    public String mCategoryIcon;
    @SerializedName("id")
    @PrimaryKey
    @NonNull
    public Long mId;



    @Ignore
    public boolean isFileAvailable(){
        return new File(getLocalFilePath()).exists();
    }

    @Ignore
    public String getLocalFilePath() {
        localFilePath = Global.INSTANCE.getPOPULAR_HYMNS_FOLDER_PATH()+"/"+mAudioName;
        return localFilePath;
    }

    public String localFilePath;

}
