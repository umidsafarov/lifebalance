package com.gmail.umidsafarov.lifebalance.di

import android.content.Context
import androidx.room.Room
import com.gmail.umidsafarov.lifebalance.BuildConfig
import com.gmail.umidsafarov.lifebalance.data.local.LifeBalanceDAO
import com.gmail.umidsafarov.lifebalance.data.local.LifeBalanceDatabase
import com.gmail.umidsafarov.lifebalance.data.repository.LifeBalanceRepositoryImpl
import com.gmail.umidsafarov.lifebalance.domain.repository.LifeBalanceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        LifeBalanceDatabase::class.java,
        BuildConfig.DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideLifeBalanceDao(db: LifeBalanceDatabase) = db.dao()

    @Singleton
    @Provides
    fun provideLifeBalanceRepository(
        dao: LifeBalanceDAO,
    ): LifeBalanceRepository = LifeBalanceRepositoryImpl(dao)

}