package com.tapbi.spark.emojimashup.ui.main

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import com.ironman.trueads.admob.interstital.InterstitialAdAdmob
import com.ironman.trueads.ironsource.InterstitialAdIronSource
import com.tapbi.spark.emojimashup.R
import com.tapbi.spark.emojimashup.data.model.Emoji
import com.tapbi.spark.emojimashup.data.model.StickerPackage
import com.tapbi.spark.emojimashup.databinding.ActivityMainBinding
import com.tapbi.spark.emojimashup.ui.base.BaseBindingActivity
import timber.log.Timber

class MainActivity : BaseBindingActivity<ActivityMainBinding, MainViewModel>() {

    private var isDispatchTouchEvent = true
    private var isFirstTime = true

    companion object {
        var currentImageDataVersionList = listOf<String>()
        var stickerPackageList = listOf<StickerPackage>()
        var emojiList = listOf<Emoji>()
        var pos1 = 0
        var pos2 = 0
    }

    fun isDispatchTouchEvent(time: Long) {
        isDispatchTouchEvent = false
        Handler(Looper.getMainLooper()).postDelayed({
            isDispatchTouchEvent = true
        }, time)
    }

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun getViewModel(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

    override fun setupView(savedInstanceState: Bundle?) {
        InterstitialAdAdmob.loadInterstitialAdmob(this, getString(R.string.admob_id_interstitial))
        InterstitialAdIronSource.initInterstitialIronSource(
            this,
            getString(R.string.ironsrc_app_key)
        )
        InterstitialAdIronSource.loadInterstitialIronSource(applicationContext)
        viewModel!!.getAllCurrentImageVersion()
        if (emojiList.isEmpty()) {
            viewModel!!.getEmojiList(this)
        }
    }

    override fun setupData() {
        if (isFirstTime) {
            isFirstTime = false
            viewModel!!.getAllStickerPackage(this)
        }

        viewModel!!.currentImageDataVersionList.observe(this) {
            currentImageDataVersionList = it
        }

        viewModel!!.isDataChange.observe(this) {
            it?.let {
                viewModel!!.getAllStickerPackage(this)
                viewModel!!.isDataChange.postValue(null)
            }
        }

        viewModel!!.stickerPackageList.observe(this) {
            stickerPackageList = it
        }

        viewModel!!.getNewStickerList.observe(this) {
            it?.let {
                Timber.e("giangld")
                mashupEmoji()
                viewModel!!.getNewStickerList.postValue(null)
            }
        }
    }

    fun mashupEmoji() {
        if (emojiList.isNotEmpty()) {
            pos1 = (Math.random() * emojiList.size).toInt()
            pos2 = (Math.random() * emojiList.size).toInt()
            if (pos2 == pos1) {
                pos2 = emojiList.size - 1 - pos1
            }
            viewModel!!.getMixedStickerList(this, emojiList[pos1], emojiList[pos2])
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return !isDispatchTouchEvent || super.dispatchTouchEvent(ev)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel!!.getAllCurrentImageVersion()
        outState.putBoolean("test", true)
        super.onSaveInstanceState(outState)
    }
    override fun onResume() {
        super.onResume()
        InterstitialAdIronSource.resumeInterstitialAdIronSource(this)
    }

    override fun onPause() {
        super.onPause()
        InterstitialAdIronSource.onPauseInterstitialAdIronSource(this)
    }
}