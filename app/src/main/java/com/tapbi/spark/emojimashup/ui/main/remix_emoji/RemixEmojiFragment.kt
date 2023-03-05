package com.tapbi.spark.emojimashup.ui.main.remix_emoji

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.activity.OnBackPressedCallback
import androidx.core.view.get
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.tapbi.spark.emojimashup.R
import com.tapbi.spark.emojimashup.common.Constant
import com.tapbi.spark.emojimashup.data.model.Emoji
import com.tapbi.spark.emojimashup.data.model.Sticker
import com.tapbi.spark.emojimashup.databinding.FragmentRemixEmojiBinding
import com.tapbi.spark.emojimashup.ui.adapter.EmojiAdapter
import com.tapbi.spark.emojimashup.ui.adapter.EmojiAdapter2
import com.tapbi.spark.emojimashup.ui.adapter.IndicatorThemeAdapter
import com.tapbi.spark.emojimashup.ui.adapter.StickerViewPager2Adapter
import com.tapbi.spark.emojimashup.ui.base.BaseBindingFragment
import com.tapbi.spark.emojimashup.ui.dialog.EmojiBottomSheetDialog
import com.tapbi.spark.emojimashup.ui.main.MainActivity
import com.tapbi.spark.emojimashup.ui.main.MainActivity.Companion.emojiList
import com.tapbi.spark.emojimashup.ui.main.MainActivity.Companion.pos1
import com.tapbi.spark.emojimashup.ui.main.MainActivity.Companion.pos2
import com.tapbi.spark.emojimashup.utils.*
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import kotlin.math.abs


