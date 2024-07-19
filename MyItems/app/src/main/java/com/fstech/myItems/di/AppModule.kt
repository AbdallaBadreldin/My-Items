package com.fstech.myItems.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import store.msolapps.flamingo.MyBaseApp
import store.msolapps.flamingo.util.ConnectionStatus

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideIsConnectionStatusContext(@ApplicationContext context: Context): ConnectionStatus {
        return ConnectionStatus(context = context)
    }

}