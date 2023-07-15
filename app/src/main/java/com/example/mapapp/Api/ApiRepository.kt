package com.example.mapapp.Api

import com.example.mapapp.Models.NearbyResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


class ApiRepository {


    companion object {
        //lateinit var apiService: ApiService = ApiService()

        fun getNearByPlaces(
            longitude: String,
            latitude: String,
            ptype: String
        ): Flow<NearbyResponse> = flow {
            val response = RetrofitBuilder.apiService.getNearByPlaces(longitude, latitude, ptype)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }
}