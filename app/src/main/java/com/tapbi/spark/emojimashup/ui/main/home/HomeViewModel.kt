package com.tapbi.spark.emojimashup.ui.main.home

import androidx.lifecycle.MutableLiveData
import com.tapbi.spark.emojimashup.data.model.Sticker
import com.tapbi.spark.emojimashup.ui.base.BaseViewModel
import javax.inject.Inject

class HomeViewModel @Inject constructor(): BaseViewModel() {
    val nextScreenId = MutableLiveData<Integer>()
}