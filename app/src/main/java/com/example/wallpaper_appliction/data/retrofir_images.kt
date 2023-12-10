package com.example.wallpaper_appliction.data

data class retrofir_images(
    val next_page: String,
    val page: Int,
    val per_page: Int,
    val photos: List<Photo>
)