class RemixEmojiFragment : BaseBindingFragment<FragmentRemixEmojiBinding, RemixEmojiViewModel>(),
    StickerViewPager2Adapter.IOnFavoriteClick {

    //region viewPager variable
    private var counterPageScroll = 0
    private var isLastPageSwiped = false
    private lateinit var viewPagerAdapter: StickerViewPager2Adapter
    private var currentPagerPosition = 0
    private lateinit var indicatorAdapter: IndicatorThemeAdapter
    //endregion

    //region emoji variable
    private lateinit var emojiAdapter1: EmojiAdapter
    private lateinit var emojiAdapter2: EmojiAdapter
    private var emoji1: Emoji? = null
    private var emoji2: Emoji? = null
    private var emojiAdapter3: EmojiAdapter2? = null
    private var emojiAdapter4: EmojiAdapter2? = null
    private lateinit var layoutManager1: LinearLayoutManager
    private lateinit var layoutManager2: LinearLayoutManager
    private lateinit var smoothScroller1: SmoothScroller
    private lateinit var smoothScroller2: SmoothScroller
    private var newCheckedPosition1 = 0
    private var newCheckedPosition2 = 0
    private var oldCheckedPosition1 = 0
    private var oldCheckedPosition2 = 0
    private lateinit var bottomSheet1: EmojiBottomSheetDialog
    private lateinit var bottomSheet2: EmojiBottomSheetDialog
    //endregion

    //region sticker variable
    private val stickerList = arrayListOf<Sticker>()
    private val stickerTempList = arrayListOf<Sticker>()
    private var isFavoriteClicked = false
    private val navArgs by navArgs<RemixEmojiFragmentArgs>()
    private var zoomInAnim: ScaleAnimation? = null
    private var sticker: Sticker? = null
    //endregion

    //region other
    private var isRandomClick = false
    var isRevertData = false

    private val mBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            findNavController().popBackStack()
            if (!navArgs.fromFavoriteFragment) {
                mainViewModel.getNewStickerList.postValue(Unit)
            }
        }
    }
    //endregion

    override fun getViewModel(): Class<RemixEmojiViewModel> {
        return RemixEmojiViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_remix_emoji

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            isRevertData = it.getBoolean(Constant.IS_REVERT_DATA, false)
            if (!navArgs.fromFavoriteFragment) {
                emoji1 = Gson().fromJson(it.getString(Constant.EMOJI1, null), Emoji::class.java)
                emoji2 = Gson().fromJson(it.getString(Constant.EMOJI2, null), Emoji::class.java)
                currentPagerPosition = it.getInt(Constant.CURRENT_PAGE_POSITION, 0)
                oldCheckedPosition1 = it.getInt(Constant.FIRST_CHECKED_POSITION)
                newCheckedPosition1 = oldCheckedPosition1
                oldCheckedPosition2 = it.getInt(Constant.SECOND_CHECKED_POSITION)
                newCheckedPosition2 = oldCheckedPosition2
                if (emoji1 != null && emoji2 != null) {
                    mainViewModel.getMixedStickerList(requireContext(), emoji1!!, emoji2!!)
                    setUpDataForRcvChooseEmoji()
                    emojiAdapter1.setCheck(oldCheckedPosition1)
                    emojiAdapter2.setCheck(oldCheckedPosition2)
                }
            } else {
                sticker = Gson().fromJson(it.getString(Constant.STICKER, null), Sticker::class.java)
                Timber.e("giangld ${sticker}")
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            mBackPressedCallback
        )
    }

    override fun initView() {
        emojiAdapter1 = EmojiAdapter()
        emojiAdapter2 = EmojiAdapter()

        binding.headerMain.tvTitle.text = getString(R.string.remix_emoji)
        initRcv()
        initBottomSheetDialog()
        initViewpager()
    }

    private fun initBottomSheetDialog() {
        bottomSheet1 =
            EmojiBottomSheetDialog(object : EmojiBottomSheetDialog.IEmojiBottomSheetDialog {
                override fun onDoneBtnClick() {
                    if (oldCheckedPosition1 != newCheckedPosition1) {
                        isRandomClick = false
                        emoji1 = emojiList[newCheckedPosition1]
                        mainViewModel.getMixedStickerList(requireContext(), emoji1!!, emoji2!!)
                        setUpDataForRcvChooseEmoji()
                        oldCheckedPosition1 = newCheckedPosition1
                    }
                }

                override fun onBottomSheetClose() {
                    if (oldCheckedPosition1 != newCheckedPosition1) {
                        emojiAdapter1.setCheck(oldCheckedPosition1)
                        newCheckedPosition1 = oldCheckedPosition1
                    }
                }
            })
        bottomSheet1.setAdapter(emojiAdapter1)

        bottomSheet2 =
            EmojiBottomSheetDialog(object : EmojiBottomSheetDialog.IEmojiBottomSheetDialog {
                override fun onDoneBtnClick() {
                    if (oldCheckedPosition2 != newCheckedPosition2) {
                        isRandomClick = false
                        emoji2 = emojiList[newCheckedPosition2]
                        mainViewModel.getMixedStickerList(requireContext(), emoji1!!, emoji2!!)
                        setUpDataForRcvChooseEmoji()
                        oldCheckedPosition2 = newCheckedPosition2
                    }
                }

                override fun onBottomSheetClose() {
                    if (oldCheckedPosition2 != newCheckedPosition2) {
                        emojiAdapter2.setCheck(oldCheckedPosition2)
                        newCheckedPosition2 = oldCheckedPosition2
                    }
                }
            })
        bottomSheet2.setAdapter(emojiAdapter2)
    }

    private fun initViewpager() {
        indicatorAdapter = IndicatorThemeAdapter()
        binding.rvIndicator.adapter = indicatorAdapter

//        val paddingHorizontal = ((23.0 / 335.0) * getScreenWidth()).roundToInt()
//        binding.vpEmoji.setPadding(paddingHorizontal, 0, paddingHorizontal, 0)

        //        val params = binding.vpEmoji.layoutParams as ConstraintLayout.LayoutParams
        //        params.height = ((360.0 / 375.0) * getScreenWidth()).roundToInt()

        viewPagerAdapter = StickerViewPager2Adapter(this)
        binding.vpEmoji.adapter = viewPagerAdapter

        binding.vpEmoji.offscreenPageLimit = 3
        binding.vpEmoji.clipToPadding = false
        binding.vpEmoji.clipChildren = false
        val nextItemTranslationX = 17f * getScreenWidth() / 60
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(0))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.65f + r * 0.35f
            page.scaleX = 0.65f + r * 0.35f
            page.translationX = -position * nextItemTranslationX
            Timber.e("giangledinh $position")
            Timber.e("giangledinh currentItem ${binding.vpEmoji.currentItem}")
        }

        binding.vpEmoji.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                Timber.e("giangledinh currentItem ${binding.vpEmoji.currentItem}")
                super.onPageSelected(position)
                currentPagerPosition = position
                binding.vpEmoji.setPageTransformer(compositePageTransformer)
                if (isRevertData) {
                    binding.vpEmoji.postDelayed({
                        setShowFavoriteIcon(position)
                        isRevertData = false
                    }, 500L)
                } else {
                    binding.vpEmoji.post {
                        setShowFavoriteIcon(position)
                    }
                }
                indicatorAdapter.setSelectedIndicator(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                //                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                //                    val viewHolder = findViewHolderForCurrentPage(currentPagerPosition)
                //                    viewHolder?.binding?.ivFavorite?.inv()
                //                }
                //                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                //                    val viewHolder = findViewHolderForCurrentPage(currentPagerPosition)
                //                    viewHolder?.binding?.ivFavorite?.show()
                //                }
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                //                if (position == stickerList.size() -1  && positionOffset == 0f && !isLastPageSwiped){
                //                    if(counterPageScroll != 0){
                //                        isLastPageSwiped=true
                //                        currentPagerPosition = 0
                //                        binding.vpEmoji.setCurrentItem(0,true)
                //                    }
                //                    isLastPageSwiped=false
                //                    counterPageScroll++;
                //                }else{
                //                    counterPageScroll=0;
                //                }

                //                if(positionOffset == 0f && positionOffsetPixels == 0){
                //                    if (currentPagerPosition == 0) {
                //                        currentPagerPosition = stickerList.size-1
                //                        binding.vpEmoji.currentItem = stickerList.size-1
                //                    }
                //                    else if(currentPagerPosition == stickerList.size-1){
                //                        currentPagerPosition = 0
                //                        binding.vpEmoji.currentItem = 0
                //                    }
                //                }
                //                val viewHolder = findViewHolderForCurrentPage(currentPagerPosition-1)
                //                viewHolder?.binding?.ivFavorite?.inv()
            }
        })

        (binding.vpEmoji[0] as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
    }

    private fun setShowFavoriteIcon(position: Int) {
        val viewHolder1 = findViewHolderForCurrentPage(position)
        viewHolder1?.binding?.ivFavorite?.show()
        val viewHolder2 = findViewHolderForCurrentPage(position - 1)
        viewHolder2?.binding?.ivFavorite?.inv()
    }

    private fun initRcv() {
        layoutManager1 = LinearLayoutManager(requireContext())
        binding.rvEmoji1.layoutManager = layoutManager1
        smoothScroller1 = object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }

            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
                return super.calculateSpeedPerPixel(displayMetrics) * 8
            }

        }
        binding.rvEmoji1.setHasFixedSize(true)

        layoutManager2 = LinearLayoutManager(requireContext())
        layoutManager2.stackFromEnd = true
        binding.rvEmoji2.layoutManager = layoutManager2
        smoothScroller2 = object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }

            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
                return super.calculateSpeedPerPixel(displayMetrics) * 8
            }

            override fun onStart() {
                super.onStart()
                if (zoomInAnim == null) {
                    zoomInAnim = ScaleAnimation(
                        1f,
                        0.01f,
                        1f,
                        0.01f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f
                    )
                    zoomInAnim!!.duration = 350 // animation duration in milliseconds
                    zoomInAnim!!.fillAfter =
                        true // If fillAfter is true, the transformation that this animation performed will persist when it is finished.
                    zoomInAnim!!.repeatMode = Animation.REVERSE
                    zoomInAnim!!.repeatCount = 1
                    zoomInAnim!!.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation) {
                        }

                        override fun onAnimationEnd(animation: Animation) {
                            isRandomClick = false
                            setUpDataForViewPager(stickerTempList)
                        }

                        override fun onAnimationRepeat(animation: Animation) {
                            viewPagerAdapter.setSticker(stickerTempList[1], currentPagerPosition)
                        }
                    })
                }
                val viewHolder = findViewHolderForCurrentPage(currentPagerPosition)
                viewHolder?.binding?.ctlEmoji?.startAnimation(zoomInAnim)
