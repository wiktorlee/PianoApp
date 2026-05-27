package com.example.instrumenttrainer.di

import com.example.instrumenttrainer.data.classifier.MockNoteClassifier
import com.example.instrumenttrainer.data.classifier.TfliteNoteClassifier
import com.example.instrumenttrainer.domain.classifier.NoteClassifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ClassifierModule {

    @Provides
    @Singleton
    fun provideNoteClassifier(
        tfliteNoteClassifier: TfliteNoteClassifier,
        mockNoteClassifier: MockNoteClassifier,
    ): NoteClassifier {
        return if (tfliteNoteClassifier.isAvailable) {
            tfliteNoteClassifier
        } else {
            mockNoteClassifier
        }
    }
}
