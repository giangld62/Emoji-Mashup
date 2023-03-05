package com.tapbi.spark.emojimashup.ui.dialog

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.tapbi.spark.emojimashup.BuildConfig
import com.tapbi.spark.emojimashup.R
import com.tapbi.spark.emojimashup.common.Constant
import com.tapbi.spark.emojimashup.databinding.DialogRateAppBinding
import com.tapbi.spark.emojimashup.ui.base.BaseBindingDialogFragment
import com.tapbi.spark.emojimashup.utils.checkTime

class RateAppDialogFragment : BaseBindingDialogFragment<DialogRateAppBinding>() {
    private var isRated = false
    private var listener: Listener? = null

    override val layoutId: Int
        get() = R.layout.dialog_rate_app

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        initView()
        evenClick()
    }

    private fun initView() {

    }

    private fun evenClick() {

        binding.simpleRatingBar.setOnRatingChangeListener { _, _ ->
            isRated = true
        }

        binding.btnRateNow.setOnClickListener {
            if (checkTime(it)) {
                if (isRated && binding.simpleRatingBar.rating > 0) {
                    listener?.onRateNowClick()
                    if (binding.simpleRatingBar.rating > 1)
                        openMarket()
                    else
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.thanks_for_your_rating),
                            Toast.LENGTH_SHORT
                        ).show()
                    dismiss()
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.please_rate_5_stars),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            if (checkTime(it)) {
                dismiss()
            }
        }
    }

    private fun openMarket() {
        try {
            context?.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)
                )
            )
        } catch (e: ActivityNotFoundException) {
            context?.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(
                        Constant.SHARE_APP_LINK + BuildConfig.APPLICATION_ID
                    )
                )
            )
        }
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    interface Listener {
        fun onRateNowClick()
    }

}