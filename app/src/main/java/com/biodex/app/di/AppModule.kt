package com.biodex.app.di

import android.content.ContentResolver
import android.content.Context
import com.biodex.app.core.SightingFileManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideFileManager(@ApplicationContext ctx: Context) = SightingFileManager(ctx)

    @Provides
    fun provideContentResolver(@ApplicationContext ctx: Context): ContentResolver = ctx.contentResolver
}