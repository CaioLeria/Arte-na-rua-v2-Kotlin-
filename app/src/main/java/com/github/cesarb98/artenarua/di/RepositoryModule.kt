package com.github.cesarb98.artenarua.di

import com.github.cesarb98.artenarua.repository.ObraRepository
import com.github.cesarb98.artenarua.repository.ObraRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindObraRepository(impl: ObraRepositoryImpl): ObraRepository
}
