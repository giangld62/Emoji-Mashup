package com.tapbi.spark.emojimashup.ui.main.home

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import com.tapbi.spark.emojimashup.BuildConfig
import com.tapbi.spark.emojimashup.R
import com.tapbi.spark.emojimashup.common.Constant
import com.tapbi.spark.emojimashup.databinding.DrawerItemBinding
import com.tapbi.spark.emojimashup.databinding.FragmentHomeBinding
import com.tapbi.spark.emojimashup.ui.base.BaseBindingFragment
import com.tapbi.spark.emojimashup.ui.dialog.RateAppDialogFragment
import com.tapbi.spark.emojimashup.utils.checkTime
import com.tapbi.spark.emojimashup.utils.getScreenWidth
import com.tapbi.spark.emojimashup.utils.gone
import com.tapbi.spark.emojimashup.utils.sendFeedback
import timber.log.Timber
import kotlin.math.roundToInt

class HomeFragment : BaseBindingFragment<FragmentHomeBinding, HomeViewModel>(),
    RateAppDialogFragment.Listener {
    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_home

    private val mBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (binding.drawer.isDrawerOpen(Gravity.END)) {
                binding.drawer.closeDrawers()
            } else {
                requireActivity().finish()
            }
        }
    }

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            mBackPressedCallback
        )
    }

    override fun onResume() {
        super.onResume()
        if (sharedPreferenceHelper.getBoolean(Constant.IS_RATE_APP, false)) {
            binding.navMain.menu.removeItem(R.id.rate_app)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun actionWhenHoverBtn(view: View, tv: TextView, icon1: Int, icon2: Int) {
        view.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    tv.setTextColor(ResourcesCompat.getColor(resources, R.color.color_FF6F4E, null))
                    tv.setCompoundDrawablesWithIntrinsicBounds(
                        ResourcesCompat.getDrawable(
                            resources,
                            icon1,
                            null
                        ), null, null, null
                    )
                    view.background =
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.bg_solid_white_radius_110,
                            null
                        )
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    tv.setTextColor(Color.WHITE)
                    view.background =
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.bg_stroke_white_radius_110,
                            null
                        )
                    tv.setCompoundDrawablesWithIntrinsicBounds(
                        ResourcesCompat.getDrawable(
                            resources,
                            icon2,
                            null
                        ), null, null, null
                    )
                }
            }
            return@setOnTouchListener false
        }
    }

    override fun onPermissionGranted() {

    }

    override fun initView() {
        val image12Params = binding.viewImg12.layoutParams as ConstraintLayout.LayoutParams
        image12Params.width = getScreenWidth()
        image12Params.height = ((388.78 / 375.0) * image12Params.width).roundToInt()

        setUpDrawerItem(
            R.id.share_app,
            getString(R.string.share_app),
            R.drawable.share_app_drawer_icon
        )
        setUpDrawerItem(
            R.id.rate_app,
            getString(R.string.rate_app),
            R.drawable.rate_app_drawer_icon
        )
        setUpDrawerItem(
            R.id.feedback,
            getString(R.string.feedback),
            R.drawable.feedback_drawer_icon
        )
        setUpDrawerItem(
            R.id.privacy_policy,
            getString(R.string.privacy_policy),
            R.drawable.privacy_policy_drawer_icon
        )
        setUpDrawerItem(
            R.id.app_version,
            getString(R.string.version) + " ${BuildConfig.VERSION_NAME}",
            null
        )

        binding.navMain.menu.getItem(5).isEnabled = false
    }

    override fun eventClick() {
        binding.viewFavoriteList.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToFavoriteFragment())
        }

        binding.viewStickers.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToStickersFragment())
        }

        binding.ivDrawerMenu.setOnClickListener {
            binding.drawer.openDrawer(Gravity.END)
        }

        binding.viewRemixEmoji.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToRemixEmojiFragment())
        }

        binding.navMain.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment -> {
                    binding.drawer.closeDrawer(Gravity.END)
                }

                R.id.share_app -> {
                    if (checkTime(1000)) {
                        try {

                            val shareIntent = Intent(Intent.ACTION_SEND)
                            shareIntent.type = "text/plain"
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                            val shareMessage =
                                "${Constant.SHARE_APP_LINK}${BuildConfig.APPLICATION_ID}\n\n"
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                            startActivity(
                                Intent.createChooser(
                                    shareIntent,
                                    getString(R.string.choose_one)
                                )
                            )
                        } catch (e: Exception) {
                            showToast(getString(R.string.error))
                        }
                    }
                }

                R.id.feedback -> {
                    if (checkTime()) {
                        sendFeedback(
                            requireContext(),
                            getString(R.string.email_feedback),
                            "App Report (${requireContext().packageName})",
                            ""
                        )
                    }
                }

                R.id.privacy_policy -> {
                    if (checkTime(it.actionView)) {
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToWebViewFragment())
                    }
                }

                R.id.rate_app -> {
                    if (checkTime(it.actionView)) {
                        val dialog = RateAppDialogFragment()
                        dialog.setListener(this)
                        dialog.show(childFragmentManager, null)
                    }
                }


            }
            return@setNavigationItemSelectedListener true
        }

        actionWhenHoverBtn(
            binding.viewFavoriteList,
            binding.tvFav,
            R.drawable.favorite_btn_icon2,
            R.drawable.favorite_btn_icon1
        )
        actionWhenHoverBtn(
            binding.viewRemixEmoji,
            binding.tvRemix,
            R.drawable.remix_btn_icon2,
            R.drawable.remix_btn_icon1
        )
        actionWhenHoverBtn(
            binding.viewStickers,
            binding.tvStickers,
            R.drawable.stickers_btn_icon2,
            R.drawable.stickers_btn_icon1
        )
    }

    override fun observerData() {
    }

    override fun initData() {

    }

    private fun setUpDrawerItem(menuItem: Int, title: String, icon: Int?) {
        val itemView = binding.navMain.menu.findItem(menuItem).actionView
        val binding = DrawerItemBinding.bind(itemView)
        binding.tvDrawerItem.text = title
        if (icon != null) {
            binding.ivIcon.setImageResource(icon)
        } else {
            binding.ivArrow.gone()
            binding.ivIcon.gone()
        }
    }

    override fun onRateNowClick() {
        sharedPreferenceHelper.storeBoolean(Constant.IS_RATE_APP, true)
        binding.navMain.menu.removeItem(R.id.rate_app)
    }

}