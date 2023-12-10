package com.example.wallpaper_appliction.data

data class searchdata(
    val next_page: String,
    val page: Int,
    val per_page: Int,
    val photos: List<PhotoXX>,
    val total_results: Int
)