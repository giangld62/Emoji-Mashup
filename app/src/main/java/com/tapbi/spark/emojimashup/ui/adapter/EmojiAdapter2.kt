package com.tapbi.spark.emojimashup.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tapbi.spark.emojimashup.data.model.Emoji
import com.tapbi.spark.emojimashup.databinding.ItemEmoji2Binding

class EmojiAdapter2(private val list: List<Emoji>) : RecyclerView.Adapter<EmojiAdapter2.ViewHolder>() {
//    private val list = arrayListOf<Emoji>()
//
//    fun submitList(emojiList: List<Emoji>){
//        list.clear()
//        list.addAll(emojiList)
//        notifyDataSetChanged()
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemEmoji2Binding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(private val binding: ItemEmoji2Binding): RecyclerView.ViewHolder(binding.root){
        fun bind(emoji: Emoji){
            Glide.with(binding.ivEmoji).load(emoji.link).into(binding.ivEmoji)
        }
    }
}