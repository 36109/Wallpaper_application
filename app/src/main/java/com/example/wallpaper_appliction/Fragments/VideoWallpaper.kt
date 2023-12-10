package com.example.wallpaper_appliction.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wallp.VideoViewModel
import com.example.wallpaper_appliction.adapter.videoadapter
import com.example.wallpaper_appliction.databinding.VideowallpaperBinding
import com.example.wallpaper_appliction.utils.Resorces
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class VideoWallpaper : Fragment() {
    val binding: VideowallpaperBinding by lazy {
        VideowallpaperBinding.inflate(layoutInflater)
    }
    private val viewModel by viewModels<VideoViewModel>()
    lateinit var videoadapter: videoadapter
    private var dataFetched = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    private fun videorv() {
        videoadapter = videoadapter()
        binding.videorecy.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
            adapter = videoadapter

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        videorv()
        binding.videorecy.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = binding.videorecy.layoutManager as GridLayoutManager
                val totalItems = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                if (!viewModel.isLoading && !viewModel.isLastPage && lastVisibleItemPosition + 1 == totalItems) {
                    binding.bottomProgressBar.visibility = View.VISIBLE
                    viewModel.fetchdatafromapi()

                }
            }
        })

        lifecycleScope.launchWhenStarted {

            viewModel.video.collectLatest {
                when (it) {
                    is Resorces.success -> {
                        videoadapter.differ.submitList(it.data)
                        binding.bottomProgressBar.visibility = View.GONE
                        binding.centerProgressBar.visibility=View.INVISIBLE

                    }
                    is Resorces.failure -> {
                        Toast.makeText(requireContext(), "${it.msg}", Toast.LENGTH_SHORT).show()
                        binding.bottomProgressBar.visibility = View.GONE
                        binding.centerProgressBar.visibility=View.INVISIBLE
                    }
                    is Resorces.loading -> {
                        if (viewModel.currentPage > 1) {
                            binding.bottomProgressBar.visibility = View.VISIBLE

                        } else {
                            binding.centerProgressBar.visibility=View.VISIBLE
                        }
                    }
                    else -> Unit
                }
            }

        }
    }
}


