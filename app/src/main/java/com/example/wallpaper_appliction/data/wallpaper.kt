package com.example.wallpaper_appliction.data

data class wallpaper(
    val id:String,
    val category:String,
    val link:String
){
    constructor():this("","","")
}
