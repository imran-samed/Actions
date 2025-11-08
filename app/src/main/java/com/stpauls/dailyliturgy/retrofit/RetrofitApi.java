package com.stpauls.dailyliturgy.retrofit;

import com.google.gson.JsonObject;
import com.stpauls.dailyliturgy.base.BaseResponse;
import com.stpauls.dailyliturgy.commonPrayers.bean.PrayerBean;
import com.stpauls.dailyliturgy.localDb.tables.GodsWordBean;
import com.stpauls.dailyliturgy.orderOfMass.OrderOfMassBean;
import com.stpauls.dailyliturgy.popularHymns.bean.PopularHymnsBean;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitApi {
    String KEY = "Authorization";
    String VALUE = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiNGVlMjY1YWQ0OGI3NTQyMjFhZWM4NzE0Y2Y5ZDc2ZDUzZjAyNjY0OTNhZDI0M2U3YzI4NzgyNmMyZGQwMTljMzhiNWM3ZWQ0ZDkwZjZjNjIiLCJpYXQiOjE2MDA3NzE3NTAsIm5iZiI6MTYwMDc3MTc1MCwiZXhwIjoxNjMyMzA3NzUwLCJzdWIiOiIxIiwic2NvcGVzIjpbXX0.m4seNl7IOJEpiqOeNSmWRGNsbG5N7zEkx6AlE-VeY6oEEu1HbLlaMTW-3SVUaK-SWDucer60jlLjHsUDzMjFJ6cV2dj-YD0EzbgQyNmeZQvP-TLEfaYBLFFPUoDcunDh-Q3yCH9osAnPLJE6laMheE7S_URd4T8wMqqt1iKOd4fcsXfhVN307u2spUcFcLbeOrJ-dZ6RszWSfc2HoKg01wskKAeIrVnld77LoJlTiO9SnOhN-ShucdeReQQ2wDT3d7nzp_K4P62PmMWdb7rxmvlWRKsXHrxJNFtQBtKeXXbPaP4pnu3A6GUeXgJCuvdOkAkZBfEBetvPfO8JzrWiSpuNo3LNrUZwUQ2mHy5PdCZfXhQOf_U7eIVpFoABg-ME_G64ZgVw4CdIZjvLY8jHsUrnkIAZk6-OfSG3JhyRpw_GWLMCte8cX07pXEiAzCv-oRF3WtvuxQaUqWbW4926THG8xgJZ7DlJYtuvxmyCWPSpvvaPaMHud-rtIiOaTz9tMCNNC89Ee9iHI5Mk9gSpd_zw1oxEoHYZg-CNoX3UZfZVmibJrtTfJxp0Bj4jJb4tZLTGXE6KINiD102u3Eu4wexcfRJIXtPQZ5vNA1ZCpxaw2l_eENzUPJ6n_hbw4ZzRrRusUQCCa1jQFfCmNv2J7YHVchEdpG0JsURDOIIU-dk";

    @GET("hymnsSongs")
    Call<BaseResponse<List<PopularHymnsBean>>> getSong(@Query("device_id") String deviceId,
                                                       @Query("need_updated") String needUpdated);

    @GET("getdata")
    Call<BaseResponse<List<GodsWordBean>>> getGodsWord(@Query("year") String year);

    @GET("updatedcaldata")
    Call<BaseResponse<List<GodsWordBean>>> getUpdateData(@Query("year") String year,
                                                         @Query("device_id") String deviceId,
                                                         @Query("need_updated") String needUpdated);
    @GET("order_of_mass")
    Call<JsonObject> getOrderOfMass() ;

    @GET("setting")
    Call<JsonObject> getSetting() ;

    @GET("about_us")
    Call<JsonObject> getAboutUs() ;

    @GET("prayerCollection")
    Call<BaseResponse<List<PrayerBean>>> getPrayerCollection(@Query("device_id") String deviceId,
                                                             @Query("need_updated") String needUpdated);

    @GET("orderMassCollection")
    Call<BaseResponse<List<OrderOfMassBean>>> newOrderOfMass(@Query("device_id") String deviceId,
                                                             @Query("need_updated") String needUpdated);

}