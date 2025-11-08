package com.stpauls.dailyliturgy.localDb;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.stpauls.dailyliturgy.base.App;
import com.stpauls.dailyliturgy.commonPrayers.bean.PrayerBean;
import com.stpauls.dailyliturgy.localDb.dao.GodsWordDao;
import com.stpauls.dailyliturgy.localDb.dao.OrderOfMassBeanDao;
import com.stpauls.dailyliturgy.localDb.dao.PopularHymnDao;
import com.stpauls.dailyliturgy.localDb.dao.PrayerBeanDao;
import com.stpauls.dailyliturgy.localDb.tables.GodsWordBean;
import com.stpauls.dailyliturgy.orderOfMass.OrderOfMass;
import com.stpauls.dailyliturgy.orderOfMass.OrderOfMassBean;
import com.stpauls.dailyliturgy.popularHymns.bean.PopularHymnsBean;
import com.stpauls.dailyliturgy.threads.DefaultExecutorSupplier;

import org.jetbrains.annotations.Nullable;

import java.util.List;

@Database(entities = {GodsWordBean.class, PopularHymnsBean.class, PrayerBean.class, OrderOfMassBean.class},
        version = 6, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {
    private static AppDataBase INSTANCE;

    public abstract GodsWordDao godsWordDao();

    public abstract PopularHymnDao popularHymnDao();

    public abstract PrayerBeanDao prayerBeanDao();

    public abstract OrderOfMassBeanDao orderOfMass();



    public static AppDataBase get() {
        if (INSTANCE == null) {
            synchronized (AppDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(App.get(),
                            AppDataBase.class,
                            "com.stpauls.godsword.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }

            }
        }
        return INSTANCE;

    }

    private static final Migration migration_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            /**
             *    @SerializedName("category_name")
             *     public String mCategoryName;
             *     @SerializedName("category_icon")
             *     public String mCategoryIcon;
             *     @SerializedName("title")
             *     public String mTitle;
             *     @SerializedName("description")
             *     public String mDescription;
             */

            database.execSQL("CREATE TABLE OrderOfMassBean " +
                    "(id INTEGER NOT NULL, " +
                    "mCategoryName TEXT , " +
                    "mCategoryIcon TEXT , " +
                    "mTitle TEXT NOT NULL, " +
                    "mDescription TEXT , " +
                    "content_id INTEGER , "+
                    "PRIMARY KEY(id))");
        }
    };

    public static void saveData(List<GodsWordBean> beans) {
        DefaultExecutorSupplier.get().forBackgroundTasks().execute(() -> {
            get().godsWordDao().saveAll(beans);
        });
    }

    public static void savePopularHymns(List<PopularHymnsBean> beans, com.stpauls.dailyliturgy.others.Callback<Void>  callback) {
        DefaultExecutorSupplier.get().forBackgroundTasks().execute(() -> {
            get().popularHymnDao().saveAll(beans);
            callback.onSuccess(null);
        });
    }

    public static void savePrayerCollection(List<PrayerBean> beans, com.stpauls.dailyliturgy.others.Callback<Void> callback) {
        DefaultExecutorSupplier.get().forBackgroundTasks().execute(() -> {
            get().prayerBeanDao().saveAll(beans);
            callback.onSuccess(null);
        });
    }

    public static void saveOrderOfMass(List<OrderOfMassBean> beans, com.stpauls.dailyliturgy.others.Callback<Void> callback) {
        DefaultExecutorSupplier.get().forBackgroundTasks().execute(() -> {
            get().orderOfMass().saveAll(beans);
            callback.onSuccess(null);
        });
    }

    public static void saveDownloadFileDetail(@Nullable PopularHymnsBean item) {
        DefaultExecutorSupplier.get().forBackgroundTasks().execute(() -> {
            get().popularHymnDao().update(item);
        });
    }
}
