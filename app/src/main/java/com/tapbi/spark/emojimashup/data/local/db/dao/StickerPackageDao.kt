package com.tapbi.spark.emojimashup.data.local.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tapbi.spark.emojimashup.data.model.StickerPackage

@Dao
interface StickerPackageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStickerPackage(stickerPackage: StickerPackage): Long

    @Query("update StickerPackage set imageDataVersion = imageDataVersion + 1 where identifier =:id")
    fun updateImageDataVersion(id: Int)

    @Query("update StickerPackage set imageDataVersion =:version where identifier =:id")
    fun updateImageDataVersion2(id: Int, version: Int)

    @Query("select count(identifier) from StickerPackage")
    fun getTotalStickerPackage(): Int

    @Query("select * from StickerPackage")
    fun getAllStickerPackage(): List<StickerPackage>

    @Query("update StickerPackage set isWhitelisted =:isAdded where identifier =:id")
    fun updateIsWhitelisted(isAdded: Boolean, id: Int)

    @Query("select imageDataVersion from StickerPackage")
    fun getAllCurrentImageDataVersion(): List<String>
}