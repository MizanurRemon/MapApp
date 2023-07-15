package com.example.mapapp.Api

import com.example.mapapp.Models.NearbyResponse
import com.example.mapapp.Utils.Urls
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("search/nearby/category/NDgwMzpDRUYzRkdWOUE5/1/10/")
    suspend fun getNearByPlaces(
        @Query("longitude") longitude: String,
        @Query("latitude") latitude: String,
        @Query("ptype") ptype: String
    ): NearbyResponse
}