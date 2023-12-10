package com.example.wallpaper_appliction.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.wallpaper_appliction.data.PhotoXX
import com.example.wallpaper_appliction.data.searchdata
import com.example.wallpaper_appliction.utils.Resorces
import com.example.wallpaper_appliction.utils.retrofitobject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class searchviewmodel @Inject constructor() : ViewModel() {
    private val perPage = 80
    private val dataList = mutableListOf<PhotoXX>()
            var currentPage = 1

    private val _search = MutableStateFlow<Resorces<List<searchdata>>>(Resorces.unspecified())
    val search: MutableStateFlow<Resorces<List<searchdata>>> = _search

    var isLoading=false
    var isLastPage=false


    init {
      _search.value=Resorces.loading()

    }

    fun loadNextPage(query:String) {
        if (isLoading || isLastPage) {
            return
        }

        isLoading = true

        retrofitobject.apiinterface.getsearchdata(
            "cXYXn7oN5LSyEugAOOKbUlit39HpEO2ylWQyGvW3O8QoNSsjrLobsrMI",
            currentPage,
            perPage,
            query,
            1000

        ).enqueue(object : Callback<searchdata> {
            override fun onResponse(
                call: Call<searchdata>?,
                response: Response<searchdata>?
            ) {
                isLoading = false

                if (response?.isSuccessful == true) {
                    val image = response.body()?.photos
                    Log.d("SEARCH", "onResponse: $image")

                    if (image != null) {
                        dataList.addAll(image)
                        currentPage++
                        _search.apply { value = Resorces.success(mapPhotosToRetrofitImages(dataList)) }

                        if (image.isEmpty()) {
                            isLastPage = true
                        }
                    } else {
                        _search.apply { value = Resorces.failure("No Image Found") }
                    }
                } else {
                    _search.apply { value = Resorces.failure("Failed to fetch data") }
                }
            }

            override fun onFailure(call: Call<searchdata>?, t: Throwable?) {
                isLoading = false
                Log.e("ViewModel", "onFailure: Data retrieval error - ${t?.message}")
                _search.apply { value = Resorces.failure(t?.message.toString()) }
            }
        })
    }





    private fun mapPhotosToRetrofitImages(photos: List<PhotoXX>): List<searchdata> {
        // Create a list of searchdata by mapping each Photo to a searchdata
        return photos.map { PhotoXX ->
            searchdata(
                next_page = "", // Set the appropriate values from the Photo
                page = currentPage,
                per_page = perPage,
                photos = listOf(PhotoXX),
                total_results = 10000  // Update to the correct total_results value
            )
        }
    }


}

