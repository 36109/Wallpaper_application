package com.example.wallpaper_appliction.utils

import android.content.Context

interface Toast {
    fun toast(context: Context,msg:String){
        android.widget.Toast.makeText(context, "$msg", android.widget.Toast.LENGTH_SHORT).show()
    }
}