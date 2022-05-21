package me.ionice.snapshot

import android.app.Application
import me.ionice.snapshot.data.AppContainer
import me.ionice.snapshot.data.AppContainerImpl

class SnapshotApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
    }
}