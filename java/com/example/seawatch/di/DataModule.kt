package com.example.seawatch

import android.content.Context
import com.example.seawatch.data.PlacesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Singleton
    @Provides
    fun provideSettingsRepository(@ApplicationContext context: Context) = SettingsRepository(context)

    @Singleton
    @Provides
    fun providePlacesRepository(@ApplicationContext context: Context) =
        PlacesRepository((context.applicationContext as SWApplication).database.placesDAO())

}