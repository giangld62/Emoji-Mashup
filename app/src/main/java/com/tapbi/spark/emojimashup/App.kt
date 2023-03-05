package com.tapbi.spark.emojimashup

import androidx.multidex.MultiDexApplication
import com.tapbi.spark.emojimashup.utils.MyDebugTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        initLog()
        instance = this
    }


    private fun initLog() {
        if (BuildConfig.DEBUG) {
            Timber.plant(MyDebugTree())
        }
    }

    companion object {
        var instance: App? = null
            private set
    }
}