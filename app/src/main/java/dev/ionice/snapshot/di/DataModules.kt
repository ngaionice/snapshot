package dev.ionice.snapshot.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.ionice.snapshot.core.database.dao.DayDao
import dev.ionice.snapshot.core.database.dao.LocationDao
import dev.ionice.snapshot.core.database.dao.TagDao
import dev.ionice.snapshot.data.backup.BackupModule
import dev.ionice.snapshot.data.backup.BackupRepository
import dev.ionice.snapshot.data.backup.GDriveBackupModule
import dev.ionice.snapshot.data.backup.GDriveBackupRepository
import dev.ionice.snapshot.data.database.repository.*
import dev.ionice.snapshot.data.preferences.OfflinePreferencesRepository
import dev.ionice.snapshot.data.preferences.PreferencesRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

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
    fun provideBackupRepository(
        @ApplicationContext context: Context
    ): BackupRepository = GDriveBackupRepository(context)

    @Provides
    @Singleton
    fun providePreferencesRepository(
        @ApplicationContext context: Context
    ): PreferencesRepository = OfflinePreferencesRepository(context)
}

@Module
@InstallIn(SingletonComponent::class)
object ServiceProviderModule {

    @Provides
    @Singleton
    fun provideBackupModule(
        @ApplicationContext context: Context
    ): BackupModule = GDriveBackupModule(context)
}