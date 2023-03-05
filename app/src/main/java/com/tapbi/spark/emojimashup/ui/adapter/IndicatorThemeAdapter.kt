package com.tapbi.spark.emojimashup.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tapbi.spark.emojimashup.data.model.Sticker
import com.tapbi.spark.emojimashup.databinding.ItemIndicatorThemeBinding
import com.tapbi.spark.emojimashup.ui.custom.CenterRcv

class IndicatorThemeAdapter: RecyclerView.Adapter<IndicatorThemeAdapter.Holder>() {
    private var size: Int = 0
    private var rv: RecyclerView? = null

    fun submitSize(size: Int, rv: RecyclerView){
        this.size = size
        this.rv = rv
        notifyDataSetChanged()
    }

    private val centerRcv = CenterRcv()
    private var posSelected = -1
    fun setSelectedIndicator(pos: Int) {
        val posSelectOld = posSelected
        posSelected = pos
        rv?.findViewHolderForLayoutPosition(posSelectOld)?.let {
            (it as Holder).setLevelDot(0)
        }
        rv?.findViewHolderForLayoutPosition(posSelected)?.let {
            (it as Holder).setLevelDot(1)
        }
        centerRcv.scrollToCenter(rv?.layoutManager as LinearLayoutManager?, rv, posSelected)
    }

    fun deleteItem(pos: Int) {
        if (pos > -1) {
            notifyItemRemoved(pos)
            notifyItemRangeChanged(pos, itemCount)
        }
    }

    inner class Holder(val binding: ItemIndicatorThemeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pos: Int) {
            binding.imDot.setImageLevel(if (pos == posSelected) 1 else 0)
        }

        fun setLevelDot(level: Int) {
            binding.imDot.setImageLevel(level)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            ItemIndicatorThemeBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(holder.layoutPosition)
    }

    override fun getItemCount(): Int {
        return size
    }

}