package com.example.wallpaper_appliction.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.wallpaper_appliction.data.Photo
import com.example.wallpaper_appliction.data.retrofir_images
import com.example.wallpaper_appliction.utils.Resorces
import com.example.wallpaper_appliction.utils.retrofitobject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class WallpaperViewModel @Inject constructor() : ViewModel() {
    private val perPage = 80
    private val dataList = mutableListOf<Photo>()
            var currentPage = 1

    private val _wallpapers = MutableStateFlow<Resorces<List<retrofir_images>>>(Resorces.unspecified())
    val wallpapers: MutableStateFlow<Resorces<List<retrofir_images>>> = _wallpapers

    var isLoading=false
    var isLastPage=false


    init {
      _wallpapers.value=Resorces.loading()
        loadNextPage()
    }

    fun loadNextPage() {
        if (isLoading || isLastPage) {
            return
        }

        isLoading = true

        retrofitobject.apiinterface.getimages(
            "cXYXn7oN5LSyEugAOOKbUlit39HpEO2ylWQyGvW3O8QoNSsjrLobsrMI",
            currentPage,
            perPage
        ).enqueue(object : Callback<retrofir_images?> {
            override fun onResponse(
                call: Call<retrofir_images?>?,
                response: Response<retrofir_images?>?
            ) {
                isLoading = false

                if (response?.isSuccessful == true) {
                    val image = response.body()?.photos
                    Log.d("DATAT", "onResponse: $image")

                    if (image != null) {
                        dataList.addAll(image)
                        currentPage++
                        _wallpapers.apply { value = Resorces.success(mapPhotosToRetrofitImages(dataList)) }

                        if (image.isEmpty()) {
                            isLastPage = true
                        }
                    } else {
                        _wallpapers.apply { value = Resorces.failure("No Image Found") }
                    }
                } else {
                    _wallpapers.apply { value = Resorces.failure("Failed to fetch data") }
                }
            }

            override fun onFailure(call: Call<retrofir_images?>?, t: Throwable?) {
                isLoading = false
                Log.e("ViewModel", "onFailure: Data retrieval error - ${t?.message}")
                _wallpapers.apply { value = Resorces.failure(t?.message.toString()) }
            }
        })
    }





    private fun mapPhotosToRetrofitImages(photos: List<Photo>): List<retrofir_images> {
        // Create a list of retrofir_images by mapping each Photo to a retrofir_images
        return photos.map { photo ->
            retrofir_images(
                next_page = "", // Set the appropriate values from the Photo
                page = currentPage,
                per_page = perPage,
                photos = listOf(photo)
            )
        }
    }
}

