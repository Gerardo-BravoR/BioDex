package com.biodex.app.di

import com.biodex.app.domain.usecase.ValidateSightingUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    fun provideValidateSightingUseCase(): ValidateSightingUseCase = ValidateSightingUseCase()
}