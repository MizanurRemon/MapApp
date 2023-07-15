package com.example.mapapp.Api

import com.example.mapapp.Utils.Urls
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetrofitBuilder {
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Urls.BASEURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}