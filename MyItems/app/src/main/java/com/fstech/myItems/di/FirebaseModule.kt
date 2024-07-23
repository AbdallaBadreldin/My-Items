package com.fstech.myItems.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jetawy.data.firebase.FirebaseService
import com.jetawy.data.firebase.FirebaseServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
object FirebaseModule {
    @Provides
    fun providesFirebaseService(
        firebaseSource: FirebaseAuth,
    ): FirebaseService =
        FirebaseServiceImpl(firebaseSource)

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
