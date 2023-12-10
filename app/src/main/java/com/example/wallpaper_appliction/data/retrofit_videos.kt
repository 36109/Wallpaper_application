package com.example.wallpaper_appliction.data

data class retrofit_videos(
    val page: Int,
    val per_page: Int,
    val total_results: Int,
    val url: String,
    val videos: List<VideoX>
)