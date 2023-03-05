package com.tapbi.spark.emojimashup.ui.splash

import androidx.lifecycle.MutableLiveData
import com.tapbi.spark.emojimashup.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : BaseViewModel(){
    var splashToMain = MutableLiveData<Boolean>()

}