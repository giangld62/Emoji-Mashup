package com.tapbi.spark.emojimashup.di

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.tapbi.spark.emojimashup.common.Constant
import com.tapbi.spark.emojimashup.data.local.db.AppDatabase
import com.tapbi.spark.emojimashup.data.local.db.dao.StickerDao
import com.tapbi.spark.emojimashup.data.local.db.dao.StickerPackageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideSharedPreference(app: Application): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(app.applicationContext)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app.applicationContext,
            AppDatabase::class.java,
            Constant.DB_NAME
        ).fallbackToDestructiveMigration().addMigrations(AppDatabase.MIGRATION_1_2).build()
    }

    @Provides
    @Singleton
    fun provideStickerDao(db: AppDatabase): StickerDao {
        return db.stickerDao
    }

    @Provides
    @Singleton
    fun provideStickerPackage(db: AppDatabase): StickerPackageDao {
        return db.stickerPackageDao
    }
}