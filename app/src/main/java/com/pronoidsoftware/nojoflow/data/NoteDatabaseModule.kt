package com.pronoidsoftware.nojoflow.data

import android.content.Context
import androidx.room.Room
import com.pronoidsoftware.nojoflow.domain.LocalNoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NoteDatabaseModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(@ApplicationContext app: Context): NoteDatabase {
        return Room.databaseBuilder(
            app,
            NoteDatabase::class.java,
            NoteDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideNoteDao(database: NoteDatabase): NoteDao {
        return database.noteDao
    }

    @Provides
    @Singleton
    fun provideLocalDataSource(noteDao: NoteDao): LocalNoteDataSource {
        return RoomLocalNoteDataSource(noteDao)
    }
}