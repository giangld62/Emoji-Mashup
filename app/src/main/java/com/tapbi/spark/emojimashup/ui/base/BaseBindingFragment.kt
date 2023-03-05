package com.tapbi.spark.emojimashup.ui.base

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.ironman.trueads.admob.interstital.InterstitialAdAdmob
import com.ironman.trueads.admob.interstital.ShowInterstitialAdsAdmobListener
import com.ironman.trueads.ironsource.ShowInterstitialIronSourceListener
import com.tapbi.spark.emojimashup.App
import com.tapbi.spark.emojimashup.BuildConfig
import com.tapbi.spark.emojimashup.R
import com.tapbi.spark.emojimashup.common.Constant
import com.tapbi.spark.emojimashup.provider.WhitelistCheck
import com.tapbi.spark.emojimashup.ui.main.MainViewModel
import com.tapbi.spark.emojimashup.utils.getScreenHeight
import com.tapbi.spark.emojimashup.utils.safeDelay
import timber.log.Timber

abstract class BaseBindingFragment<B : ViewDataBinding, T : BaseViewModel> :
    BaseFragment() {
    lateinit var binding: B
    lateinit var viewModel: T
    lateinit var mainViewModel: MainViewModel
    protected abstract fun getViewModel(): Class<T>
    abstract val layoutId: Int
    private var toast: Toast? = null

    protected abstract fun initView()
    protected abstract fun initData()
    protected abstract fun observerData()
    protected abstract fun eventClick()

    protected abstract fun onCreatedView(view: View?, savedInstanceState: Bundle?)
    protected abstract fun onPermissionGranted()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        initView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(getViewModel())
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        onCreatedView(view, savedInstanceState)
        initData()
        observerData()
        eventClick()
    }

    protected fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT){
        if(toast == null){
            toast = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                createCustomToast(message,duration)
            } else{
                Toast.makeText(requireContext(),message,duration)
            }
            toast?.show()
        }
        else{
            toast?.cancel()
            toast = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                createCustomToast(message,duration)
            } else{
                Toast.makeText(requireContext(),message,duration)
            }
            safeDelay(600L){ toast?.show() }
        }
    }

    private fun createCustomToast(message: String, duration: Int = Toast.LENGTH_SHORT): Toast {
        val inflater = layoutInflater
        val layout: View = inflater.inflate(R.layout.layout_toast, null)
        // set a message
        val text = layout.findViewById<TextView>(R.id.tv_toast_message)
        text.text = message
        // Toast...
        val toast = Toast(activity)
        toast.setGravity(Gravity.BOTTOM, 0, ((getScreenHeight() * 0.09).toInt()))
        toast.duration = duration
        toast.view = layout
        return toast
    }

    fun addStickerPackToWhatsApp(context: Context, identifier: String, stickerPackName: String) {
        try {
            Timber.e("giangld addStickerPackToWhatsApp")
            //if neither WhatsApp Consumer or WhatsApp Business is installed, then tell user to install the apps.
            if (!WhitelistCheck.isWhatsAppConsumerAppInstalled(context.packageManager) && !WhitelistCheck.isWhatsAppSmbAppInstalled(
                    context.packageManager
                )
            ) {
                Timber.e("giangld addStickerPackToWhatsApp")
                showToast(getString(R.string.add_pack_fail_prompt_update_whatsapp),Toast.LENGTH_LONG)
                return
            }
            val stickerPackWhitelistedInWhatsAppConsumer: Boolean =
                WhitelistCheck.isStickerPackWhitelistedInWhatsAppConsumer(context, identifier)
            val stickerPackWhitelistedInWhatsAppSmb: Boolean =
                WhitelistCheck.isStickerPackWhitelistedInWhatsAppSmb(context, identifier)
            if (!stickerPackWhitelistedInWhatsAppConsumer && !stickerPackWhitelistedInWhatsAppSmb) {
                //ask users which app to add the pack to.
                Timber.e("giangld addStickerPackToWhatsApp")
                launchIntentToAddPackToChooser(context, identifier, stickerPackName)
            } else if (!stickerPackWhitelistedInWhatsAppConsumer) {
                Timber.e("giangld addStickerPackToWhatsApp")
                launchIntentToAddPackToSpecificPackage(
                    context,
                    identifier,
                    stickerPackName,
                    WhitelistCheck.CONSUMER_WHATSAPP_PACKAGE_NAME
                )
            } else if (!stickerPackWhitelistedInWhatsAppSmb) {
                Timber.e("giangld addStickerPackToWhatsApp")
                launchIntentToAddPackToSpecificPackage(
                    context,
                    identifier,
                    stickerPackName,
                    WhitelistCheck.SMB_WHATSAPP_PACKAGE_NAME
                )
            } else {
                showToast(getString(R.string.add_pack_fail_prompt_update_whatsapp),Toast.LENGTH_LONG)
            }
        } catch (e: java.lang.Exception) {
            Log.e(
                "TAG",
                "error adding sticker pack to WhatsApp",
                e
            )
            showToast(getString(R.string.add_pack_fail_prompt_update_whatsapp),Toast.LENGTH_LONG)
        }
    }

    private fun launchIntentToAddPackToChooser(
        context: Context,
        identifier: String,
        stickerPackName: String
    ) {
        val intent: Intent = createIntentToAddStickerPack(identifier, stickerPackName)
        try {
            ActivityCompat.startActivity(
                context,
                Intent.createChooser(intent, context.getString(R.string.add_to_whatsapp)),
                null
            )

        } catch (e: ActivityNotFoundException) {
            showToast(getString(R.string.add_pack_fail_prompt_update_whatsapp),Toast.LENGTH_LONG)
        }
    }

    private fun createIntentToAddStickerPack(identifier: String, stickerPackName: String): Intent {
        val intent = Intent()
        intent.action = "com.whatsapp.intent.action.ENABLE_STICKER_PACK"
        intent.putExtra(Constant.EXTRA_STICKER_PACK_ID, identifier)
        intent.putExtra(
            Constant.EXTRA_STICKER_PACK_AUTHORITY,
            BuildConfig.CONTENT_PROVIDER_AUTHORITY
        )
        intent.putExtra(Constant.EXTRA_STICKER_PACK_NAME, stickerPackName)

        return intent
    }

    private fun launchIntentToAddPackToSpecificPackage(
        context: Context,
        identifier: String,
        stickerPackName: String,
        whatsappPackageName: String
    ) {
        val intent = createIntentToAddStickerPack(identifier, stickerPackName)
        intent.setPackage(whatsappPackageName)
        try {
            ActivityCompat.startActivity(context, intent, null)
        } catch (e: ActivityNotFoundException) {
            showToast(getString(R.string.add_pack_fail_prompt_update_whatsapp),Toast.LENGTH_LONG)
        }
    }

    fun getNavigationBarSize(): Int {
        val appUsableSize: Point = getAppUsableScreenSize(requireContext())
        val realScreenSize: Point = getRealScreenSize(requireContext())
        var navHeight = 0

        // navigation bar on the right
        if (appUsableSize.x < realScreenSize.x) {
            navHeight = Point(realScreenSize.x - appUsableSize.x, appUsableSize.y).y
        } else {
            // navigation bar at the bottom
            if (appUsableSize.y < realScreenSize.y) {
                navHeight = getHeightNav(requireContext())
            }
        }
//        if (navHeight == App.widthScreen) {
//            navHeight = App.getInstance().mPrefs.getInt(Constant.HEIGHT_NAV_BAR, 0)
//        }
        Timber.e("giangld ${navHeight}")
        return navHeight
    }

    private fun getAppUsableScreenSize(context: Context): Point {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display: Display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size
    }

    private fun getRealScreenSize(context: Context): Point {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display: Display = windowManager.defaultDisplay
        val size = Point()
        display.getRealSize(size)
        return size
    }

    private fun getHeightNav(context: Context): Int {
        val resources = context.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }
    protected open fun showAdsFull() {
        InterstitialAdAdmob.showAdInterstitialAdmobIronSrcInterleaved(
            requireActivity(),
            object : ShowInterstitialAdsAdmobListener {
                override
                fun onLoadFailInterstitialAdsAdmob() {
                    Timber.e("onLoadFailInterstitialAdsAdmob")
                    nextAfterFullScreen()
                }
                override
                fun onInterstitialAdsAdmobClose() {
                    Timber.e("onInterstitialAdsAdmobClose")
                    nextAfterFullScreen()
                }
                override
                fun onInterstitialAdsNotShow() {
                    nextAfterFullScreen()
                    Timber.e("onInterstitialAdsNotShow")
                }
            },object : ShowInterstitialIronSourceListener {
                override
                fun onLoadFailInterstitialAdsIronSource() {
                    Timber.e("onLoadFailInterstitialAdsIronSource")
                    nextAfterFullScreen()
                }
                override
                fun onInterstitialAdsIronSourceClose() {
                    Timber.e("onInterstitialAdsIronSourceClose")
                    nextAfterFullScreen()
                }
                override
                fun onInterstitialAdsIronSourceNotShow() {
                    Timber.e("onInterstitialAdsIronSourceNotShow")
                    nextAfterFullScreen()
                }
                override
                fun onInterstitialAdsIronSourceLoaded() {
                    Timber.e("onInterstitialAdsIronSourceLoaded")
                }
                override
                fun onInterstitialAdsIronSourceShowFail() {
                    Timber.e("onInterstitialAdsIronSourceShowFail")
                    nextAfterFullScreen()
                }
            })
    }

    protected open fun nextAfterFullScreen() {}
}