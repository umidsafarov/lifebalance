package com.gmail.umidsafarov.lifebalance.di

import android.content.Context
import androidx.room.Room
import com.gmail.umidsafarov.lifebalance.data.local.LifeBalanceDAO
import com.gmail.umidsafarov.lifebalance.data.local.LifeBalanceDatabase
import com.gmail.umidsafarov.lifebalance.data.repository.LifeBalanceRepositoryImpl
import com.gmail.umidsafarov.lifebalance.domain.repository.LifeBalanceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
class TestAppModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext app: Context
    ) = Room.inMemoryDatabaseBuilder(app, LifeBalanceDatabase::class.java)
        .allowMainThreadQueries().build()

    @Singleton
    @Provides
    fun provideLifeBalanceDao(db: LifeBalanceDatabase) = db.dao()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Singleton
    @Provides
    fun provideLifeBalanceRepository(
        dao: LifeBalanceDAO,
    ): LifeBalanceRepository = LifeBalanceRepositoryImpl(dao, UnconfinedTestDispatcher())

}