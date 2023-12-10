package com.example.wallpaper_appliction.helperclass

import android.app.ProgressDialog
import android.content.Context
import android.media.MediaScannerConnection
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.example.wallpaper_appliction.utils.DownloadProgressListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

class downloadimage(
    private val context: Context,
    private val progressListener: DownloadProgressListener,
    private val callback: DownloadCallback
) : AsyncTask<String?, Int, File>() {
    private var progressDialog: ProgressDialog? = null

    init {
        progressDialog = ProgressDialog(context)
        progressDialog?.apply {
            setTitle("Downloading Image")
            setMessage("Please wait...")
            setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            isIndeterminate = false
            max = 100
            setCancelable(false)
        }
    }

    override fun doInBackground(vararg params: String?): File? {
        if (params.isEmpty() || params[0].isNullOrBlank()) {
            return null
        }

        try {
            val url = URL(params[0])
            val connection = url.openConnection()
            val totalSize = connection.contentLength
            val bufferSize = 8192
            var bytesRead: Int
            var downloadedSize = 0

            val customDirectory = "wallpaper/images"
            val path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val customPath = File(path, customDirectory)
            if (!customPath.exists()) {
                customPath.mkdirs()
            }
            val imageFile = File(customPath, System.currentTimeMillis().toString() + ".png")
            var out: FileOutputStream? = null

            try {
                out = FileOutputStream(imageFile)
                val data = ByteArray(bufferSize)

                while (true) {
                    bytesRead = connection.getInputStream().read(data, 0, bufferSize)
                    if (bytesRead <= 0) {
                        break
                    }
                    out.write(data, 0, bytesRead)
                    downloadedSize += bytesRead
                    out.flush()

                    val progress = (downloadedSize * 100 / totalSize).toInt()
                    publishProgress(progress)
                }
                out.close()

                MediaScannerConnection.scanFile(
                    context, arrayOf(imageFile.absolutePath), null
                ) { _, _ ->
                    // File scanned
                }

                return imageFile // Return the downloaded image file
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("DownloadImage", "Error: ${e.message}")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("DownloadImage", "Error: ${e.message}")
        }
        return null // Return null to indicate download failure
    }

    override fun onPostExecute(result: File?) {
        super.onPostExecute(result)
        progressDialog?.dismiss()


        if (result != null && result.exists() && result.length() > 0) {
            callback.onDownloadComplete(result)
            Toast.makeText(context, "Image downloaded", Toast.LENGTH_SHORT).show()
        } else {
            callback.onDownloadComplete(null)
            Toast.makeText(context, "Failed to download image", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onPreExecute() {
        super.onPreExecute()
        progressDialog?.show()
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
        if (values.isNotEmpty()) {
            val progress = values[0] ?: 0
            progressDialog?.progress = progress
            progressListener.onProgressUpdate(progress)
        }
    }
    interface DownloadCallback {
        fun onDownloadComplete(result: File?)
    }
}

interface DownloadProgressListener {
    fun onProgressUpdate(progress: Int)
}
