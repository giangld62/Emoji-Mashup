package com.tapbi.spark.emojimashup.ui.main.remix_emoji

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tapbi.spark.emojimashup.data.local.db.dao.StickerDao
import com.tapbi.spark.emojimashup.data.local.repo.StickerRepository
import com.tapbi.spark.emojimashup.data.model.Sticker
import com.tapbi.spark.emojimashup.ui.base.BaseViewModel
import com.tapbi.spark.emojimashup.utils.getBitmapFromView
import com.tapbi.spark.emojimashup.utils.getImageUri
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemixEmojiViewModel @Inject constructor(
    private val stickerRepository: StickerRepository,
    private val stickerDao: StickerDao
) : BaseViewModel() {
    val bitmapFromView = MutableLiveData<Bitmap>()
    val imageUri = MutableLiveData<Uri?>()
    val isDeleteStickerDone = MutableLiveData<Unit>()
    val isInsertStickerDone = MutableLiveData<Long>()
    val sticker = MutableLiveData<Sticker>()

    fun updateFavorite(context: Context,sticker: Sticker, bitmapImage: Bitmap){
        viewModelScope.launch(Dispatchers.IO){
            isInsertStickerDone.postValue(stickerRepository.insertSticker(context,sticker, bitmapImage))
        }
    }

    fun deleteFavorite(context: Context, id: Int){
        viewModelScope.launch(Dispatchers.IO) {
            isDeleteStickerDone.postValue(stickerRepository.deleteSticker(context,id))
        }
    }

    fun getBitmapFromViewLiveData(view: View) {
        viewModelScope.launch(Dispatchers.IO) {
            bitmapFromView.postValue(getBitmapFromView(view))
        }
    }

    fun getUriForImage(context: Context, bitmap: Bitmap?) {
        viewModelScope.launch(Dispatchers.IO) {
            imageUri.postValue(getImageUri(context, bitmap))
        }
    }

    fun getStickerById(id: Int){
        viewModelScope.launch(Dispatchers.IO) {
            sticker.postValue(stickerDao.getStickerById(id))
        }
    }
}
