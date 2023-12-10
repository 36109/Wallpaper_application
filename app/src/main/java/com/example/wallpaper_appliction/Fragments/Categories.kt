package com.example.wallpaper_appliction.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.example.wallpaper_appliction.R
import com.example.wallpaper_appliction.databinding.CategoriesBinding
import com.example.wallpaper_appliction.viewmodel.searchviewmodel

class Categories:Fragment() {
    val binding:CategoriesBinding by lazy {
        CategoriesBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<searchviewmodel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bike.setOnClickListener{
           fragmentreplace("bike")
        }
        binding.cars.setOnClickListener{
            fragmentreplace("cars")
        }
        binding.animated.setOnClickListener{
            fragmentreplace("bike")
        }
        binding.bike.setOnClickListener{
            fragmentreplace("animated")
        }
        binding.city.setOnClickListener{
            fragmentreplace("city")
        }
        binding.black.setOnClickListener{
            fragmentreplace("black")
        }
        binding.music .setOnClickListener{
            fragmentreplace("music")
        }
        binding.nature.setOnClickListener{
            fragmentreplace("nature")
        }

    }
    fun fragmentreplace(category:String)
    {
        val searchFragment = Search()

        // Pass the parameter to the fragment
        val bundle = Bundle()
        bundle.putString("category", "$category")
        searchFragment.arguments = bundle

        // Perform the fragment transaction
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, searchFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}