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
import com.example.wallpaper_appliction.activities.Detailsimage
import com.example.wallpaper_appliction.data.retrofir_images
import com.example.wallpaper_appliction.databinding.HdwallpaperRvIttemBinding


class hdwallpaperadapter : RecyclerView.Adapter<hdwallpaperadapter.viewholdervideo>() {

    inner class viewholdervideo(val binding: HdwallpaperRvIttemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun databind(data: retrofir_images) {
            val url = data.photos.firstOrNull()?.src?.original

            binding.shimmerRecyclerView.showShimmerAdapter()

            Glide.with(itemView)
                .asBitmap()
                .load(url)
                .listener(object : RequestListener<Bitmap?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Bitmap?>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e("HdwallpaperAdapter", "Image loading failed", e)
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
                .into(binding.image)
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, Detailsimage::class.java)
                val photosList = data.photos.firstOrNull()?.src?.original
                intent.putExtra("imageuri", photosList)
                itemView.context.startActivity(intent)
            }
            Log.d("HdwallpaperAdapter", "onBindViewHolder: bound item at position $adapterPosition")
        }


    }
    val diffCallback = object : DiffUtil.ItemCallback<retrofir_images>() {
        override fun areItemsTheSame(oldItem: retrofir_images, newItem: retrofir_images): Boolean {
            return oldItem.photos.firstOrNull()?.url  == newItem.photos.firstOrNull()?.url
        }

        override fun areContentsTheSame(
            oldItem: retrofir_images,
            newItem: retrofir_images
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholdervideo {
        val binding = HdwallpaperRvIttemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return viewholdervideo(binding)
    }

    override fun onBindViewHolder(holder: viewholdervideo, position: Int) {

                val data = differ.currentList[position]
                holder.databind(data)

        }


    override fun getItemCount(): Int {
      return differ.currentList.size
    }
}





