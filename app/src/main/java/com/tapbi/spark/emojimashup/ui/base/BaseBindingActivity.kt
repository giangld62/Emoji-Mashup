package com.tapbi.spark.emojimashup.ui.base

import android.os.Bundle
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.tapbi.spark.emojimashup.utils.getStatusBarHeight

abstract class BaseBindingActivity<B : ViewDataBinding?, VM : BaseViewModel?> :
    BaseActivity() {
    var binding: B? = null
    var viewModel: VM? = null
    abstract val layoutId: Int

    abstract fun getViewModel(): Class<VM>
    abstract fun setupView(savedInstanceState: Bundle?)
    abstract fun setupData()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        viewModel = ViewModelProvider(this).get(getViewModel())
        setPaddingTop()
        setupView(savedInstanceState)
        setupData()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun setPaddingTop(){
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        binding!!.root.setPadding(0, getStatusBarHeight(resources),0,0)
    }
}