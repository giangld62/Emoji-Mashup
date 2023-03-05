package com.tapbi.spark.emojimashup.ui.main.stickers

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tapbi.spark.emojimashup.R
import com.tapbi.spark.emojimashup.common.Constant
import com.tapbi.spark.emojimashup.data.model.StickerPackage
import com.tapbi.spark.emojimashup.databinding.FragmentStickersBinding
import com.tapbi.spark.emojimashup.ui.adapter.StickerPackageAdapter
import com.tapbi.spark.emojimashup.ui.base.BaseBindingFragment
import com.tapbi.spark.emojimashup.utils.show
import java.io.File

class StickersFragment : BaseBindingFragment<FragmentStickersBinding, StickersViewModel>(),
    StickerPackageAdapter.IStickerPackageAdapter {
    private val CREATE_STICKER_PACK_ACTION = "org.telegram.messenger.CREATE_STICKER_PACK"
    private val CREATE_STICKER_PACK_EMOJIS_EXTRA = "STICKER_EMOJIS"
    private val CREATE_STICKER_PACK_IMPORTER_EXTRA = "IMPORTER"

    private lateinit var stickerPackageAdapter: StickerPackageAdapter
    private var stickerPackageList = listOf<StickerPackage>()

    override fun getViewModel(): Class<StickersViewModel> {
        return StickersViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_stickers

    override fun initView() {
        stickerPackageAdapter = StickerPackageAdapter(this)
        binding.headerMain.tvTitle.text = getString(R.string.stickers)
        binding.rvStickerPackage.adapter = stickerPackageAdapter
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.isAutoMeasureEnabled = false
        binding.rvStickerPackage.layoutManager = layoutManager
    }

    override fun initData() {
        mainViewModel.getAllStickerPackage(requireContext())
    }

    override fun observerData() {
        mainViewModel.stickerPackageList.observe(viewLifecycleOwner) {
            stickerPackageList = it
            val tempList = arrayListOf<StickerPackage>()
            tempList.addAll(stickerPackageList)
            for (item in stickerPackageList) {
                if (item.stickers.isEmpty()) {
                    tempList.remove(item)
                }
            }
            stickerPackageAdapter.submitList(tempList)
        }

        mainViewModel.getFavoriteStickersLiveData.observe(viewLifecycleOwner) {
            binding.tvNoData.show(it.isNullOrEmpty())
        }
    }

    override fun eventClick() {
        binding.headerMain.ivHome.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {

    }

    override fun onResume() {
        super.onResume()
        mainViewModel.getAllStickerPackage(requireContext())
    }

    override fun onPermissionGranted() {

    }


    override fun onAddToWhatsAppClick(position: Int, stickerPackage: StickerPackage) {
        if (stickerPackage.stickers.size > 2) {
            addStickerPackToWhatsApp(
                requireContext(),
                stickerPackage.identifier.toString(),
                stickerPackage.name
            )
        }
        else{
            showToast(getString(R.string.add_failed))
        }
    }

    override fun onAddToTelegramClick(stickerPackage: StickerPackage) {
        if (stickerPackage.stickers.size > 2) {
            val uris = arrayListOf<Uri>()
            for (sticker in stickerPackage.stickers) {
                getRawUri(sticker.stickerPathSaved)?.let { uris.add(it) }
            }
            doImport(uris)
        }
        else{
            showToast(getString(R.string.add_failed))
        }
    }

    private fun doImport(stickers: ArrayList<Uri>) {
        val intent = Intent(CREATE_STICKER_PACK_ACTION)
        intent.putExtra(Intent.EXTRA_STREAM, stickers)
        intent.putExtra(CREATE_STICKER_PACK_IMPORTER_EXTRA, context?.packageName)
        intent.type = "image/*"
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            showToast(getString(R.string.add_pack_to_tele_fail), Toast.LENGTH_LONG)
        } catch (e: Exception) {
            e.printStackTrace()
            //no activity to handle intent
        }
    }

    private fun getRawUri(filePath: String): Uri? {
        val file = File(filePath)
        val uri = try {
            FileProvider.getUriForFile(
                requireContext(),
                Constant.APP_FILE_PROVIDER_AUTHORITIES,
                file
            )
        } catch (e: Exception) {
            null
        }
        if (uri != null) {
            requireContext().grantUriPermission(
                getPackage(),
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
        return uri
    }

    private fun getPackage(): String {
        val pm: PackageManager = requireContext().packageManager
        val list = listOf(
            "org.telegram.messenger",
            "org.telegram.messenger.web",
            "org.thunderdog.challegram"
        )
        for (pkg in list) {
            try {
                pm.getPackageInfo(pkg, PackageManager.GET_META_DATA)
                return pkg
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return ""
    }
}