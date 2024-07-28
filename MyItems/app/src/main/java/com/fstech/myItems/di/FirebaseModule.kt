package com.fstech.myItems.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jetawy.data.firebase.FirebaseAuthService
import com.jetawy.data.firebase.FirebaseAuthServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object FirebaseModule {
    @Provides
    fun providesFirebaseService(
        firebaseSource: FirebaseAuth,
    ): FirebaseAuthService =
        FirebaseAuthServiceImpl(firebaseSource)

    @Provides
    fun providesFirebaseAuth(): FirebaseAuth =Firebase.auth

    /* @Provides
     @Singleton
     fun provideSharedPreference(@ApplicationContext context: Context): Firebase =
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
