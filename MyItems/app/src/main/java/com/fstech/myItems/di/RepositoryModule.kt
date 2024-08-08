package com.fstech.myItems.di

import com.jetawy.data.firebase.FirebaseAuthService
import com.jetawy.data.firebase.FirebaseChatService
import com.jetawy.data.firebase.FirebaseDataBaseService
import com.jetawy.data.repositories.AccountSettingsRepositoryImpl
import com.jetawy.data.repositories.ChatRepositoryImpl
import com.jetawy.data.repositories.FoundItemsRepositoryImpl
import com.jetawy.data.repositories.LostItemsRepositoryImpl
import com.jetawy.domain.repository.AccountSettingsRepository
import com.jetawy.domain.repository.ChatRepository
import com.jetawy.domain.repository.FoundItemsRepository
import com.jetawy.domain.repository.LostItemsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {
    @Provides
    fun providesFoundItemsRepo(fbs: FirebaseDataBaseService): FoundItemsRepository =
        FoundItemsRepositoryImpl(fbs)

    @Provides
    fun providesLostItemsRepo(fbs: FirebaseDataBaseService): LostItemsRepository =
        LostItemsRepositoryImpl(fbs)

    @Provides
    fun providesChatRepo(focis: FirebaseChatService): ChatRepository = ChatRepositoryImpl(focis)

    @Provides
    fun providesSettingsRepo(fbs: FirebaseAuthService): AccountSettingsRepository =
        AccountSettingsRepositoryImpl(fbs)
}