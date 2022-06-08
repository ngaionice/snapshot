package me.ionice.snapshot.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import me.ionice.snapshot.data.day.DayRepository
import me.ionice.snapshot.data.day.DayRepositoryImpl
import me.ionice.snapshot.data.metric.MetricRepository
import me.ionice.snapshot.data.metric.MetricRepositoryImpl
import me.ionice.snapshot.data.network.NetworkRepository
import me.ionice.snapshot.data.network.NetworkRepositoryImpl
import me.ionice.snapshot.data.preferences.PreferencesRepository
import me.ionice.snapshot.data.preferences.PreferencesRepositoryImpl

interface AppContainer {
    val dayRepository: DayRepository
    val metricRepository: MetricRepository
    val networkRepository: NetworkRepository
    val preferencesRepository: PreferencesRepository
}

class AppContainerImpl(private val applicationContext: Context) : AppContainer {

    private val Context.datastore by preferencesDataStore(name = "snapshot_preferences")

    override val dayRepository: DayRepository by lazy {
        DayRepositoryImpl(SnapshotDatabase.getInstance(applicationContext))
    }

    override val metricRepository: MetricRepository by lazy {
        MetricRepositoryImpl(SnapshotDatabase.getInstance(applicationContext))
    }

    override val networkRepository: NetworkRepository by lazy {
        NetworkRepositoryImpl(applicationContext)
    }

    override val preferencesRepository: PreferencesRepository by lazy {
        PreferencesRepositoryImpl(applicationContext.datastore)
    }
}