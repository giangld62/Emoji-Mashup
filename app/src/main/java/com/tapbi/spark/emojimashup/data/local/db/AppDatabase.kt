package com.tapbi.spark.emojimashup.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tapbi.spark.emojimashup.common.Constant
import com.tapbi.spark.emojimashup.data.local.db.dao.StickerDao
import com.tapbi.spark.emojimashup.data.local.db.dao.StickerPackageDao
import com.tapbi.spark.emojimashup.data.model.Sticker
import com.tapbi.spark.emojimashup.data.model.StickerPackage

@Database(entities = [StickerPackage::class, Sticker::class], version = Constant.DB_VERSION, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val stickerDao: StickerDao
    abstract val stickerPackageDao: StickerPackageDao

    companion object {
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {}
        }
        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {}
        }
    }
}