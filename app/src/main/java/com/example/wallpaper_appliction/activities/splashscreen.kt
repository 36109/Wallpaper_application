package com.example.wallpaper_appliction.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.wallpaper_appliction.R

class splashscreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        supportActionBar?.hide()
        setContentView(R.layout.activity_splashscreen)

        Handler().postDelayed({
            startActivity(Intent(this,MainActivity::class.java))
            finish()

        },4000)
    }
}