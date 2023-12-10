package com.example.wallpaper_appliction.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wallpaper_appliction.databinding.DownloadRvItemBinding
import java.io.File

class downloadadapter(val context: Context, val imagelist: ArrayList<String>) :
    RecyclerView.Adapter<downloadadapter.viewholder>() {
    inner class viewholder(val binding: DownloadRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: String) {
            Glide.with(itemView).load(File(data)).into(binding.image)

        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        return viewholder(
            DownloadRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }


    override fun onBindViewHolder(holder: viewholder, position: Int) {
        val data = imagelist[position]
        holder.bind(data)

    }


    override fun getItemCount(): Int {
        return imagelist.size
    }
}