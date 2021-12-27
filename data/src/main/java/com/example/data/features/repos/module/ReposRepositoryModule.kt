package com.example.data.features.repos.module

import com.example.data.features.repos.ReposRepository
import com.example.data.features.repos.ReposRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class ReposRepositoryModule {

    @Binds
    internal abstract fun bindsRepository(instance: ReposRepositoryImpl): ReposRepository
}