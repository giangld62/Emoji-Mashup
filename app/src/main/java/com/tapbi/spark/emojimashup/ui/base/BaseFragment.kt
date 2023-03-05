package com.tapbi.spark.emojimashup.ui.base

import androidx.fragment.app.Fragment
import com.tapbi.spark.emojimashup.data.local.SharedPreferenceHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseFragment : Fragment(){
    @Inject
    lateinit var sharedPreferenceHelper: SharedPreferenceHelper
}