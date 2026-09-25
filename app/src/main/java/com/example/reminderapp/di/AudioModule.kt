package com.example.reminderapp.di

import android.content.Context
import com.example.reminderapp.core.audio.ReminderSoundPlayer
import com.example.reminderapp.core.audio.SoundPoolReminderSoundPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AudioModule {

    @Provides
    @Singleton
    fun provideReminderSoundPlayer(
        @ApplicationContext context: Context
    ) : ReminderSoundPlayer {
        return SoundPoolReminderSoundPlayer(context)
    }
}