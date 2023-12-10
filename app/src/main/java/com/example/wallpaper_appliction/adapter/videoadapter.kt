package com.example.wallpaper_appliction.adapter

import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.wallpaper_appliction.activities.Detailsvideo
import com.example.wallpaper_appliction.data.videowithimage
import com.example.wallpaper_appliction.databinding.VideoRvIttemBinding


class videoadapter() : RecyclerView.Adapter<videoadapter.viewholdervideo>() {


    class viewholdervideo(private val binding: VideoRvIttemBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(videoUrl: videowithimage) {
            Log.d("@@@@", "bind: ${videoUrl.videourl}")
            Glide.with(itemView)
                .asBitmap()
                .load(videoUrl.imageurl)
                .listener(object : RequestListener<Bitmap?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Bitmap?>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.shimmerRecyclerView.hideShimmerAdapter()
                        return false
                    }
                    override fun onResourceReady(
                        resource: Bitmap?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Bitmap?>?,
                        dataSource: com.bumptech.glide.load.DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.shimmerRecyclerView.hideShimmerAdapter()
                        return false
                    }
                })
                .into(binding.thumbnail)
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, Detailsvideo::class.java)
                intent.putExtra("videouri", videoUrl.videourl)
                itemView.context.startActivity(intent)
            }


        }

    }

    private val diffcallbacks = object : DiffUtil.ItemCallback<videowithimage>() {
        override fun areItemsTheSame(oldItem: videowithimage, newItem: videowithimage): Boolean {
            // Compare video URLs for uniqueness
            return oldItem.imageurl == newItem.imageurl
        }

        override fun areContentsTheSame(oldItem: videowithimage, newItem: videowithimage): Boolean {

            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffcallbacks)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholdervideo {
        val binding =
            VideoRvIttemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewholdervideo(binding)
    }


    override fun onBindViewHolder(holder: viewholdervideo, position: Int) {

        val data = differ.currentList.getOrNull(position)
        Log.d("URLS", "onBindViewHolder: $data")
        if (data != null) {
            holder.bind(data)
        }

    }


    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}