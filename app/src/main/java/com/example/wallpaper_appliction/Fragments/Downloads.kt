package com.example.wallpaper_appliction.Fragments

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.wallpaper_appliction.adapter.downloadadapter
import com.example.wallpaper_appliction.databinding.DownloadsBinding
import java.io.File

class Downloads : Fragment() {
    val binding: DownloadsBinding by lazy {
        DownloadsBinding.inflate(layoutInflater)
    }
    lateinit var downloadadapter: downloadadapter
    lateinit var imagelist: ArrayList<String>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imagelist = arrayListOf()
        var allfiles: Array<File>


        val customDirectory = "wallpaper/images"
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val customPath = File(path, customDirectory)
        allfiles = customPath.listFiles()!!


        for (data in allfiles) {
            imagelist.add(data.toString())
        }
        setuprv()
    }

    private fun setuprv() {
        binding.recy.apply {
            downloadadapter = downloadadapter(requireContext(), imagelist)
            layoutManager =
                GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
            adapter = downloadadapter

        }
    }
}