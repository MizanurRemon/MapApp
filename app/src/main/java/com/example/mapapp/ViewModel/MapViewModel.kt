package com.example.mapapp.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapapp.Api.ApiRepository
import com.example.mapapp.Models.NearbyResponse
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {

    val nearByPlaces: MutableLiveData<NearbyResponse> = MutableLiveData()

    fun nearByPlaces(
        longitude: String, latitude: String, ptype: String
    ) {
        viewModelScope.launch {
            ApiRepository.getNearByPlaces(longitude, latitude, ptype).catch {
                Log.d("dataxx", "nearByPlacesError: ${it.message}")
            }.collect {
                nearByPlaces.value = it
            }
        }
    }
}