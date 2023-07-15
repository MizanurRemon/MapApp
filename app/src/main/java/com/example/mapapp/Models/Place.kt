package com.example.mapapp.Models


data class Place(
    val Address: String,
    //val ST_AsText(location): String,
    val area: String,
    val city: String,
    val contact_person_phone: Any,
    val distance_in_meters: Double,
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val pType: String,
    val subType: String,
    val uCode: String
)