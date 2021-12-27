package com.example.data.features.repos.module

import com.example.data.features.repos.apiservice.RepoApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ReposModule {

    @Provides
    @Singleton
    internal fun provideRepoApiService(retrofit: Retrofit): RepoApiService {
        retrofit.newBuilder().baseUrl("")
        return retrofit.create(RepoApiService::class.java)
    }
}