package com.example.instrumenttrainer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.instrumenttrainer.data.local.dao.PracticeJournalDao
import com.example.instrumenttrainer.data.local.entity.PracticeAttemptEntity
import com.example.instrumenttrainer.data.local.entity.PracticeSessionEntity

@Database(
    entities = [
        PracticeSessionEntity::class,
        PracticeAttemptEntity::class,
    ],
    version = 1,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun practiceJournalDao(): PracticeJournalDao
}
