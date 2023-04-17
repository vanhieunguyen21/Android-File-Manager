package com.ryan.filemanager.di

import android.content.Context
import com.ryan.filemanager.app.BaseApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideApplication(@ApplicationContext context: Context): BaseApplication {
        return context as BaseApplication
    }
}