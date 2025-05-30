package com.example.weatherapp


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


    interface ApiInterface {
        @GET("data/2.5/weather")
        fun getWeatherData(
            @Query("q") city: String,
            @Query("appid") apiKey: String,
            @Query("units") units: String
        ): Call<WeatherApp>
    }