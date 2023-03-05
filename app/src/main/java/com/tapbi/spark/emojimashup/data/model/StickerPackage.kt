package com.tapbi.spark.emojimashup.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class StickerPackage(
    @PrimaryKey(autoGenerate = true)
    var identifier                      : Int = 0,
    var name                            : String = "",
    var publisher                       : String = "",
    var trayImageFile                   : String = "",
    var publisherEmail                  : String = "",
    var publisherWebsite                : String = "",
    var privacyPolicyWebsite            : String = "",
    var licenseAgreementWebsite         : String = "",
    var imageDataVersion                : String = "1",
    var avoidCache                      : Boolean = false,
    var animatedStickerPack             : Boolean = false,

    var iosAppStoreLink                 : String = "",
    var totalSize                       : Long = 0,
    var androidPlayStoreLink            : String = "",
    var isWhitelisted                   : Boolean = false,
    @Ignore
    var stickers                        : List<Sticker> = arrayListOf()
)
