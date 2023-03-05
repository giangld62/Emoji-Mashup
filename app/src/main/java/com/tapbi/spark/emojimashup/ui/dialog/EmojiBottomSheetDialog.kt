package com.tapbi.spark.emojimashup.ui.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tapbi.spark.emojimashup.R
import com.tapbi.spark.emojimashup.databinding.BottomSheetStickerBinding
import com.tapbi.spark.emojimashup.ui.adapter.EmojiAdapter
import timber.log.Timber

class EmojiBottomSheetDialog(private val inter: IEmojiBottomSheetDialog) : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetStickerBinding
    private var emojiAdapter: EmojiAdapter? = null
    lateinit var bottomSheetBehaviour: BottomSheetBehavior<*>
    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialogTheme
    }

    fun setAdapter(adapter: EmojiAdapter){
        emojiAdapter = adapter
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface: DialogInterface? ->
//            val bottomSheetDialog = dialogInterface as BottomSheetDialog
//            ViewHelper.setupFullHeight(bottomSheetDialog, requireContext())
            bottomSheetBehaviour = BottomSheetBehavior.from(
                binding.root.parent as View
            )
            bottomSheetBehaviour.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if(newState == BottomSheetBehavior.STATE_HIDDEN){
                        inter.onBottomSheetClose()
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {

                }
            })
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetStickerBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rvEmoji.adapter = emojiAdapter
        eventClick()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun eventClick() {
        binding.tvDone.setOnClickListener {
            dismiss()
            inter.onDoneBtnClick()
        }
    }

    interface IEmojiBottomSheetDialog{
        fun onDoneBtnClick()
        fun onBottomSheetClose()
    }
}