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
import com.example.wallpaper_appliction.adapter.searchadapter
import com.example.wallpaper_appliction.databinding.SearchBinding
import com.example.wallpaper_appliction.utils.Resorces
import com.example.wallpaper_appliction.viewmodel.searchviewmodel
import kotlinx.coroutines.flow.collectLatest

class Search : Fragment() {

    val binding: SearchBinding by lazy {
        SearchBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<searchviewmodel>()
    private lateinit var wallpaperAdapter: searchadapter

    private fun initializeWallpaperRecyclerView() {
        wallpaperAdapter = searchadapter()
        binding.hdwallpaperrecy.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
            adapter = wallpaperAdapter

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
        initializeWallpaperRecyclerView()
        val category = arguments?.getString("category")
        category?.let {
            viewModel.loadNextPage(category)
        }


        val query = arguments?.getString("query")
        binding.hdwallpaperrecy.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = binding.hdwallpaperrecy.layoutManager as GridLayoutManager
                val totalItems = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                if (!viewModel.isLoading && !viewModel.isLastPage && lastVisibleItemPosition + 1 == totalItems) {
                    binding.bottomProgressBar.visibility = View.VISIBLE
                    viewModel.loadNextPage(query.toString())

                }
            }
        })



        lifecycleScope.launchWhenStarted {
            viewModel.search.collectLatest { result ->
                when (result) {
                    is Resorces.success -> {
                        wallpaperAdapter.differ.submitList(result.data)
                        //  binding.shimmerRecyclerView.hideShimmerAdapter()
                        binding.bottomProgressBar.visibility = View.GONE
                        binding.centerProgressBar.visibility = View.GONE
                        Log.d("DATATA", "onViewCreated: ${result.data?.size}")

                    }
                    is Resorces.failure -> {

                        Log.d("ERROR", "onViewCreated:${result.msg} ")
                        Toast.makeText(requireContext(), "${result.msg}", Toast.LENGTH_SHORT).show()

                        binding.bottomProgressBar.visibility = View.GONE
                        binding.centerProgressBar.visibility = View.GONE
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

        viewModel.loadNextPage(query.toString())

    }


}
