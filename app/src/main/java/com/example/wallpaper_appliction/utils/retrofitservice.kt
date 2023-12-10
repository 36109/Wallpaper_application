package com.example.wallpaper_appliction.utils

import com.example.wallpaper_appliction.data.retrofir_images
import com.example.wallpaper_appliction.data.retrofit_videos
import com.example.wallpaper_appliction.data.searchdata
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface retrofitservice {
    // val ApiKey:String="cXYXn7oN5LSyEugAOOKbUlit39HpEO2ylWQyGvW3O8QoNSsjrLobsrMI"
    @GET("v1/curated")
    fun getimages(
        @Header("Authorization") apiKey: String,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int
    ): Call<retrofir_images>

    @GET("videos/popular")
    fun getvideos(
        @Header("Authorization") apiKey: String,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int,
        @Query("max_duration") max_duration: Int,
        @Query("min_duration") min_duration: Int
    ): Call<retrofit_videos>

    @GET("v1/search")
    fun getsearchdata(
        @Header("Authorization") apiKey: String,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int,
        @Query("query") query: String,
        @Query("total_results") total_results: Int
    ): Call<searchdata>
}