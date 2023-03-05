package com.tapbi.spark.emojimashup.data.local.db

import androidx.room.TypeConverter
import com.tapbi.spark.emojimashup.data.model.*
import timber.log.Timber

class Converters {

    @TypeConverter
    fun fromEmoji(emoji: Emoji): String{
        return "${emoji.link},${emoji.jsonFolder}"
    }

    @TypeConverter
    fun toEmoji(emojiString: String): Emoji{
        val list = emojiString.split(",")
        return Emoji(
            link = list[0],
            jsonFolder = list[1]
        )
    }


    @TypeConverter
    fun fromStickerPart(stickerPart: StickerPart?): String? {
        return if (stickerPart == null) {
            null
        } else {
            "${stickerPart.link},${stickerPart.width},${stickerPart.height},${stickerPart.marginStart},${stickerPart.marginTop}"
        }
    }

    @TypeConverter
    fun toStickerPart(stickerPartString: String?): StickerPart? {
        return if (stickerPartString == null) {
            null
        } else {
            val list = stickerPartString.split(",")
            StickerPart(
                link = list[0],
                width = list[1].toDouble(),
                height = list[2].toDouble(),
                marginStart = list[3].toDouble(),
                marginTop = list[4].toDouble()
            )
        }
    }

    @TypeConverter
    fun fromEyes(eyes: Eyes): String {
        return "${eyes.leftEye.link},${eyes.leftEye.width},${eyes.leftEye.height},${eyes.leftEye.marginStart},${eyes.leftEye.marginTop}," +
                "${eyes.rightEye.link},${eyes.rightEye.width},${eyes.rightEye.height},${eyes.rightEye.marginStart},${eyes.rightEye.marginTop}"
    }

    @TypeConverter
    fun toEyes(eyesString: String): Eyes {
        val list = eyesString.split(",")
        return Eyes(
            leftEye = StickerPart(
                link = list[0],
                width = list[1].toDouble(),
                height = list[2].toDouble(),
                marginStart = list[3].toDouble(),
                marginTop = list[4].toDouble()
            ),
            rightEye = StickerPart(
                link = list[5],
                width = list[6].toDouble(),
                height = list[7].toDouble(),
                marginStart = list[8].toDouble(),
                marginTop = list[9].toDouble()
            )
        )
    }


    @TypeConverter
    fun fromEyebrows(eyebrows: Eyebrows?): String? {
        return if (eyebrows == null)
            null
        else
            "${eyebrows.leftEyebrow.link},${eyebrows.leftEyebrow.width},${eyebrows.leftEyebrow.height},${eyebrows.leftEyebrow.marginStart},${eyebrows.leftEyebrow.marginTop}," +
                "${eyebrows.rightEyebrow.link},${eyebrows.rightEyebrow.width},${eyebrows.rightEyebrow.height},${eyebrows.rightEyebrow.marginStart},${eyebrows.rightEyebrow.marginTop}"
    }

    @TypeConverter
    fun toEyebrows(eyebrowsString: String?): Eyebrows? {
        return if(eyebrowsString == null)
            null
        else{
            val list = eyebrowsString.split(",")
            Eyebrows(
                leftEyebrow = StickerPart(
                    link = list[0],
                    width = list[1].toDouble(),
                    height = list[2].toDouble(),
                    marginStart = list[3].toDouble(),
                    marginTop = list[4].toDouble()
                ),
                rightEyebrow = StickerPart(
                    link = list[5],
                    width = list[6].toDouble(),
                    height = list[7].toDouble(),
                    marginStart = list[8].toDouble(),
                    marginTop = list[9].toDouble()
                )
            )
        }
    }

    @TypeConverter
    fun fromFace(face: Face): String {
        return "${face.link},${face.ratio},${face.rotationAngle}"
    }

    @TypeConverter
    fun toFace(faceString: String): Face {
        val list = faceString.split(",")
        return Face(
            link = list[0],
            ratio = list[1].toDouble(),
            rotationAngle = list[2].toFloat()
        )
    }


}