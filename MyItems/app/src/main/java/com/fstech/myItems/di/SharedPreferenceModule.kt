package store.msolapps.flamingo.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

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