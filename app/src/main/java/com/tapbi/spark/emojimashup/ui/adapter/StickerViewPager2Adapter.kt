package com.tapbi.spark.emojimashup.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tapbi.spark.emojimashup.R
import com.tapbi.spark.emojimashup.data.model.Sticker
import com.tapbi.spark.emojimashup.data.model.StickerPart
import com.tapbi.spark.emojimashup.databinding.ItemStickerViewPager2Binding
import com.tapbi.spark.emojimashup.utils.*
import timber.log.Timber
import kotlin.math.roundToInt

class StickerViewPager2Adapter(private val inter: IOnFavoriteClick) :
    RecyclerView.Adapter<StickerViewPager2Adapter.ViewHolder>() {

    private val list = arrayListOf<Sticker>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemStickerViewPager2Binding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    fun submitList(stickerList: List<Sticker>?) {
        list.clear()
        if (stickerList != null) {
            list.addAll(stickerList)
        }
        notifyDataSetChanged()
    }

    fun setSticker(sticker: Sticker, position: Int){
        list[position] = sticker
        notifyItemChanged(position)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.sticker = list[position]
        holder.bind()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val binding: ItemStickerViewPager2Binding) :
        RecyclerView.ViewHolder(binding.root) {
        var sticker: Sticker? = null

        fun updateFavorite(id: Int?) {
            list[adapterPosition].isFavorite = !list[adapterPosition].isFavorite
            list[adapterPosition].id = id ?: 0
            notifyItemChanged(adapterPosition)
        }

        fun bind() {
            sticker?.let { sticker ->
                binding.ivFavorite.setOnClickListener {
                    if (checkTime(it,1000)) {
                        inter.onFavoriteClick(adapterPosition,sticker)
                    }
                }
                binding.ivFavorite.setImageResource(if (sticker.isFavorite) R.drawable.fav_icon2 else R.drawable.fav_icon1)

                val params = binding.viewBackground.layoutParams as ConstraintLayout.LayoutParams
                params.width = ((305.0 / 375) * getScreenWidth()).roundToInt()
                params.height = params.width
                val heightPx = if(sticker.beard == null ) (225 / 375.0) * getScreenWidth() else (200 / 375.0) * getScreenWidth()
                val widthPx = heightPx * sticker.face.ratio

                binding.ctlEmoji.rotation = sticker.face.rotationAngle
                val paramsFace = binding.ivFace.layoutParams as ConstraintLayout.LayoutParams
                paramsFace.width = widthPx.roundToInt()
                paramsFace.height = heightPx.roundToInt()
                Glide.with(binding.ivFace).load(sticker.face.link).into(binding.ivFace)

                setImageStickerPart(heightPx, widthPx, binding.ivHat, sticker.hat)
                setImageStickerPart(heightPx, widthPx, binding.ivGlasses, sticker.glasses)
                setImageStickerPart(heightPx, widthPx, binding.ivTear, sticker.tear)
                setImageStickerPart(heightPx, widthPx, binding.ivSweat, sticker.sweat)
                setImageStickerPart(heightPx, widthPx, binding.ivMouth, sticker.mouth)
                setImageStickerPart(heightPx, widthPx, binding.ivNosePart, sticker.nosePart)
                setImageStickerPart(heightPx, widthPx, binding.ivHand, sticker.hand)
                setImageStickerPart(heightPx, widthPx, binding.ivMouthPart, sticker.mouthPart)
                setImageStickerPart(heightPx, widthPx, binding.ivHeart, sticker.heart)
                setImageStickerPart(heightPx, widthPx, binding.ivNose, sticker.nose)
                setImageStickerPart(heightPx, widthPx, binding.ivHair, sticker.hair)
                setImageStickerPart(heightPx, widthPx, binding.ivBeard, sticker.beard)
                setImageStickerPart(heightPx, widthPx, binding.ivLeftEye, sticker.eyes.leftEye)
                setImageStickerPart(heightPx, widthPx, binding.ivRightEye, sticker.eyes.rightEye)


                setImageStickerPart(
                    heightPx,
                    widthPx,
                    binding.ivLeftEyebrow,
                    sticker.eyebrows?.leftEyebrow
                )
                setImageStickerPart(
                    heightPx,
                    widthPx,
                    binding.ivRightEyebrow,
                    sticker.eyebrows?.rightEyebrow
                )
            }
        }

        private fun setImageStickerPart(
            heightPx: Double,
            widthPx: Double,
            iv: ImageView,
            stickerPart: StickerPart?
        ) {
            if (stickerPart == null) {
                iv.gone()
            } else {
                iv.show()
                setSizeAndMargin(
                    widthPx = widthPx,
                    heightPx = heightPx,
                    iv = iv,
                    marginStart = stickerPart.marginStart,
                    marginTop = stickerPart.marginTop,
                    widthPercent = stickerPart.width,
                    heightPercent = stickerPart.height
                )
                Glide.with(iv).load(stickerPart.link).into(iv)
            }
        }

    }

    interface IOnFavoriteClick {
        fun onFavoriteClick(position: Int,sticker: Sticker)
    }
}