package com.example.data.di

import com.example.data.MathSolverRepositoryImpl
import com.example.domain.data.repository.MathSolverRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {


    @Binds
    @Singleton
    abstract fun bindMathSolverRepository(
        impl: MathSolverRepositoryImpl
    ): MathSolverRepository
}