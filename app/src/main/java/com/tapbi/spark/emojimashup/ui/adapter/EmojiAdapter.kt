package com.tapbi.spark.emojimashup.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tapbi.spark.emojimashup.R
import com.tapbi.spark.emojimashup.data.model.Emoji
import com.tapbi.spark.emojimashup.databinding.ItemEmojiBinding
import timber.log.Timber


private val emojiDiff = object: DiffUtil.ItemCallback<Emoji>(){
    override fun areItemsTheSame(oldItem: Emoji, newItem: Emoji): Boolean {
        return oldItem.jsonFolder == newItem.jsonFolder
    }

    override fun areContentsTheSame(oldItem: Emoji, newItem: Emoji): Boolean {
        return oldItem == newItem
    }
}
class EmojiAdapter: ListAdapter<Emoji,EmojiAdapter.ViewHolder>(emojiDiff) {

    private var checkedPosition = -1

    fun setCheck(position: Int){
        this.checkedPosition = position
        notifyItemChanged(checkedPosition)
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
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemEmojiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(emoji: Emoji) {
            if(checkedPosition == adapterPosition){
                binding.ivEmoji.background =
                    ResourcesCompat.getDrawable(
                        binding.root.resources,
                        R.drawable.bg_white_40_percent_radius_6_ripple_blue,
                        null
                    )
            }
            else{
                binding.ivEmoji.background =
                    ResourcesCompat.getDrawable(
                        binding.root.resources,
                        R.drawable.bg_transparent_radius_6_ripple_blue,
                        null
                    )
            }
            Glide.with(binding.ivEmoji).load(emoji.link).into(binding.ivEmoji)
            binding.root.setOnClickListener {
                if(checkedPosition != adapterPosition){
                    onItemClickListener?.let {
                        it(adapterPosition)
                    }
                    binding.ivEmoji.background = ResourcesCompat.getDrawable(
                        binding.root.resources,
                        R.drawable.bg_white_40_percent_radius_6_ripple_blue,
                        null
                    )
                    notifyItemChanged(checkedPosition)
                    checkedPosition = adapterPosition
                }
            }
        }
    }

    private var onItemClickListener: ((Int) -> Unit)? = null

    fun setOnItemClickListener(listener: ((Int) -> Unit)){
        onItemClickListener = listener
    }
}