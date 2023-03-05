package com.tapbi.spark.emojimashup.ui.adapter

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.tapbi.spark.emojimashup.data.model.Sticker
import com.tapbi.spark.emojimashup.databinding.ItemFavoriteBinding

private val favoriteDiff = object: DiffUtil.ItemCallback<Sticker>(){
    override fun areItemsTheSame(oldItem: Sticker, newItem: Sticker): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Sticker, newItem: Sticker): Boolean {
        return oldItem == newItem
    }
}

class FavoriteAdapter : ListAdapter<Sticker,FavoriteAdapter.ViewHolder>(favoriteDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFavoriteBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    inner class ViewHolder(private val binding: ItemFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(sticker: Sticker) {
            Glide.with(binding.ivFavoriteSticker).load(sticker.stickerPathSaved).into(binding.ivFavoriteSticker)

            binding.ivFavoriteSticker.setOnClickListener {
                onItemClickListener?.let {
                    it(sticker.id)
                }
            }
        }
    }

    private var onItemClickListener: ((Int) -> Unit)? = null

    fun setOnItemClickListener(listener: ((Int) -> Unit)){
        onItemClickListener = listener
    }
}