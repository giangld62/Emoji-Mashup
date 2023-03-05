package com.tapbi.spark.emojimashup.utils

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.util.DisplayMetrics
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import com.tapbi.spark.emojimashup.R
import com.tapbi.spark.emojimashup.data.model.StickerPart
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import kotlin.math.roundToInt


fun getBitmapFromView(view: View): Bitmap {
    //Define a bitmap with the same size as the view
    val returnedBitmap =
        Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    //Bind a canvas to it
    val canvas = Canvas(returnedBitmap)
    //Get the view's background
    val bgDrawable: Drawable? = view.background
    if (bgDrawable != null) //has background drawable, then draw it on the canvas
        bgDrawable.draw(canvas) else  //does not have background drawable, then draw white background on the canvas
        canvas.drawColor(Color.WHITE)
    // draw the view on the canvas
    view.draw(canvas)
    //return the bitmap
    if (view.rotation == 0F) {
        return returnedBitmap
    } else {
        val matrix = Matrix()
        matrix.postRotate(view.rotation)
        return Bitmap.createBitmap(
            returnedBitmap,
            0,
            0,
            returnedBitmap.width,
            returnedBitmap.height,
            matrix,
            true
        )
    }
}

fun getImageUri(context: Context, img: Bitmap?): Uri? {
    return try {
        img?.let{ imgBitmap ->
            val file = File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "emoji.png"
            )
            val stream = FileOutputStream(file)
            val bitmap = Bitmap.createScaledBitmap(imgBitmap,512,512,true )
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
            stream.close()
            FileProvider.getUriForFile(
                context,
                "com.tapbi.spark.emojimashup.provider",
                file
            )
        }
    } catch (e: IOException) {
        null
    }
}

fun pxFromDp(dp: Float, context: Context): Double {
    return dp * context.resources.displayMetrics.density.toDouble()
}


fun getScreenWidth(): Int {
    return Resources.getSystem().displayMetrics.widthPixels
}

fun getScreenHeight(): Int {
    return Resources.getSystem().displayMetrics.heightPixels
}

fun setSizeAndMargin(
    widthPx: Double,
    heightPx: Double,
    iv: ImageView,
    marginStart: Double,
    marginTop: Double,
    widthPercent: Double,
    heightPercent: Double
) {
    val params = iv.layoutParams as ConstraintLayout.LayoutParams
    iv.translationX = (marginStart * widthPx).toFloat()
    iv.translationY = (marginTop * heightPx).toFloat()
    params.width = (widthPercent * widthPx).roundToInt()
    params.height = (heightPercent * heightPx).roundToInt()
    iv.requestLayout()
}

fun checkIntersect(
    lowerPart: StickerPart,
    higherPart: StickerPart,
    percentIntersection: Float
): Boolean {
    val condition =
        (lowerPart.marginTop + lowerPart.height > higherPart.marginTop)
                &&
                (lowerPart.marginTop + lowerPart.height - higherPart.marginTop) / (higherPart.height) > percentIntersection
    return condition
}

fun writeEmojiObjectToFile(context: Context) {
    val f = context.assets.list("emoji")
    val stringBuilder = StringBuilder()
    f?.let {
        for (file in it) {
            val listFile = context.assets.list("emoji/$file")
//            Timber.e("giangld ${listFile?.size}")
//            Timber.e("giangld ${listFile!![0]}")
//            Timber.e("giangld ${file}")
            val string = "number${listFile!![0]} - $file"
            stringBuilder.append(string)
            stringBuilder.append("\n")
            stringBuilder.append("\n")
        }
    }
    writeFileOnInternalStorage(context, "emoji_info.json", stringBuilder.toString())

//    Timber.e("giangld ${f?.size}")
}

fun writeFileOnInternalStorage(mcoContext: Context, sFileName: String, sBody: String?) {
    val dir = File(mcoContext.filesDir, "mydir")
    if (!dir.exists()) {
        dir.mkdir()
    }
    try {
        val gpxFile = File(dir, sFileName)
        val writer = FileWriter(gpxFile)
        writer.append(sBody)
        writer.flush()
        writer.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun convertPixelsToDp(px: Float, context: Context): Float {
    return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun sendFeedback(context: Context, supportEmail: String, subject: String, text: String?) {
    val emailIntent = Intent(Intent.ACTION_SEND)
    emailIntent.type = "text/email"
    emailIntent.setPackage("com.google.android.gm")
    emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmail))
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
    emailIntent.putExtra(Intent.EXTRA_TEXT, text)

    context.startActivity(
        Intent.createChooser(
            emailIntent,
            context.getString(R.string.send_email_report_app)
        )
    )
}

fun getStatusBarHeight(resources: Resources): Int {
    var result = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    return result
}