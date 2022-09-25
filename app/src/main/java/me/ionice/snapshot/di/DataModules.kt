package me.ionice.snapshot.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import me.ionice.snapshot.data.database.SnapshotDatabase
import me.ionice.snapshot.data.database.dao.DayDao
import me.ionice.snapshot.data.database.dao.LocationDao
import me.ionice.snapshot.data.database.dao.TagDao
import me.ionice.snapshot.data.database.dao.UtilsDao
import me.ionice.snapshot.data.database.repository.*
import me.ionice.snapshot.data.network.NetworkRepository
import me.ionice.snapshot.data.network.NetworkRepositoryImpl
import me.ionice.snapshot.data.preferences.PreferencesRepository
import me.ionice.snapshot.data.preferences.PreferencesRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    fun provideDayDao(database: SnapshotDatabase): DayDao = database.dayDao

    @Provides
    fun provideLocationDao(database: SnapshotDatabase): LocationDao = database.locationDao

    @Provides
    fun provideTagDao(database: SnapshotDatabase): TagDao = database.tagDao

    @Provides
    fun provideUtilsDao(database: SnapshotDatabase): UtilsDao = database.utilsDao

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): SnapshotDatabase {
        return SnapshotDatabase.getInstance(appContext)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideDayRepository(
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        dayDao: DayDao,
        locationDao: LocationDao,
        tagDao: TagDao
    ): DayRepository = OfflineDayRepository(ioDispatcher, dayDao, locationDao, tagDao)

    @Provides
    @Singleton
    fun provideLocationRepository(
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        locationDao: LocationDao
    ): LocationRepository = OfflineLocationRepository(ioDispatcher, locationDao)

    @Provides
    @Singleton
    fun provideTagRepository(
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        tagDao: TagDao
    ): TagRepository = OfflineTagRepository(ioDispatcher, tagDao)

    @Provides
    @Singleton
    fun provideNetworkRepository(
        @ApplicationContext context: Context
    ): NetworkRepository = NetworkRepositoryImpl(context)

    @Provides
    @Singleton
    fun providePreferencesRepository(
        @ApplicationContext context: Context
    ): PreferencesRepository = PreferencesRepositoryImpl(context)
}