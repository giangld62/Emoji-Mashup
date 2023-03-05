package com.tapbi.spark.emojimashup.ui.main.web

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import com.tapbi.spark.emojimashup.R
import com.tapbi.spark.emojimashup.databinding.FragmentWebviewBinding
import com.tapbi.spark.emojimashup.ui.base.BaseBindingFragment
import com.tapbi.spark.emojimashup.ui.main.MainViewModel
import com.tapbi.spark.emojimashup.utils.checkTime
import com.tapbi.spark.emojimashup.utils.gone


class WebViewFragment : BaseBindingFragment<FragmentWebviewBinding, MainViewModel>() {


    override fun getViewModel(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_webview

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
    }

    override fun onPermissionGranted() {
    }

    override fun initView() {
    }

    override fun eventClick() {
        binding.ivBack.setOnClickListener {
            if (checkTime(it))
                findNavController().popBackStack()
        }
    }

    override fun observerData() {
    }

    override fun initData() {
            loadLink(getString(R.string.link_gg))
            binding.tvTitle.text = getString(R.string.privacy_policy)
    }

    private fun loadLink(url: String) {
        binding.webView.apply {
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    if (isAdded) {
                        binding.pbLoad.gone()
                    }
                }
            }
            loadUrl(url)
        }
    }

}