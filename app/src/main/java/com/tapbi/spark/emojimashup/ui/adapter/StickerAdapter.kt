package com.tapbi.spark.emojimashup.ui.adapter

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.tapbi.spark.emojimashup.data.model.Sticker
import com.tapbi.spark.emojimashup.databinding.ItemEmojiBinding
import timber.log.Timber

class StickerAdapter : RecyclerView.Adapter<StickerAdapter.ViewHolder>() {
    private val stickerList = arrayListOf<Sticker>()

    fun submitList(list: List<Sticker>?) {
        stickerList.clear()
        if (!list.isNullOrEmpty()) {
            stickerList.addAll(list)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemEmojiBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(stickerList[position])
    }

    override fun getItemCount(): Int {
        return stickerList.size
    }

    class ViewHolder(private val binding: ItemEmojiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(sticker: Sticker) {
            Glide.with(binding.ivEmoji).load(sticker.stickerPathSaved).into(binding.ivEmoji)
        }
    }
}