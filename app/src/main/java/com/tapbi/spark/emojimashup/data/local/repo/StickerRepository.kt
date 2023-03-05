package com.tapbi.spark.emojimashup.data.local.repo

import android.content.Context
import android.graphics.Bitmap
import com.google.gson.Gson
import com.tapbi.spark.emojimashup.common.Constant
import com.tapbi.spark.emojimashup.data.local.db.dao.StickerDao
import com.tapbi.spark.emojimashup.data.local.db.dao.StickerPackageDao
import com.tapbi.spark.emojimashup.data.model.*
import com.tapbi.spark.emojimashup.provider.WhitelistCheck
import com.tapbi.spark.emojimashup.ui.main.MainActivity.Companion.currentImageDataVersionList
import com.tapbi.spark.emojimashup.utils.checkIntersect
import timber.log.Timber
import java.io.*
import javax.inject.Inject
import kotlin.math.abs


class StickerRepository @Inject constructor(
    private val assetRepository: AssetRepository,
    private val stickerDao: StickerDao,
    private val stickerPackageDao: StickerPackageDao
) {

    fun mixSticker(context: Context, emoji1: Emoji, emoji2: Emoji): List<Sticker> {
        val favStickerList = stickerDao.getFavoriteStickerList(emoji1, emoji2)
        val list = arrayListOf<Sticker>()
        val jsonString1 =
            assetRepository.loadJSONFromAsset(context, "emoji/${emoji1.jsonFolder}/config.json")
        val sticker1 = Gson().fromJson(jsonString1, Sticker::class.java)
        val jsonString2 =
            assetRepository.loadJSONFromAsset(context, "emoji/${emoji2.jsonFolder}/config.json")
        val sticker2 = Gson().fromJson(jsonString2, Sticker::class.java)

        val eyeList = getEyeList(sticker1.eyes, sticker2.eyes)

        val glassesList = getStickerPart(sticker1.glasses, sticker2.glasses)

        val hatList = getStickerPart(sticker1.hat, sticker2.hat)

        val noseList = getStickerPart(sticker1.nose, sticker2.nose)

        val hairList = getStickerPart(sticker1.hair, sticker2.hair)

        val beardList = getStickerPart(sticker1.beard, sticker2.beard)

        val eyebrowList = getEyebrowList(sticker1.eyebrows, sticker2.eyebrows)

        val mouthList = getStickerPart(sticker1.mouth, sticker2.mouth)

        val faceList = getFaceList(sticker1.face, sticker2.face)

        val sweatList = getStickerPart(sticker1.sweat, sticker2.sweat)

        val mouthPartList = getStickerPart(sticker1.mouthPart, sticker2.mouthPart)

        val nosePartList = getStickerPart(sticker1.nosePart, sticker2.nosePart)

        val handList = getStickerPart(sticker1.hand, sticker2.hand)

        val heartList = getStickerPart(sticker1.heart, sticker2.heart)

        val tearList = getStickerPart(sticker1.tear, sticker2.tear)

        for (eye in eyeList) {
            for (mouth in mouthList) {
                for (eyebrow in eyebrowList) {
                    for (face in faceList) {
                        for (sweat in sweatList) {
                            for (glasses in glassesList) {
                                for (hat in hatList) {
                                    for (nosePart in nosePartList) {
                                        for (mouthPart in mouthPartList) {
                                            for (hand in handList) {
                                                for (tear in tearList) {
                                                    for (heart in heartList) {
                                                        for (beard in beardList) {
                                                            for (nose in noseList) {
                                                                for (hair in hairList) {
                                                                    if (sticker1 == sticker2) {
                                                                        sticker1.emoji1 = emoji1
                                                                        sticker1.emoji2 = emoji2
                                                                        if (favStickerList.isNotEmpty()) {
                                                                            for (fav in favStickerList) {
                                                                                if (fav == sticker1) {
                                                                                    sticker1.id = fav.id
                                                                                    sticker1.isFavorite = fav.isFavorite
                                                                                    break
                                                                                }
                                                                            }
                                                                        }
                                                                        list.add(sticker1)
                                                                        return list
                                                                    }
                                                                    if (eyebrow != null && (checkIntersect(
                                                                            lowerPart = eyebrow.leftEyebrow,
                                                                            higherPart = eye.leftEye,
                                                                            percentIntersection = 0.25F
                                                                        ) || checkIntersect(
                                                                            lowerPart = eyebrow.rightEyebrow,
                                                                            higherPart = eye.rightEye,
                                                                            percentIntersection = 0.25F
                                                                        ))
                                                                    ) {
                                                                        continue
                                                                    }
                                                                    if (mouth != null && (checkIntersect(
                                                                            lowerPart = eye.leftEye,
                                                                            higherPart = mouth,
                                                                            percentIntersection = 0.15F
                                                                        ) || checkIntersect(
                                                                            lowerPart = eye.rightEye,
                                                                            higherPart = mouth,
                                                                            percentIntersection = 0.15F
                                                                        ))
                                                                    ) {
                                                                        continue
                                                                    }

                                                                    if (nose != null && mouth != null && checkIntersect(
                                                                            lowerPart = nose,
                                                                            higherPart = mouth,
                                                                            percentIntersection = 1.3F
                                                                        )
                                                                    ) {
                                                                        continue
                                                                    }
                                                                    val sticker = Sticker(
                                                                        face = face,
                                                                        eyes = eye,
                                                                        eyebrows = eyebrow,
                                                                        mouth = mouth,
                                                                        sweat = sweat,
                                                                        glasses = glasses,
                                                                        hat = hat,
                                                                        nosePart = nosePart,
                                                                        mouthPart = mouthPart,
                                                                        hand = hand,
                                                                        tear = tear,
                                                                        heart = heart,
                                                                        beard = beard,
                                                                        nose = nose,
                                                                        hair = hair,
                                                                        emoji1 = emoji1,
                                                                        emoji2 = emoji2
                                                                    )
                                                                    list.add(sticker)
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (favStickerList.isNotEmpty()) {
            for (fav in favStickerList) {
                for (sticker in list) {
                    if (fav == sticker) {
                        sticker.id = fav.id
                        sticker.isFavorite = fav.isFavorite
                        break
                    }
                }
            }
        }

        return list
    }

    private fun getStickerPart(
        stickerPart1: StickerPart?, stickerPart2: StickerPart?
    ): List<StickerPart?> {
        val list = arrayListOf<StickerPart?>()
        list.add(stickerPart1)
        list.add(stickerPart2)
        if (stickerPart1?.link == stickerPart2?.link) {
            list.removeAt(1)
        }
        return list
    }

    private fun getEyeList(eyes1: Eyes, eyes2: Eyes): List<Eyes> {
        val list = arrayListOf<Eyes>()
        list.add(eyes1)
        list.add(eyes2)
        if (eyes1.leftEye.link == eyes2.leftEye.link && eyes1.rightEye.link == eyes2.rightEye.link) {
            if (abs(eyes1.leftEye.marginTop - eyes2.leftEye.marginTop) < 0.03) list.remove(eyes1)
        }
        return list
    }

    private fun getEyebrowList(eyebrows1: Eyebrows?, eyebrows2: Eyebrows?): List<Eyebrows?> {
        val eyebrowList = arrayListOf<Eyebrows?>()
        eyebrowList.add(eyebrows1)
        eyebrowList.add(eyebrows2)
        if (eyebrows1?.leftEyebrow?.link == eyebrows2?.leftEyebrow?.link && eyebrows1?.rightEyebrow?.link == eyebrows2?.rightEyebrow?.link) {
            if (eyebrows1 != null && eyebrows2 != null) {
                if (abs(eyebrows1.leftEyebrow.marginTop - eyebrows2.leftEyebrow.marginTop) < 0.03) eyebrowList.remove(
                    eyebrows1
                )
            } else {
                eyebrowList.removeAt(1)
            }
        }
        return eyebrowList
    }

    private fun getFaceList(face1: Face, face2: Face): List<Face> {
        val faceList = arrayListOf<Face>()
        faceList.add(face1)
        faceList.add(face2)
        if (face1.link == face2.link && face1.rotationAngle == face2.rotationAngle) {
            faceList.removeAt(1)
        }
        return faceList
    }

    private fun saveToInternalStorage(
        context: Context,
        bitmapImage: Bitmap,
        stickerFileName: String
    ): ByteArray? {
        val directory = File(context.externalCacheDir, Constant.STICKER_FOLDER)
        if (!directory.exists()) {
            directory.mkdir()
        }
        val myPath = File(directory, "${stickerFileName}.webp")
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(myPath)
            val scaledBitmap = Bitmap.createScaledBitmap(bitmapImage, 512, 512, true)
            scaledBitmap.compress(Bitmap.CompressFormat.WEBP, 75, fos)

            val buf = BufferedInputStream(FileInputStream(myPath))
            val size = myPath.length().toInt()
            val bytes = ByteArray(size)
            buf.read(bytes, 0, bytes.size)
            buf.close()
//            val bytes = Files.readAllBytes(myPath.toPath())
            return bytes
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    fun deleteSticker(context: Context, id: Int) {
        stickerDao.deleteSticker(id)
        val stickerPackageList = stickerPackageDao.getAllStickerPackage()
        val file = File(context.externalCacheDir, "${Constant.STICKER_FOLDER}/sticker_${id}.webp")
        if (file.exists()) {
            file.delete()
        }
        val stickerCount = stickerDao.getFavoriteStickerListSize()
        if (stickerCount in 3..30) {
            stickerPackageDao.updateImageDataVersion(stickerPackageList[0].identifier)
        } else if (stickerCount > 30) {
            if (stickerCount % 30 >= 3) {
                for (stickerPackage in stickerPackageList) {
                    stickerPackageDao.updateImageDataVersion(stickerPackage.identifier)
                }
            } else {
                for (i in 0..stickerPackageList.size - 2) {
                    stickerPackageDao.updateImageDataVersion(stickerPackageList[i].identifier)
                }
                if (stickerPackageList.size == currentImageDataVersionList.size) {
                    stickerPackageDao.updateImageDataVersion2(stickerPackageList[stickerPackageList.size-1].identifier,currentImageDataVersionList[stickerPackageList.size-1].toInt())
                }
            }
        }
        else{
            stickerPackageDao.updateImageDataVersion2(stickerPackageList[0].identifier,if(currentImageDataVersionList.isNotEmpty()) currentImageDataVersionList[0].toInt() else 1)
        }
    }

    fun insertSticker(context: Context, sticker: Sticker, bitmapImage: Bitmap): Long {
        val newSticker = Sticker(
            emojiName = sticker.emojiName,
            emoji1 = sticker.emoji1,
            emoji2 = sticker.emoji2,
            isFavorite = true,
            face = sticker.face,
            eyes = sticker.eyes,
            mouth = sticker.mouth,
            eyebrows = sticker.eyebrows,
            sweat = sticker.sweat,
            glasses = sticker.glasses,
            hat = sticker.hat,
            nosePart = sticker.nosePart,
            mouthPart = sticker.mouthPart,
            tear = sticker.tear,
            hand = sticker.hand,
            heart = sticker.heart,
            hair = sticker.hair,
            nose = sticker.nose,
            beard = sticker.beard
        )

        val id = stickerDao.insertSticker(newSticker)

        val totalStickerPackage = stickerPackageDao.getTotalStickerPackage()
        val stickerCount = stickerDao.getFavoriteStickerListSize()
        val index = ((stickerCount - 1) / 30) + 1
        if (index > totalStickerPackage) {
            stickerPackageDao.insertStickerPackage(
                StickerPackage(
                    name = "Remix Sticker #$index",
                    publisher = "giangle"
                )
            )
        } else {
            val stickerPackageList = stickerPackageDao.getAllStickerPackage()
            if (stickerCount in 3..30) {
                stickerPackageDao.updateImageDataVersion(stickerPackageList[0].identifier)
            }
            else if (stickerCount > 30) {
                if (stickerCount % 30 >= 3) {
                    stickerPackageDao.updateImageDataVersion(stickerPackageList[totalStickerPackage - 1].identifier)
                }
                else{
                    if (totalStickerPackage == currentImageDataVersionList.size) {
                        stickerPackageDao.updateImageDataVersion2(stickerPackageList[totalStickerPackage - 1].identifier,
                            currentImageDataVersionList[totalStickerPackage - 1].toInt())
                    }
                }
            }
            else{
                stickerPackageDao.updateImageDataVersion2(stickerPackageList[0].identifier,if(currentImageDataVersionList.isNotEmpty()) currentImageDataVersionList[0].toInt() else 1)
            }
        }

        stickerDao.updatePathSaved(
            id.toInt(),
            "${context.externalCacheDir}/${Constant.STICKER_FOLDER}/sticker_${id}.webp"
        )
        saveToInternalStorage(context, bitmapImage, "sticker_${id}")
        return id
    }

    fun getAllStickerPackage(context: Context): List<StickerPackage> {
        val list = arrayListOf<StickerPackage>()
        list.addAll(stickerPackageDao.getAllStickerPackage())
        val stickerList = stickerDao.getAllFavoriteStickerList()
        if (list.isNotEmpty() && stickerList.isNotEmpty()) {
            val index = (stickerList.size - 1) / 30
            if (index == 0) {
                list[0].stickers = stickerList
                val splitTempList = stickerList[0].stickerPathSaved.split("/")
                list[0].trayImageFile = splitTempList[splitTempList.size - 1]
                val isWhitelisted = WhitelistCheck.isStickerPackWhitelistedInWhatsAppConsumer(
                    context,
                    list[0].identifier.toString()
                )
                list[0].isWhitelisted = isWhitelisted
            } else {
                var i = 0
                while (index - i > 0) {
                    list[i].stickers = stickerList.subList(i * 30, i * 30 + 30)
                    val splitTempList = stickerList[i * 30].stickerPathSaved.split("/")
                    list[i].trayImageFile = splitTempList[splitTempList.size - 1]
                    val isWhitelisted = WhitelistCheck.isStickerPackWhitelistedInWhatsAppConsumer(
                        context,
                        list[i].identifier.toString()
                    )
                    list[i].isWhitelisted = isWhitelisted
                    i++
                }
                if (index == i) {
                    list[i].stickers = stickerList.subList(i * 30, stickerList.size)
                    val splitTempList = stickerList[i * 30].stickerPathSaved.split("/")
                    list[i].trayImageFile = splitTempList[splitTempList.size - 1]
                    val isWhitelisted = WhitelistCheck.isStickerPackWhitelistedInWhatsAppConsumer(
                        context,
                        list[i].identifier.toString()
                    )
                    list[i].isWhitelisted = isWhitelisted
                }
            }
        }
        return list
    }
}