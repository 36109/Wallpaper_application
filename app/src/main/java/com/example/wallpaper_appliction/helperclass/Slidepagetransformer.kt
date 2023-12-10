package com.example.wallpaper_appliction.helperclass

import android.view.View
import androidx.viewpager2.widget.ViewPager2

class Slidepagetransformer:ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        page.setAlpha(0f)
        page.setVisibility(View.VISIBLE)

        page.animate()
            .alpha(1f)
            .setDuration(page.getResources().getInteger(android.R.integer.config_shortAnimTime).toLong());
    }
}