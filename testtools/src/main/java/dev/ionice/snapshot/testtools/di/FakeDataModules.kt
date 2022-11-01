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
import dev.ionice.snapshot.data.backup.BackupModule
import dev.ionice.snapshot.data.backup.BackupRepository
import dev.ionice.snapshot.data.database.repository.DayRepository
import dev.ionice.snapshot.data.database.repository.LocationRepository
import dev.ionice.snapshot.data.database.repository.TagRepository
import dev.ionice.snapshot.data.preferences.PreferencesRepository
import dev.ionice.snapshot.di.RepositoryModule
import dev.ionice.snapshot.di.ServiceProviderModule
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