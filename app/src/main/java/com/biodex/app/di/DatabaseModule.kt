package com.biodex.app.di

import android.content.Context
import androidx.room.Room
import com.biodex.app.data.local.dao.SightingDao
import com.biodex.app.data.local.db.BioDexDatabase
import com.biodex.app.data.repository.SightingRepositoryImpl
import com.biodex.app.domain.repository.SightingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): BioDexDatabase =
        Room.databaseBuilder(context, BioDexDatabase::class.java, "biodex.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideSightingDao(db: BioDexDatabase): SightingDao = db.sightingDao()

    @Provides
    fun provideSightingRepository(impl: SightingRepositoryImpl): SightingRepository = impl
}