//                binding.vpEmoji.postDelayed({
//                    val viewHolder = findViewHolderForCurrentPage(currentPagerPosition)
//                    viewHolder?.binding?.ctlEmoji?.startAnimation(zoomInAnim)
//                },50L)
            }

        }
        binding.rvEmoji2.setHasFixedSize(true)
    }

    private fun setUpDataForViewPager(list: List<Sticker>, position: Int = 1) {
        if (!isRandomClick) {
            stickerList.clear()
            stickerList.addAll(list)
            viewPagerAdapter.submitList(list)
            if (stickerList.size > 1) {
                binding.vpEmoji.setCurrentItem(position, false)
                binding.rvIndicator.show()
                indicatorAdapter.setSelectedIndicator(currentPagerPosition)
                indicatorAdapter.submitSize(stickerList.size, binding.rvIndicator)
            } else {
                binding.rvIndicator.gone()
            }
        } else {
            stickerTempList.clear()
            stickerTempList.addAll(list)
        }
    }


    override fun eventClick() {

        binding.headerMain.ivHome.setOnClickListener {
            if (!navArgs.fromFavoriteFragment) {
                mainViewModel.getNewStickerList.postValue(Unit)
            }
            findNavController().popBackStack(R.id.homeFragment, false)
        }

        binding.ivShare.setOnClickListener {
            if (checkTime(it, 1000)) {
                isFavoriteClicked = false
                getStickerBitmap()
            }
        }

        binding.tvRandom.setOnClickListener {
            if (checkTime(it,800)) {
                (activity as MainActivity).isDispatchTouchEvent(800L)
                isRandomClick = true
                randomEmoji()
            }
        }

        binding.viewEmoji1.setOnClickListener {
            if (checkTime(it)) {
                bottomSheet1.show(childFragmentManager, null)
            }
        }

        binding.viewEmoji2.setOnClickListener {
            if (checkTime(it)) {
                bottomSheet2.show(childFragmentManager, null)
            }
        }

        emojiAdapter1.setOnItemClickListener { position ->
            newCheckedPosition1 = position
        }

        emojiAdapter2.setOnItemClickListener { position ->
            newCheckedPosition2 = position
        }

    }

    private fun randomEmoji() {
        val list1 = arrayListOf<Emoji>()
        val list2 = arrayListOf<Emoji>()
        for (i in 0..4) {
            val position1 = (Math.random() * emojiList.size).toInt()
            val position2 = (Math.random() * emojiList.size).toInt()
            list1.add(emojiList[position1])
            list2.add(emojiList[position2])
            if (i == 0) {
                emojiAdapter2.setCheck(position2)
                oldCheckedPosition2 = position2
                newCheckedPosition2 = position2
            } else if (i == 4) {
                emojiAdapter1.setCheck(position1)
                oldCheckedPosition1 = position1
                newCheckedPosition1 = position1
                if (oldCheckedPosition1 == oldCheckedPosition2) {
                    Timber.e("giangld isTheSame")
                    oldCheckedPosition1 = emojiList.size - oldCheckedPosition1 - 1
                    list1[4] = emojiList[oldCheckedPosition1]
                    newCheckedPosition1 = oldCheckedPosition1
                    emojiAdapter1.setCheck(oldCheckedPosition1)
                }
            }
        }
        setUpRcvData(list1, list2)
        smoothScroller1.targetPosition = 4
        smoothScroller2.targetPosition = 0
        layoutManager1.startSmoothScroll(smoothScroller1)
        layoutManager2.startSmoothScroll(smoothScroller2)
        emoji1 = list1[4]
        emoji2 = list2[0]
        mainViewModel.getMixedStickerList(requireContext(), emoji1!!, emoji2!!)
    }

    private fun setUpRcvData(
        list1: List<Emoji>,
        list2: List<Emoji>
    ) {
        emojiAdapter3 = EmojiAdapter2(list1)
        binding.rvEmoji1.adapter = emojiAdapter3

        emojiAdapter4 = EmojiAdapter2(list2)
        binding.rvEmoji2.adapter = emojiAdapter4
    }

    override fun onPermissionGranted() {
    }

    private fun findViewHolderForCurrentPage(position: Int): StickerViewPager2Adapter.ViewHolder? {
        return (binding.vpEmoji[0] as RecyclerView).findViewHolderForAdapterPosition(position)
                as StickerViewPager2Adapter.ViewHolder?
    }

    override fun observerData() {

        mainViewModel.stickerList.observe(requireActivity()) {
            it?.let {
                if (!isRevertData) {
                    Timber.e("giangld")
                    setUpDataForViewPager(it)
                } else {
                    setUpDataForViewPager(it, currentPagerPosition)
                }
//                mainViewModel.stickerList.postValue(null)
            }
//            setUpDataForViewPager(getStickerListTest())
        }

        viewModel.bitmapFromView.observe(viewLifecycleOwner) {
            it?.let { bitmap ->
                if (!isFavoriteClicked) {
                    viewModel.getUriForImage(requireContext(), bitmap)
                } else {
                    viewModel.updateFavorite(
                        requireContext(),
                        stickerList[currentPagerPosition],
                        it
                    )
                }
                viewModel.bitmapFromView.postValue(null)
            }
        }

        mainViewModel.emojiList.observe(viewLifecycleOwner) {
            it?.let {
                emojiList = it
                if (!navArgs.fromFavoriteFragment) {
                    if (!isRevertData) {
                        Timber.e("giangld")
                        (activity as MainActivity).mashupEmoji()
                        initBottomSheetData()
                    }
                    else{
                        emojiAdapter2.submitList(emojiList)
                        emojiAdapter1.submitList(emojiList)
                        emojiAdapter1.setCheck(oldCheckedPosition1)
                        emojiAdapter2.setCheck(oldCheckedPosition2)
                    }
                    mainViewModel.emojiList.postValue(null)
                }
            }
        }

        viewModel.imageUri.observe(viewLifecycleOwner) {
            it?.let {
                shareImage(it)
                viewModel.imageUri.postValue(null)
            }
        }

        viewModel.isInsertStickerDone.observe(viewLifecycleOwner) {
            it?.let {
                findViewHolderForCurrentPage(currentPagerPosition)?.updateFavorite(it.toInt())
                stickerList[currentPagerPosition].isFavorite = true
                mainViewModel.isDataChange.postValue(Unit)
                viewModel.isInsertStickerDone.postValue(null)
            }
        }

        viewModel.isDeleteStickerDone.observe(viewLifecycleOwner) {
            it?.let {
                findViewHolderForCurrentPage(currentPagerPosition)?.updateFavorite(null)
                stickerList[currentPagerPosition].isFavorite = false
                mainViewModel.isDataChange.postValue(Unit)
                viewModel.isDeleteStickerDone.postValue(null)
            }
        }

        viewModel.sticker.observe(viewLifecycleOwner) {
            if (it != null) {
                Timber.e("giangld")
                sticker = it
                setUpDataForViewPager(listOf(it))
                setUpRcvData(
                    list1 = arrayListOf(it.emoji1),
                    list2 = arrayListOf(it.emoji2)
                )
            } else {
                sticker?.let { sticker ->
                    setUpDataForViewPager(listOf(sticker))
                    setUpRcvData(
                        list1 = arrayListOf(sticker.emoji1),
                        list2 = arrayListOf(sticker.emoji2)
                    )
                }
            }
        }

    }

    override fun initData() {
        if (navArgs.fromFavoriteFragment) {
            binding.tvChoose.inv()
            binding.tvRandom.inv()
            binding.viewEmoji1.isEnabled = false
            binding.viewEmoji2.isEnabled = false
            viewModel.getStickerById(navArgs.stickerId)
        } else if (!isRevertData && emojiList.isNotEmpty()) {
            Timber.e("giangld emojiList")
            initBottomSheetData()
        }
    }

    private fun initBottomSheetData() {
        Timber.e("giangld emojiList")
        oldCheckedPosition1 = pos1
        newCheckedPosition1 = pos1

        oldCheckedPosition2 = pos2
        newCheckedPosition2 = pos2

        emojiAdapter1.setCheck(pos1)
        emojiAdapter2.setCheck(pos2)
        emoji1 = emojiList[pos1]
        emoji2 = emojiList[pos2]
        emojiAdapter2.submitList(emojiList)
        emojiAdapter1.submitList(emojiList)
        setUpRcvData(listOf(emoji1!!), listOf(emoji2!!))
    }

    private fun getStickerListTest(): List<Sticker> {
        val list = arrayListOf<Sticker>()
//        val f = requireContext().assets.list("emoji")
//        f?.let {
//            for (file in it) {
//                val jsonString = loadJSONFromAsset(requireContext(), "emoji/$file/config.json")
//                if (jsonString != null) {
//                    list.add(Gson().fromJson(jsonString, Sticker::class.java))
//                }
//            }
//        }
        val string = loadJSONFromAsset(requireContext(), "emoji/1F626/config.json")
        list.add(Gson().fromJson(string, Sticker::class.java))
        return list
    }

    private fun loadJSONFromAsset(context: Context, path: String): String? {
        val json: String? = try {
            val input: InputStream = context.assets.open(path)
            val size: Int = input.available()
            val buffer = ByteArray(size)
            input.read(buffer)
            input.close()
            String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    private fun shareImage(uri: Uri) {
        try {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.type = "image/*"
            startActivity(Intent.createChooser(intent, "share image"))
        } catch (e: Exception) {
            showToast(getString(R.string.error))
        }
    }

    override fun onFavoriteClick(position: Int, sticker: Sticker) {
        (activity as MainActivity).isDispatchTouchEvent(500L)
        isFavoriteClicked = true
        if (!sticker.isFavorite) {
            getStickerBitmap()
        }
        else{
            viewModel.deleteFavorite(requireContext(),sticker.id)
        }
    }

    private fun getStickerBitmap() {
        Timber.e("giangld ${currentPagerPosition}")
        val currentPage = findViewHolderForCurrentPage(currentPagerPosition)
        currentPage?.binding?.ctlRoot?.let { view -> viewModel.getBitmapFromViewLiveData(view) }
    }

    private fun setUpDataForRcvChooseEmoji() {
        emojiAdapter4 = EmojiAdapter2(listOf(emoji2!!))
        binding.rvEmoji2.adapter = emojiAdapter4

        emojiAdapter3 = EmojiAdapter2(listOf(emoji1!!))
        binding.rvEmoji1.adapter = emojiAdapter3
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(Constant.IS_REVERT_DATA, true)
        if (!navArgs.fromFavoriteFragment) {
            outState.putString(Constant.EMOJI1, Gson().toJson(emoji1))
            outState.putString(Constant.EMOJI2, Gson().toJson(emoji2))
            outState.putInt(Constant.CURRENT_PAGE_POSITION, currentPagerPosition)
            outState.putInt(Constant.FIRST_CHECKED_POSITION, oldCheckedPosition1)
            outState.putInt(Constant.SECOND_CHECKED_POSITION, oldCheckedPosition2)
        } else {
            outState.putString(Constant.STICKER, Gson().toJson(sticker))
        }
        if (bottomSheet1.isVisible) {
            bottomSheet1.dismissAllowingStateLoss()
        }
        if (bottomSheet2.isVisible) {
            bottomSheet2.dismissAllowingStateLoss()
        }
    }
}