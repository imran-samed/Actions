package com.stpauls.dailyliturgy.localDb.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.stpauls.dailyliturgy.commonPrayers.bean.PrayerBean;
import com.stpauls.dailyliturgy.orderOfMass.OrderOfMassBean;

import java.util.List;

@Dao
public interface OrderOfMassBeanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(List<OrderOfMassBean> items);

    @Query("SELECT * FROM OrderOfMassBean")
    List<OrderOfMassBean> getAllPrayer();

    @Query("SELECT COUNT(*) FROM OrderOfMassBean")
    Integer getCount();
}
