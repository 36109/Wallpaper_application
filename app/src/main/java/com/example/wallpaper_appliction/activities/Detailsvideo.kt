package com.example.wallpaper_appliction.activities




import android.Manifest
import android.app.ProgressDialog
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.webkit.URLUtil
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.arefbhrn.eprdownloader.*
import com.example.wallpaper_appliction.Application
import com.example.wallpaper_appliction.databinding.ActivityDetailsBinding
import com.example.wallpaper_appliction.helperclass.VideoWallpaperService

import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class Detailsvideo : AppCompatActivity() {
    private val binding: ActivityDetailsBinding by lazy {
        ActivityDetailsBinding.inflate(layoutInflater)
    }
    lateinit var localFilePath: String

    private var ispreviewing = false
    lateinit var videouri: String
    private var isVideoDownloaded = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        EPRDownloader.initialize(applicationContext)
        setContentView(binding.root)

        binding.share.visibility = View.INVISIBLE
        binding.download.setOnClickListener {
            requestpermision()
        }
        binding.preview.setOnClickListener {
            preview()
        }
        binding.setas.setOnClickListener {
            if (checkVideoFileExists(localFilePath)) {
                setLiveWallpaper(localFilePath)

            } else {
                Toast.makeText(this@Detailsvideo, "Video file does not exist", Toast.LENGTH_SHORT).show()
            }
        }
        setupvideo()


    }
    private fun setupvideo() {
        videouri = intent.getStringExtra("videouri") ?: ""
        Log.d("@@@@", "setupvideo:$videouri ")
        binding.progress.visibility = View.VISIBLE


        binding.videoview.setVideoURI(Uri.parse(videouri))

        binding.videoview.setOnPreparedListener {
            binding.progress.visibility = View.INVISIBLE
            binding.videoview.start()

            isVideoDownloaded = true
        }


    }

    private fun preview() {
        supportActionBar!!.hide()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val currentDate = dateFormat.format(Date())
        val currentTime = timeFormat.format(Date())

        binding.date.text = currentDate
        binding.time.text = currentTime

        binding.linearLayout.visibility = View.INVISIBLE
        binding.previewicon.visibility = View.VISIBLE
        binding.time.visibility = View.VISIBLE
        binding.date.visibility = View.VISIBLE

        ispreviewing = true

    }


    private fun requestpermision() {

        Dexter.withContext(this@Detailsvideo)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    if (p0?.areAllPermissionsGranted()!!) {
                        downloadVideo(this@Detailsvideo, videouri)
                    } else {
                        Toast.makeText(
                            this@Detailsvideo,
                            "Plase give Alll nessesary permission..",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }


            }).check()
    }

    private fun downloadVideo(context: Context, url: String) {
        val progressbar = ProgressDialog(this)
        progressbar.setMessage("Downloading...")
        progressbar.setCancelable(false)
        progressbar.show()

        val file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val filename = URLUtil.guessFileName(url, null, null)
        val filepath = file.path + File.separator + filename
        EPRDownloader.download(url, file.path, filename).build()
            .addOnStartOrResumeListener { }
            .addOnPauseListener { }
            .addOnCancelListener(object : OnCancelListener {
                override fun onCancel() {

                }
            })
            .addOnProgressListener(object : OnProgressListener {
                override fun onProgress(progress: Progress?) {
                    val per = progress!!.currentBytes * 100 / progress.totalBytes
                    progressbar.setMessage("Downloading...$per%")
                }
            })
            .addOnDownloadListener(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    Toast.makeText(
                        this@Detailsvideo,
                        "Download succsefully...",
                        Toast.LENGTH_SHORT
                    )
                        .show()

                    progressbar.dismiss()
                    binding.setas.visibility = View.VISIBLE
                    localFilePath = filepath
                    MediaScannerConnection.scanFile(
                        context,
                        arrayOf(filepath),
                        null
                    ) { _, _ -> }

                }

                override fun onError(error: Error?) {
                    Toast.makeText(this@Detailsvideo, "Download error...", Toast.LENGTH_SHORT)
                        .show()
                    progressbar.dismiss()
                }


            }).start()


    }



    private fun setLiveWallpaper(localFilePath: String) {
        Log.d("####", "setLiveWallpaper: $localFilePath")
        val wallpaperIntent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
        wallpaperIntent.putExtra(
            WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
            ComponentName(this, VideoWallpaperService::class.java)
        )

        val myApplication = application as Application
        myApplication.videoPath = localFilePath
        if (wallpaperIntent.resolveActivity(packageManager) != null) {
            try {
                startActivity(wallpaperIntent)
            } catch (e: Exception) {
                Toast.makeText(this@Detailsvideo, "Error setting live wallpaper: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this@Detailsvideo, "Live wallpaper service not available on this device.", Toast.LENGTH_SHORT).show()
        }
    }





    override fun onBackPressed() {
        if (ispreviewing) {
            supportActionBar!!.show()
            binding.linearLayout.visibility = View.VISIBLE
            binding.previewicon.visibility = View.INVISIBLE
            binding.time.visibility = View.INVISIBLE
            binding.date.visibility = View.INVISIBLE
            ispreviewing = false
        } else {
            super.onBackPressed()
        }

    }
    private fun checkVideoFileExists(videoPath: String): Boolean {
        return File(videoPath).exists()
    }


}