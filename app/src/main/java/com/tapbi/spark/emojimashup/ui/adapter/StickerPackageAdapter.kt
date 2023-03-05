package com.tapbi.spark.emojimashup.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.tapbi.spark.emojimashup.data.model.StickerPackage
import com.tapbi.spark.emojimashup.databinding.ItemStickerPackageBinding
import com.tapbi.spark.emojimashup.utils.checkTime
import com.tapbi.spark.emojimashup.utils.pxFromDp
import com.tapbi.spark.emojimashup.utils.show

class StickerPackageAdapter(private val inter: IStickerPackageAdapter) :
    RecyclerView.Adapter<StickerPackageAdapter.ViewHolder>() {

    private val list = arrayListOf<StickerPackage>()
    private var isFirstTime = true

    fun submitList(stickerPackageList: List<StickerPackage>?){
        if (!stickerPackageList.isNullOrEmpty()) {
            if (isFirstTime) {
                list.addAll(stickerPackageList)
                notifyDataSetChanged()
                isFirstTime = false
            }
            else{
                for(i in stickerPackageList.indices){
                    if(stickerPackageList[i].isWhitelisted != list[i].isWhitelisted){
                        list[i].isWhitelisted = stickerPackageList[i].isWhitelisted
                        notifyItemChanged(i,list[i].isWhitelisted)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemStickerPackageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(private val binding: ItemStickerPackageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val stickerAdapter = StickerAdapter()
        fun bind(stickerPackage: StickerPackage) {
            binding.tvStickerPackageName.text = stickerPackage.name
            stickerAdapter.submitList(stickerPackage.stickers)
            binding.rvStickers.adapter = stickerAdapter
            binding.viewWhatsapp.show(!stickerPackage.isWhitelisted)
            binding.tvWhatsapp.show(!stickerPackage.isWhitelisted)

            val params = binding.tvTelegram.layoutParams as ConstraintLayout.LayoutParams
            params.setMargins(
                0,
                (if (stickerPackage.isWhitelisted) pxFromDp(
                    36F,
                    binding.root.context
                ) else pxFromDp(110F, binding.root.context)).toInt(),
                0,
                pxFromDp(38F, binding.root.context).toInt()
            )

            binding.viewWhatsapp.setOnClickListener {
                if (checkTime(it)) {
                    inter.onAddToWhatsAppClick(adapterPosition, stickerPackage)
                }
            }
            binding.viewTelegram.setOnClickListener {
                if (checkTime(it)) {
                    inter.onAddToTelegramClick(stickerPackage)
                }
            }
        }
    }

    interface IStickerPackageAdapter {
        fun onAddToWhatsAppClick(position: Int, stickerPackage: StickerPackage)
        fun onAddToTelegramClick(stickerPackage: StickerPackage)
    }
}