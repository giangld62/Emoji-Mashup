package com.tapbi.spark.emojimashup.data.local.repo

import android.content.Context
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

class AssetRepository @Inject constructor(){
    fun loadJSONFromAsset(context: Context, path: String): String? {
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
}