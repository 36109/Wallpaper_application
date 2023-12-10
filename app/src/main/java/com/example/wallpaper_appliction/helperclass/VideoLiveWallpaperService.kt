package com.example.wallpaper_appliction.helperclass

import android.media.MediaPlayer
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import android.widget.Toast
import com.example.wallpaper_appliction.Application
import java.io.IOException

class VideoWallpaperService : WallpaperService() {

    override fun onCreateEngine(): WallpaperService.Engine {
        return VideoEngine()
    }

    inner class VideoEngine : WallpaperService.Engine() {
        private var mediaPlayer: MediaPlayer? = null
        private var videoPath: String? = null


        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)





        }

        private fun initializeMediaPlayer(videopath:String) {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer()
            try {
                mediaPlayer?.setDataSource(videoPath)
                mediaPlayer?.setSurface(surfaceHolder.surface)
                mediaPlayer?.isLooping = true
                mediaPlayer?.prepare()

            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this@VideoWallpaperService, e.message.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
        }


        override fun onDestroy() {
            super.onDestroy()
            mediaPlayer?.release()
            mediaPlayer = null
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            val myApplication = application as Application
            videoPath = myApplication.videoPath
            if (visible) {
                if (videoPath!=null){
                    initializeMediaPlayer(videoPath!!)
                    mediaPlayer?.start()
                }
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            mediaPlayer?.pause()
        }

    }
}
