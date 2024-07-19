package store.msolapps.flamingo.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import store.msolapps.domain.repository.AuthRepository
import store.msolapps.domain.repository.CartRepository
import store.msolapps.domain.repository.HomeRepository
import store.msolapps.domain.useCase.auth.IsPhoneTakenUseCase
import store.msolapps.domain.useCase.auth.LoginUseCase
import store.msolapps.domain.useCase.auth.RegisterUseCase
import store.msolapps.domain.useCase.cart.AddProductToCartUseCase
import store.msolapps.domain.useCase.cart.AddProductToFavouriteUseCase
import store.msolapps.domain.useCase.cart.CreateOrderUseCase
import store.msolapps.domain.useCase.cart.GetCartUseCase
import store.msolapps.domain.useCase.cart.GetDefaultAddressUseCase
import store.msolapps.domain.useCase.cart.GetPaidTypesUseCase
import store.msolapps.domain.useCase.cart.GetPaymentMethodUseCase
import store.msolapps.domain.useCase.cart.GetSlotsUseCase
import store.msolapps.domain.useCase.home.GetCategoriesUseCase
import store.msolapps.domain.useCase.home.GetHomeBannersUseCase
import store.msolapps.domain.useCase.home.GetSpecialCategoriesUseCase

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    @ViewModelScoped
    fun provideLoginUseCase(
        repo: AuthRepository
    ): LoginUseCase {
        return LoginUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideIsPhoneTakenUseCase(
        repo: AuthRepository
    ): IsPhoneTakenUseCase {
        return IsPhoneTakenUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideRegisterUseCase(
        repo: AuthRepository
    ): RegisterUseCase {
        return RegisterUseCase(repo)
    }

    //home repository
    @Provides
    @ViewModelScoped
    fun provideGetHomeCategoriesUseCase(
        repo: HomeRepository
    ): GetCategoriesUseCase {
        return GetCategoriesUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetHomeBannersUseCase(
        repo: HomeRepository
    ): GetHomeBannersUseCase {
        return GetHomeBannersUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideGetSpecialCategoriesUseCase(
        repo: HomeRepository
    ): GetSpecialCategoriesUseCase {
        return GetSpecialCategoriesUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideAddProductToCartUseCase(
        repo: CartRepository
    ): AddProductToCartUseCase {
        return AddProductToCartUseCase(repo)
    }

    @Provides
    @ViewModelScoped
    fun provideAddProductToFavouriteUseCase(
        repo: CartRepository
    ): AddProductToFavouriteUseCase {
        return AddProductToFavouriteUseCase(repo)
    }
    @Provides
    @ViewModelScoped
    fun provideGetCarteUseCase(
        repo: CartRepository
    ): GetCartUseCase {
        return GetCartUseCase(repo)
    }
    @Provides
    @ViewModelScoped
    fun provideCreateOrderUseCase(
        repo: CartRepository
    ): CreateOrderUseCase {
        return CreateOrderUseCase(repo)
    }
    @Provides
    @ViewModelScoped
    fun provideGetDefaultAddressUseCase(
        repo: CartRepository
    ): GetDefaultAddressUseCase {
        return GetDefaultAddressUseCase(repo)
    }
     @Provides
    @ViewModelScoped
    fun provideGetPaidTypesUseCase(
        repo: CartRepository
    ): GetPaidTypesUseCase {
        return GetPaidTypesUseCase(repo)
    }
     @Provides
    @ViewModelScoped
    fun provideGetPaymentMethodUseCase(
        repo: CartRepository
    ): GetPaymentMethodUseCase {
        return GetPaymentMethodUseCase(repo)
    }
    @Provides
    @ViewModelScoped
    fun provideGetSlotsUseCase(
        repo: CartRepository
    ): GetSlotsUseCase {
        return GetSlotsUseCase(repo)
    }


}