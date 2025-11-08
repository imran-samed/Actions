package com.stpauls.dailyliturgy.retrofit

import com.google.gson.GsonBuilder
import com.stpauls.dailyliturgy.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitClient {
    private var retrofit: RetrofitApi? = null
    //private const val BASE_URL: String = "http://3.7.238.8/api/"
    //const val BASE_URL: String = "http://liturgyforeachday.com/admin/public/index.php/api/"
    const val BASE_URL1: String = "https://liturgyforeachday.com/api/"
    const val BASE_URL: String = BuildConfig.BASE_URL;

    fun getRetrofitApi(): RetrofitApi {
        if (retrofit == null) {

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .addInterceptor(object : Interceptor {
                    override fun intercept(chain: Interceptor.Chain): Response {
                        val request: Request = chain.request()
                        val request2: Request = request.newBuilder()
                            .addHeader(RetrofitApi.KEY, RetrofitApi.VALUE).build()
                        return chain.proceed(request2)
                    }
                })
                .build()


            val rtr = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(
                    GsonConverterFactory.create(
                        GsonBuilder()
                            .setLenient()
                            .create()
                    )
                )
                .client(okHttpClient)
                .build()
            retrofit = rtr.create(RetrofitApi::class.java)
        }
        return retrofit!!
    }
}