package dev.ionice.snapshot.testtools.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import dev.ionice.snapshot.core.database.DatabaseModule
import dev.ionice.snapshot.core.database.SnapshotDatabase
import dev.ionice.snapshot.core.database.dao.DayDao
import dev.ionice.snapshot.core.database.dao.LocationDao
import dev.ionice.snapshot.core.database.dao.TagDao
import dev.ionice.snapshot.core.database.dao.UtilsDao
import dev.ionice.snapshot.core.data.repository.PreferencesRepository
import dev.ionice.snapshot.core.data.repository.RepositoryModule
import dev.ionice.snapshot.core.data.repository.ServiceProviderModule
import dev.ionice.snapshot.sync.BackupModule
import dev.ionice.snapshot.sync.BackupRepository
import dev.ionice.snapshot.testtools.data.backup.FakeBackupModule
import dev.ionice.snapshot.testtools.data.database.repository.FakeDayRepository
import dev.ionice.snapshot.testtools.data.database.repository.FakeLocationRepository
import dev.ionice.snapshot.testtools.data.database.repository.FakeTagRepository
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
    fun provideDayRepository(): dev.ionice.snapshot.core.data.repository.DayRepository = FakeDayRepository()

    @Provides
    @Singleton
    fun provideLocationRepository(): dev.ionice.snapshot.core.data.repository.LocationRepository = FakeLocationRepository()

    @Provides
    @Singleton
    fun provideTagRepository(): dev.ionice.snapshot.core.data.repository.TagRepository = FakeTagRepository()

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