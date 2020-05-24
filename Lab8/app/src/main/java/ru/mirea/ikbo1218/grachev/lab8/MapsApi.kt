package ru.mirea.ikbo1218.grachev.lab8

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MapsApi {

    @GET("directions/json")
    fun getDirection(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("sensor") sensor: String = "false",
        @Query("mode") mode:String = "driving",
        @Query("key") key: String
    ): Call<MapDTO>
}