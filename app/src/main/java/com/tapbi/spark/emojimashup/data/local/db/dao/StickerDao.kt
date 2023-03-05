package com.tapbi.spark.emojimashup.data.local.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tapbi.spark.emojimashup.data.model.Emoji
import com.tapbi.spark.emojimashup.data.model.Sticker

@Dao
interface StickerDao {

    @Query("select * from Sticker where (emoji1 =:emoji1 and emoji2 =:emoji2) or ((emoji1 =:emoji2 and emoji2 =:emoji1))")
    fun getFavoriteStickerList(emoji1: Emoji, emoji2: Emoji): List<Sticker>

    @Query("select * from Sticker order by id desc")
    fun getAllFavoriteStickerListLiveData(): LiveData<List<Sticker>>

    @Query("select * from Sticker")
    fun getAllFavoriteStickerList(): List<Sticker>

    @Query("delete from Sticker where id =:id")
    fun deleteSticker(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSticker(sticker: Sticker): Long

    @Query("select count(id) from Sticker")
    fun getFavoriteStickerListSize(): Int

    @Query("update Sticker set stickerPathSaved =:path where id =:id")
    fun updatePathSaved(id: Int, path: String)

    @Query("select * from Sticker where id =:id")
    fun getStickerById(id: Int): Sticker

}