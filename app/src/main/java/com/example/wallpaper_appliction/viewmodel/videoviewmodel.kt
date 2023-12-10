package com.example.wallp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wallpaper_appliction.data.VideoX
import com.example.wallpaper_appliction.data.retrofit_videos
import com.example.wallpaper_appliction.data.videowithimage
import com.example.wallpaper_appliction.utils.Resorces
import com.example.wallpaper_appliction.utils.retrofitobject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor() : ViewModel() {
    private val perPage = 80
    private val videolist = mutableListOf<VideoX>()
     var currentPage = 1
    private val _video= MutableStateFlow<Resorces<List<videowithimage>>>(Resorces.unspecified())
    val video: MutableStateFlow<Resorces<List<videowithimage>>> = _video

     var isLoading = false
     var isLastPage = false

    init {
        video.value=Resorces.loading()
        fetchdatafromapi()
    }

    fun fetchdatafromapi() {
        if (isLoading || isLastPage) {
            return
        }
        isLoading = true

        viewModelScope.launch {
            _video.emit(Resorces.loading())
        }

        retrofitobject.apiinterface.getvideos(
            "cXYXn7oN5LSyEugAOOKbUlit39HpEO2ylWQyGvW3O8QoNSsjrLobsrMI",
            currentPage,
            perPage,
            30,
            3
        ).enqueue(object : Callback<retrofit_videos?> {
            override fun onResponse(
                call: Call<retrofit_videos?>?,
                response: Response<retrofit_videos?>?
            ) {
                isLoading=false
                if (response?.isSuccessful ==true) {
                    val videos = response.body()?.videos
                    if (videos != null) {
                        videolist.addAll(videos)
                        val videoandimage = videolist.mapNotNull { video ->
                            val hdvideolink = video.video_files.firstOrNull { it.quality == "hd" }?.link
                            val imageurl = video.image

                            if (hdvideolink != null) {
                                videowithimage(hdvideolink, imageurl)
                            } else {
                                null
                            }
                        }
                        currentPage++
                        video.apply { value = Resorces.success(videoandimage) }
                        if (videos.size < perPage) {
                            isLastPage = true
                        }

                    } else {
                        viewModelScope.launch {
                            _video.emit(Resorces.failure("No Image Found"))
                        }
                    }
                }
            }

            override fun onFailure(call: Call<retrofit_videos?>?, t: Throwable?) {
                viewModelScope.launch {
                    isLoading=false
                    _video.emit(Resorces.failure(t?.localizedMessage.toString()))
                }
            }
        })
    }
}
