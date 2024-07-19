package com.fstech.myItems.di
/*

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import store.msolapps.data.api.ApiService
import store.msolapps.data.sp.SharedPreferenceImpl
import store.msolapps.domain.utils.Constants
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    @Singleton
    fun provideAuthIntercept(asp: SharedPreferenceImpl): Interceptor {
        return Interceptor { chain ->
            var language = asp.getLang()
            if (language.isEmpty()) {
                language = "en"
            }
            val newRequest: Request = if (asp.getToken().isNotEmpty()) chain.request().newBuilder()
                .addHeader("User-Agent", "android")
                .addHeader("authorization", "Bearer ${asp.getToken()}")
                .addHeader("Accept-Language", language).addHeader("Accept", "application/json")
                .build()
            else chain.request().newBuilder().addHeader("User-Agent", "android")
                .addHeader("Accept-Language", language).addHeader("Accept", "application/json")
                .build()
            chain.proceed(newRequest)
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        logging: HttpLoggingInterceptor, interceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(logging).addInterceptor(interceptor)
            .connectTimeout(80, TimeUnit.SECONDS) // connect timeout
            .readTimeout(80, TimeUnit.SECONDS)
            .writeTimeout(90, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        client: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).client(client).build()
    }

    @Provides
    @Singleton
    fun provideGeneralApiServices(
        retrofit: Retrofit
    ): ApiService {
        return retrofit.create(ApiService::class.java)
    }

}*/
