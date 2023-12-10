package com.example.wallpaper_appliction.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wallpaper_appliction.adapter.hdwallpaperadapter
import com.example.wallpaper_appliction.databinding.HdwallpaperBinding
import com.example.wallpaper_appliction.utils.Resorces
import com.example.wallpaper_appliction.viewmodel.WallpaperViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class Hdwallpaper : Fragment() {
    private val binding: HdwallpaperBinding by lazy {
        HdwallpaperBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<WallpaperViewModel>()
    private lateinit var wallpaperAdapter: hdwallpaperadapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("HdwallpaperFragment", "onCreateView")
        return binding.root
    }

    private fun initializeWallpaperRecyclerView() {
        wallpaperAdapter = hdwallpaperadapter()
        binding.hdwallpaperrecy.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
            adapter = wallpaperAdapter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("HdwallpaperFragment", "onViewCreated")

        initializeWallpaperRecyclerView()
        binding.hdwallpaperrecy.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = binding.hdwallpaperrecy.layoutManager as GridLayoutManager
                val totalItems = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                if (!viewModel.isLoading && !viewModel.isLastPage && lastVisibleItemPosition + 1 == totalItems) {
                    binding.bottomProgressBar.visibility = View.VISIBLE
                    viewModel.loadNextPage()

                }
            }
        })


        lifecycleScope.launchWhenStarted {
            viewModel.wallpapers.collectLatest { result ->
                when (result) {
                    is Resorces.success -> {
                        wallpaperAdapter.differ.submitList(result.data)
                      //  binding.shimmerRecyclerView.hideShimmerAdapter()
                        binding.bottomProgressBar.visibility = View.GONE
                        binding.centerProgressBar.visibility=View.GONE

                    }
                    is Resorces.failure -> {
                        Toast.makeText(
                            requireContext(),
                            "${result.msg}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("ERROR", "onViewCreated: ${result.msg.toString()}")

                        binding.bottomProgressBar.visibility = View.GONE
                        binding.centerProgressBar.visibility=View.GONE
                    }
                    is Resorces.loading -> {
                        if (viewModel.currentPage > 1) {
                            binding.bottomProgressBar.visibility = View.VISIBLE
                            binding.centerProgressBar.visibility = View.VISIBLE
                        } else {
                            binding.centerProgressBar.visibility = View.VISIBLE
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}