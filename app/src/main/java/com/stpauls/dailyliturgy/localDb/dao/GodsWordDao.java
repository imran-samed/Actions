package com.stpauls.dailyliturgy.localDb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.stpauls.dailyliturgy.localDb.tables.GodsWordBean;

import java.util.List;

@Dao
public interface GodsWordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(List<GodsWordBean> items);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(GodsWordBean item);

    @Update
    void update(GodsWordBean item);

    @Delete
    void delete(GodsWordBean item);

    @Transaction
    @Query("SELECT date, date_in_normal_form, today_word, today_description, plaster, colorCode, readingsString " +
            "from GodsWordBean WHERE date_in_normal_form = :date LIMIT 1")
    GodsWordBean getItemByDate(String date);


    @Transaction
    @Query("SELECT additional_homely_tip_title, additional_homely_tips, date,date_in_normal_form  from GodsWordBean WHERE date_in_normal_form = :date LIMIT 1")
    GodsWordBean getAdditionHomelyTips(String date);

    @Transaction
    @Query("SELECT COUNT(*) FROM GodsWordBean where year = :currentYear")
    Integer getCount(String currentYear);
}
