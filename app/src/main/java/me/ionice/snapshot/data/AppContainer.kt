package me.ionice.snapshot.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import me.ionice.snapshot.data.database.SnapshotDatabase
import me.ionice.snapshot.data.database.repository.*
import me.ionice.snapshot.data.network.NetworkRepository
import me.ionice.snapshot.data.network.NetworkRepositoryImpl
import me.ionice.snapshot.data.preferences.PreferencesRepository
import me.ionice.snapshot.data.preferences.PreferencesRepositoryImpl

interface AppContainer {
    val dayRepository: DayRepository
    val locationRepository: LocationRepository
    val tagRepository: TagRepository
    val networkRepository: NetworkRepository
    val preferencesRepository: PreferencesRepository
}

class AppContainerImpl(private val applicationContext: Context) : AppContainer {

    private val Context.datastore by preferencesDataStore(name = "snapshot_preferences")
    private val dispatcher = Dispatchers.IO

    val database = SnapshotDatabase.getInstance(applicationContext)

    override val dayRepository: DayRepository by lazy {
        OfflineDayRepository(dispatcher, database.dayDao, database.locationDao, database.tagDao)
    }

    override val locationRepository: LocationRepository by lazy {
        OfflineLocationRepository(dispatcher, database.locationDao)
    }

    override val tagRepository: TagRepository by lazy {
        OfflineTagRepository(dispatcher, database.tagDao)
    }

    override val networkRepository: NetworkRepository by lazy {
        NetworkRepositoryImpl(applicationContext)
    }

    override val preferencesRepository: PreferencesRepository by lazy {
        PreferencesRepositoryImpl(applicationContext, applicationContext.datastore)
    }
}