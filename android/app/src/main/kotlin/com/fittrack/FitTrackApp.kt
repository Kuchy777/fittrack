package com.fittrack

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.fittrack.notification.NotificationHelper
import com.fittrack.notification.NotificationScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class FitTrackApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        // Lokalne powiadomienia (bez Firebase): kanal + cykliczne przypomnienia.
        NotificationHelper.ensureChannel(this)
        NotificationScheduler.scheduleAll(this)
    }
}
