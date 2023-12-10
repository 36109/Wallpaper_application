package com.example.wallpaper_appliction.utils

sealed class Resorces<T>(
    val data: T? =null,
    val msg: String?=null
){
    class success<T>(data: T?):Resorces<T>(data)
    class failure<T>(msg: String?):Resorces<T>(msg=msg)
    class loading<T>():Resorces<T>()
    class unspecified<T>():Resorces<T>()
}

