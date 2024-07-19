package store.msolapps.flamingo.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import store.msolapps.data.sp.AuthSharedPreference
import store.msolapps.data.sp.ProfileSharedPreference
import store.msolapps.data.sp.SharedPreferenceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SharedPreferenceModule {
    private const val sharedPreferencesFileName = "SETTINGS"
/*
    @Provides
    @Singleton
    fun provideSharedPreference(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(sharedPreferencesFileName, Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideAuthSP(sp: SharedPreferences): AuthSharedPreference =
        SharedPreferenceImpl(sp)

    @Provides
    @Singleton
    fun provideProfileSP(sp: SharedPreferences): ProfileSharedPreference =
        SharedPreferenceImpl(sp)*/

}