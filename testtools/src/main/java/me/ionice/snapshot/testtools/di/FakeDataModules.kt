package me.ionice.snapshot.testtools.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import me.ionice.snapshot.data.backup.BackupModule
import me.ionice.snapshot.data.database.SnapshotDatabase
import me.ionice.snapshot.data.database.dao.DayDao
import me.ionice.snapshot.data.database.dao.LocationDao
import me.ionice.snapshot.data.database.dao.TagDao
import me.ionice.snapshot.data.database.dao.UtilsDao
import me.ionice.snapshot.data.database.repository.*
import me.ionice.snapshot.data.backup.BackupRepository
import me.ionice.snapshot.data.preferences.PreferencesRepository
import me.ionice.snapshot.di.DatabaseModule
import me.ionice.snapshot.di.RepositoryModule
import me.ionice.snapshot.di.ServiceProviderModule
import me.ionice.snapshot.testtools.data.backup.FakeBackupModule
import me.ionice.snapshot.testtools.data.database.repository.FakeDayRepository
import me.ionice.snapshot.testtools.data.database.repository.FakeLocationRepository
import me.ionice.snapshot.testtools.data.database.repository.FakeTagRepository
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
object DatabaseMockModule {

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
        return Room
            .inMemoryDatabaseBuilder(appContext, SnapshotDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }
}

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
object RepositoryMockModule {

    @Provides
    @Singleton
    fun provideDayRepository(): DayRepository = FakeDayRepository()

    @Provides
    @Singleton
    fun provideLocationRepository(): LocationRepository = FakeLocationRepository()

    @Provides
    @Singleton
    fun provideTagRepository(): TagRepository = FakeTagRepository()

    @Provides
    @Singleton
    fun provideNetworkRepository(): BackupRepository = TODO()

    @Provides
    @Singleton
    fun providePreferencesRepository(): PreferencesRepository = TODO()
}

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [ServiceProviderModule::class]
)
object ServiceProviderMockModule {

    @Provides
    @Singleton
    fun provideBackupModule(
        @ApplicationContext context: Context
    ): BackupModule = FakeBackupModule
}