package com.example.wallpaper_appliction.Fragments


import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.wallpaper_appliction.Application
import com.example.wallpaper_appliction.databinding.GalleryvideosBinding
import com.example.wallpaper_appliction.helperclass.VideoWallpaperService

import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

@AndroidEntryPoint
class Galleryvideos : Fragment() {
    val binding: GalleryvideosBinding by lazy {
        GalleryvideosBinding.inflate(layoutInflater)
    }

    private var videopath: String? = null
    private val contract =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                videopath = getRealPathFromUri(it)
                videopath?.let { path ->
                    setLiveWallpaper(path)
                }

            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.selectVideo.setOnClickListener {
            contract.launch("video/*")
        }


    }


    private fun setLiveWallpaper(localFilePath: String) {
        Log.d("####", "setLiveWallpaper: $localFilePath")
        val wallpaperIntent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
        wallpaperIntent.putExtra(
            WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
            ComponentName(requireContext(), VideoWallpaperService::class.java)
        )

        val myApplication = requireActivity().application as Application
        myApplication.videoPath = localFilePath

        if (wallpaperIntent.resolveActivity(requireActivity().packageManager) != null) {
            try {
                startActivity(wallpaperIntent)

            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error setting live wallpaper: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                requireContext(),
                "Live wallpaper service not available on this device.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getRealPathFromUri(uri: Uri): String? {
        var inputStream: InputStream? = null
        var filePath: String? = null

        try {
            val contentResolver = requireContext().contentResolver
            inputStream = contentResolver.openInputStream(uri)

            val fileName = getFileNameFromUri(uri)

            val cacheDir = requireContext().cacheDir
            val tempFile = File(cacheDir, fileName)

            FileOutputStream(tempFile).use { output ->
                inputStream?.copyTo(output)
            }

            filePath = tempFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(
                requireContext(),
                "Error getting file path: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        } finally {
            inputStream?.close()
        }

        return filePath
    }

    private fun getFileNameFromUri(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    result = it.getString(it.getColumnIndexOrThrow("_display_name"))
                }
            }
        }
        return result ?: uri.lastPathSegment ?: "unknown"
    }

}
