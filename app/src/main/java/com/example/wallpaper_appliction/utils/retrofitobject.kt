package com.example.wallpaper_appliction.utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object retrofitobject {
    private const val API_KEY="cXYXn7oN5LSyEugAOOKbUlit39HpEO2ylWQyGvW3O8QoNSsjrLobsrMI"

    val retrofit by lazy {
        Retrofit.Builder().baseUrl("https://api.pexels.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val apiinterface by lazy {
        retrofit.create(retrofitservice::class.java)
    }
}