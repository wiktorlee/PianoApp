package com.example.instrumenttrainer.di

import com.example.instrumenttrainer.data.repository.AudioPracticeRepositoryImpl
import com.example.instrumenttrainer.domain.repository.AudioPracticeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAudioPracticeRepository(
        impl: AudioPracticeRepositoryImpl,
    ): AudioPracticeRepository
}
