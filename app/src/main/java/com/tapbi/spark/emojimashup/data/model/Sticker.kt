package com.tapbi.spark.emojimashup.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class Sticker(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var imageFileName: String = "",
    var stickerPathSaved: String = "",
    var emojiName: String = "",
    var emoji1: Emoji = Emoji(),
    var emoji2: Emoji = Emoji(),
    var isFavorite: Boolean = false,
    var face: Face = Face(),
    var eyes: Eyes = Eyes(StickerPart(),StickerPart()),
    var mouth: StickerPart? = null,
    var eyebrows: Eyebrows? = null,
    var sweat: StickerPart? = null,
    var glasses: StickerPart? = null,
    var hat: StickerPart? = null,
    var nosePart: StickerPart? = null,
    var mouthPart: StickerPart? = null,
    var tear: StickerPart? = null,
    var hand: StickerPart? = null,
    var heart: StickerPart? = null,
    var hair: StickerPart? = null,
    var nose: StickerPart? = null,
    var beard: StickerPart? = null,
    var size: Long? = 0L,
    @Ignore
    val emojis: List<String> = arrayListOf(),

    ){

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Sticker

        if (face.link != other.face.link) return false
        if(face.link == other.face.link && face.rotationAngle != other.face.rotationAngle) return false
        if (eyes.leftEye.link != other.eyes.leftEye.link) return false
        if (eyes.leftEye.link == other.eyes.leftEye.link && eyes.leftEye.marginTop != other.eyes.leftEye.marginTop) return false
        if (eyes.rightEye.link != other.eyes.rightEye.link) return false
        if (eyes.rightEye.link == other.eyes.rightEye.link && eyes.rightEye.marginTop != other.eyes.rightEye.marginTop) return false
        if (mouth?.link != other.mouth?.link) return false
        if (eyebrows?.leftEyebrow?.link != other.eyebrows?.leftEyebrow?.link) return false
        if (eyebrows?.leftEyebrow?.link == other.eyebrows?.leftEyebrow?.link && eyebrows?.leftEyebrow?.marginTop != other.eyebrows?.leftEyebrow?.marginTop) return false
        if (eyebrows?.rightEyebrow?.link != other.eyebrows?.rightEyebrow?.link) return false
        if (eyebrows?.rightEyebrow?.link == other.eyebrows?.rightEyebrow?.link && eyebrows?.rightEyebrow?.marginTop != other.eyebrows?.rightEyebrow?.marginTop) return false
        if (sweat?.link != other.sweat?.link) return false
        if (glasses?.link != other.glasses?.link) return false
        if (hat?.link != other.hat?.link) return false
        if (nosePart?.link != other.nosePart?.link) return false
        if (mouthPart?.link != other.mouthPart?.link) return false
        if (tear?.link != other.tear?.link) return false
        if (hand?.link != other.hand?.link) return false
        if (heart?.link != other.heart?.link) return false
        if (hair?.link != other.hair?.link) return false
        if (nose?.link != other.nose?.link) return false
        if (beard?.link != other.beard?.link) return false

        return true
    }

    override fun hashCode(): Int {
        var result = face.hashCode()
        result = 31 * result + eyes.hashCode()
        result = 31 * result + mouth.hashCode()
        result = 31 * result + (eyebrows?.hashCode() ?: 0)
        result = 31 * result + (sweat?.hashCode() ?: 0)
        result = 31 * result + (glasses?.hashCode() ?: 0)
        result = 31 * result + (hat?.hashCode() ?: 0)
        result = 31 * result + (nosePart?.hashCode() ?: 0)
        result = 31 * result + (mouthPart?.hashCode() ?: 0)
        result = 31 * result + (tear?.hashCode() ?: 0)
        result = 31 * result + (hand?.hashCode() ?: 0)
        result = 31 * result + (heart?.hashCode() ?: 0)
        result = 31 * result + (hair?.hashCode() ?: 0)
        result = 31 * result + (nose?.hashCode() ?: 0)
        result = 31 * result + (beard?.hashCode() ?: 0)
        return result
    }
}
