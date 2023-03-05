package com.tapbi.spark.emojimashup.ui.main

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tapbi.spark.emojimashup.data.local.db.dao.StickerDao
import com.tapbi.spark.emojimashup.data.local.db.dao.StickerPackageDao
import com.tapbi.spark.emojimashup.data.local.repo.AssetRepository
import com.tapbi.spark.emojimashup.data.local.repo.StickerRepository
import com.tapbi.spark.emojimashup.data.model.Emoji
import com.tapbi.spark.emojimashup.data.model.Sticker
import com.tapbi.spark.emojimashup.data.model.StickerPackage
import com.tapbi.spark.emojimashup.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val stickerDao: StickerDao,
    private val stickerRepository: StickerRepository,
    private val stickerPackageDao: StickerPackageDao,
    private val assetRepository: AssetRepository
) : BaseViewModel() {

    val getFavoriteStickersLiveData = stickerDao.getAllFavoriteStickerListLiveData()
    val currentImageDataVersionList = MutableLiveData<List<String>>()
    val isDataChange = MutableLiveData<Unit>()
    val stickerPackageList = MutableLiveData<List<StickerPackage>>()
    val emojiList = MutableLiveData<List<Emoji>>()
    val stickerList = MutableLiveData<List<Sticker>>()
    var getNewStickerList = MutableLiveData<Unit>()



    fun getAllStickerPackage(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            stickerPackageList.postValue(stickerRepository.getAllStickerPackage(context))
        }
    }

    fun getMixedStickerList(context: Context,emoji1: Emoji, emoji2: Emoji){
        viewModelScope.launch(Dispatchers.IO) {
            stickerList.postValue(stickerRepository.mixSticker(context,emoji1,emoji2))
        }
    }

    fun getAllCurrentImageVersion() {
        viewModelScope.launch(Dispatchers.IO) {
            currentImageDataVersionList.postValue(stickerPackageDao.getAllCurrentImageDataVersion())
        }
    }

    fun getEmojiList(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = arrayListOf<Emoji>()
            val emojiInfoString = assetRepository.loadJSONFromAsset(context, "emoji_info.json")
            val jsonArray = JSONArray(emojiInfoString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                list.add(
                    Emoji(
                        link = jsonObject.getString("link"),
                        jsonFolder = jsonObject.getString("jsonFolder")
                    )
                )
            }
            emojiList.postValue(list)
        }
    }
}