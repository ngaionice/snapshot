package me.ionice.snapshot.data

import android.content.Context
import me.ionice.snapshot.data.day.DayRepository
import me.ionice.snapshot.data.day.DayRepositoryImpl
import me.ionice.snapshot.data.metric.MetricRepository
import me.ionice.snapshot.data.metric.MetricRepositoryImpl
import me.ionice.snapshot.data.network.NetworkRepository
import me.ionice.snapshot.data.network.NetworkRepositoryImpl

interface AppContainer {
    val dayRepository: DayRepository
    val metricRepository: MetricRepository
    val networkRepository: NetworkRepository
}

class AppContainerImpl(private val applicationContext: Context) : AppContainer {

    override val dayRepository: DayRepository by lazy {
        DayRepositoryImpl(SnapshotDatabase.getInstance(applicationContext))
    }

    override val metricRepository: MetricRepository by lazy {
        MetricRepositoryImpl(SnapshotDatabase.getInstance(applicationContext))
    }

    override val networkRepository: NetworkRepository by lazy {
        NetworkRepositoryImpl(applicationContext)
    }
}