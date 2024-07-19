package com.fstech.myItems.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {
    /*    @Provides
        fun providesAuthRepository(
            authRemoteDataSource: ApiService,
            authLocalDataSource: AuthSharedPreference
        ): AuthRepository =
            AuthRepositoryImpl(authRemoteDataSource, authLocalDataSource)

        @Provides
        fun providesHomeRepository(
            authRemoteDataSource: ApiService,
            authLocalDataSource: AuthSharedPreference,
            profileSharedPreference: ProfileSharedPreference,
        ): HomeRepository =
            HomeRepositoryImpl(authRemoteDataSource, authLocalDataSource, profileSharedPreference)

        @Provides
        fun providesProductRepository(
            authRemoteDataSource: ApiService
        ): ProductRepository =
            ProductRepositoryImpl(authRemoteDataSource)

        @Provides
        fun providesCartRepository(
            authRemoteDataSource: ApiService,
            authLocalDataSource: AuthSharedPreference,
            profileSharedPreference: ProfileSharedPreference,
        ): CartRepository =
            CartRepositoryImpl(
                authRemoteDataSource,
                authLocalDataSource,
                profileSharedPreference
            )
        @Provides
        fun providesProfileRepository(
            authRemoteDataSource: ApiService
        ): ProfileRepository =
            ProfileRepositoryImpl(authRemoteDataSource)

        @Provides
        fun providesOfferRepository(
            authRemoteDataSource: ApiService
        ): OfferRepository =
            OfferRepositoryImpl(authRemoteDataSource)*/
}