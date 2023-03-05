package com.tapbi.spark.emojimashup.ui.splash

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import com.ironman.trueads.admob.ControlAdsAdmob
import com.ironman.trueads.admob.open.AppOpenAdAdmob
import com.ironman.trueads.admob.open.ShowOpenAdsAdmobListener
import com.tapbi.spark.emojimashup.App
import com.tapbi.spark.emojimashup.R
import com.tapbi.spark.emojimashup.common.Constant
import com.tapbi.spark.emojimashup.data.model.Emoji
import com.tapbi.spark.emojimashup.databinding.ActivitySplashBinding
import com.tapbi.spark.emojimashup.ui.base.BaseBindingActivity
import com.tapbi.spark.emojimashup.ui.main.MainActivity
import com.tapbi.spark.emojimashup.ui.main.MainActivity.Companion.emojiList
import com.tapbi.spark.emojimashup.ui.main.MainViewModel
import com.tapbi.spark.emojimashup.utils.getScreenWidth
import com.tapbi.spark.emojimashup.utils.getStatusBarHeight
import com.tapbi.spark.emojimashup.utils.safeDelay
import timber.log.Timber
import kotlin.math.abs
import kotlin.math.roundToInt

class SplashActivity : BaseBindingActivity<ActivitySplashBinding, SplashViewModel>() {
    private var canGoMain = false
    private var startTime = System.currentTimeMillis()
    private var handlerGoMain = Handler(Looper.getMainLooper())
    private val runnableGoMain = Runnable { this.goMain() }
    override val layoutId: Int
        get() = R.layout.activity_splash

    override fun getViewModel(): Class<SplashViewModel> {
        return SplashViewModel::class.java
    }

    override fun setupView(savedInstanceState: Bundle?) {
        ControlAdsAdmob.initAds(this)
        setUpSplash()
        loadAds()
    }

    private fun setUpSplash() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            window.insetsController?.hide(WindowInsets.Type.statusBars())
//        } else {
//            window.setFlags(
//                WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN
//            )
//        }

        val image2Params = binding!!.viewImg2.layoutParams as ConstraintLayout.LayoutParams
        image2Params.width = ((110.0 / 375.0) * getScreenWidth() * 0.85).roundToInt()
        image2Params.height = ((320.0 / 146.0) * image2Params.width).roundToInt()

        val image12Params = binding!!.viewImg12.layoutParams as ConstraintLayout.LayoutParams
        image12Params.width = getScreenWidth()
        image12Params.height = ((388.78 / 375.0) * image12Params.width).roundToInt()
    }

    override fun setupData() {
    }
    private fun loadAds() {
        AppOpenAdAdmob.getInstance(application).loadAndShowOpenAdsAdmob(this,
            getString(R.string.admob_id_ads_open), true, object : ShowOpenAdsAdmobListener {
                override fun onLoadedAdsOpenApp() {}
                override fun onLoadFailAdsOpenApp() {
                    checkGoToMain()
                }

                override fun onShowAdsOpenAppDismissed() {
                    checkGoToMain()

                }

                override fun onAdsOpenLoadedButNotShow() {
                    checkGoToMain()
                }
            })
    }
    private fun checkGoToMain() {
        Timber.e("checkGoToMain" + lifecycle.currentState.name)
        canGoMain = true
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            nextActivity()
        }
    }
    private fun nextActivity() {
        val deltaTime = abs(System.currentTimeMillis() - startTime)
        if (deltaTime >= Constant.SPLASH_TIME_DELAY) {
            Timber.e("nextActivity$deltaTime")
            handlerGoMain.postDelayed(runnableGoMain, 200)
        } else {
            Timber.e("nextActivity 2222 $deltaTime")
            handlerGoMain.postDelayed(runnableGoMain, Constant.SPLASH_TIME_DELAY-deltaTime)
        }
    }
    private fun goMain() {
        Timber.e("goMain  $this$canGoMain")
        if (canGoMain) {
            canGoMain = false
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    override fun onBackPressed() {
        AppOpenAdAdmob.getInstance(App.instance!!).isShowAdsBack=false
        if (!AppOpenAdAdmob.getInstance(App.instance!!).checkOpenAdsShowing()) {
            super.onBackPressed()
        }
    }

}