package com.example.wallpaper_appliction

import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application : android.app.Application() {
    var videoPath: String? = null


}