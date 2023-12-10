package com.example.wallpaper_appliction.activities

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.arefbhrn.eprdownloader.EPRDownloader
import com.bumptech.glide.Glide
import com.example.wallpaper_appliction.databinding.ActivityDetailsBinding
import com.example.wallpaper_appliction.helperclass.downloadimage
import com.example.wallpaper_appliction.utils.DownloadProgressListener
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
class Detailsimage : AppCompatActivity(), DownloadProgressListener {
    private val binding: ActivityDetailsBinding by lazy {
        ActivityDetailsBinding.inflate(layoutInflater)
    }
    private var ispreviewing = false
    lateinit var imguri: String
    private var downloadedimage: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        EPRDownloader.initialize(applicationContext)
        setContentView(binding.root)

        setupimgurl()

        binding.download.setOnClickListener {
            requestpermision()
        }
        binding.preview.setOnClickListener {
            preview()
        }
        binding.share.setOnClickListener {
            share()
        }
        binding.setas.setOnClickListener {
            setas()
        }


    }




    private fun setas() {
        if (downloadedimage != null) {
            setwallpaper(downloadedimage!!)
        } else {
            Toast.makeText(this, "Image not downloaded yet.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setwallpaper(downloadedimage: Uri) {

        val intent = Intent(Intent.ACTION_ATTACH_DATA)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Add this if your targetVersion is more than Android 7.0+
        intent.setDataAndType(downloadedimage, "image/jpeg")
        intent.putExtra("mimeType", "image/jpeg")
        startActivity(Intent.createChooser(intent, "Set as:"))


    }

    private fun share() {

        if (downloadedimage != null) {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "image/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, downloadedimage)
            val chooser = Intent.createChooser(shareIntent, "Share Image")

            if (shareIntent.resolveActivity(packageManager) != null) {
                startActivity(chooser)
            } else {
                Toast.makeText(this, "No apps available for sharing", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Image not downloaded yet.", Toast.LENGTH_SHORT).show()
        }

    }


    private fun setupimgurl() {
        imguri = intent.getStringExtra("imageuri").toString()
        Glide.with(this).asBitmap().load(imguri).into(binding.image)
    }

    private fun preview() {
        supportActionBar!!.hide()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        // Get the current date and time
        val currentDate = dateFormat.format(Date())
        val currentTime = timeFormat.format(Date())

        // Set the formatted date and time in your TextViews
        binding.date.text = currentDate
        binding.time.text = currentTime

        // Update the visibility of views
        binding.linearLayout.visibility = View.INVISIBLE
        binding.previewicon.visibility = View.VISIBLE
        binding.time.visibility = View.VISIBLE
        binding.date.visibility = View.VISIBLE

        ispreviewing = true

    }


    private fun requestpermision() {
        Dexter.withContext(this@Detailsimage)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    if (p0?.areAllPermissionsGranted()!!) {
                        // Check if download is not already in progress

                        val downloadTask = downloadimage(
                            this@Detailsimage,
                            this@Detailsimage,
                            object : downloadimage.DownloadCallback {
                                override fun onDownloadComplete(result: File?) {
                                    if (result != null && result.exists() && result.length() > 0) {
                                        binding.setas.visibility = View.VISIBLE
                                        binding.download.visibility = View.INVISIBLE
                                        downloadedimage = FileProvider.getUriForFile(
                                            this@Detailsimage,
                                            "${packageName}.provider",
                                            result
                                        )
                                    } else {
                                        Toast.makeText(
                                            this@Detailsimage,
                                            "Please download first...",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            })
                        downloadTask.execute(imguri)

                    } else {
                        Toast.makeText(
                            this@Detailsimage,
                            "Please give all necessary permissions.",
                            Toast.LENGTH_SHORT
                        ).show()
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

    override fun onProgressUpdate(progress: Int) {

    }


}