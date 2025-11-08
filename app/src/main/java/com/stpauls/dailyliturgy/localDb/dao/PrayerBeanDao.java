package com.stpauls.dailyliturgy.localDb.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.stpauls.dailyliturgy.commonPrayers.bean.PrayerBean;

import java.util.List;

@Dao
public interface PrayerBeanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(List<PrayerBean> items);

    @Query("SELECT * FROM PrayerBean")
    List<PrayerBean> getAllPrayer();

    @Query("SELECT COUNT(*) FROM PrayerBean")
    Integer getCount();
}
