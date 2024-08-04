package com.fstech.myItems.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.jetawy.data.firebase.FirebaseAuthService
import com.jetawy.data.firebase.FirebaseAuthServiceImpl
import com.jetawy.data.firebase.FirebaseChatService
import com.jetawy.data.firebase.FirebaseChatServiceImpl
import com.jetawy.data.firebase.FirebaseDataBaseService
import com.jetawy.data.firebase.FirebaseDataBaseServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object FirebaseModule {
    @Provides
    fun providesFirebaseAuthService(
        firebaseSource: FirebaseAuth,
    ): FirebaseAuthService =
        FirebaseAuthServiceImpl(firebaseSource)

    @Provides
    fun providesFirebaseService(
        firebaseStorage: FirebaseStorage,
        firebaseDatabase: FirebaseDatabase,
    ): FirebaseDataBaseService =
        FirebaseDataBaseServiceImpl(firebaseStorage, firebaseDatabase)

    @Provides
    fun providesFirebaseChatService(
        firebaseDatabase: FirebaseDatabase,
    ): FirebaseChatService =
        FirebaseChatServiceImpl( firebaseDatabase)

    @Provides
    fun providesFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Provides
    fun providesFirebaseDatabase() = Firebase.database

    @Provides
    fun providesFirebaseStorage() = Firebase.storage

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
