package com.example.reminderapp.di

import android.content.Context
import androidx.room.Room
import com.example.reminderapp.data.db.ReminderDao
import com.example.reminderapp.data.db.ReminderDatabase
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
    fun provideDatabase(@ApplicationContext context: Context): ReminderDatabase =
        Room.databaseBuilder(
            context,
            ReminderDatabase::class.java,
            "reminders.db"
        )
            .build()

    @Provides
    fun provideReminderDao(db: ReminderDatabase): ReminderDao = db.reminderDao()
}
