package com.stpauls.dailyliturgy.localDb.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.stpauls.dailyliturgy.popularHymns.bean.PopularHymnsBean;

import java.util.List;

@Dao
public interface PopularHymnDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(List<PopularHymnsBean> items);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(PopularHymnsBean items);

    @Update
    void update(PopularHymnsBean items);

    @Query("SELECT * FROM PopularHymnsBean")
    List<PopularHymnsBean> getAllHymns();
}
