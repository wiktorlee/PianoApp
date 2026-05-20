package com.example.instrumenttrainer.di

import com.example.instrumenttrainer.data.classifier.MockNoteClassifier
import com.example.instrumenttrainer.domain.classifier.NoteClassifier
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ClassifierModule {

    @Binds
    @Singleton
    abstract fun bindNoteClassifier(impl: MockNoteClassifier): NoteClassifier
}
