package com.example.instrumenttrainer.di

import com.example.instrumenttrainer.data.repository.AppSettingsRepositoryImpl
import com.example.instrumenttrainer.data.repository.AudioPracticeRepositoryImpl
import com.example.instrumenttrainer.data.repository.PracticeJournalRepositoryImpl
import com.example.instrumenttrainer.domain.repository.AppSettingsRepository
import com.example.instrumenttrainer.domain.repository.AudioPracticeRepository
import com.example.instrumenttrainer.domain.repository.PracticeJournalRepository
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

    @Binds
    @Singleton
    abstract fun bindPracticeJournalRepository(
        impl: PracticeJournalRepositoryImpl,
    ): PracticeJournalRepository

    @Binds
    @Singleton
    abstract fun bindAppSettingsRepository(
        impl: AppSettingsRepositoryImpl,
    ): AppSettingsRepository
}
