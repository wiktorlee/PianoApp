package com.example.instrumenttrainer.di

import android.content.Context
import androidx.room.Room
import com.example.instrumenttrainer.data.local.AppDatabase
import com.example.instrumenttrainer.data.local.dao.PracticeJournalDao
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "instrument_trainer.db",
        ).build()
    }

    @Provides
    fun providePracticeJournalDao(database: AppDatabase): PracticeJournalDao {
        return database.practiceJournalDao()
    }
}
