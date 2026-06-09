package com.fittrack.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.fittrack.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class WaterReminderWorker @AssistedInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        showWaterReminder()
        return Result.success()
    }

    private fun showWaterReminder() {
        val channelId = "water_reminders"
        val nm = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(
            NotificationChannel(channelId, "Przypomnienia o wodzie", NotificationManager.IMPORTANCE_DEFAULT)
        )
        val n = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_water)
            .setContentTitle("💧 Czas na wodę!")
            .setContentText("Pamiętaj o odpowiednim nawodnieniu organizmu.")
            .setAutoCancel(true)
            .build()
        nm.notify(42, n)
    }

    companion object {
        // Planuje codzienne przypomnienia: 10:00, 14:00, 18:00
        fun schedule(context: Context) {
            val workManager = WorkManager.getInstance(context)
            listOf(10, 14, 18).forEachIndexed { i, hour ->
                val delay = calculateDelayUntil(hour)
                val request = PeriodicWorkRequestBuilder<WaterReminderWorker>(24, TimeUnit.HOURS)
                    .setInitialDelay(delay, TimeUnit.MINUTES)
                    .addTag("water_reminder_$hour")
                    .build()
                workManager.enqueueUniquePeriodicWork(
                    "water_$hour", ExistingPeriodicWorkPolicy.UPDATE, request)
            }
        }

        private fun calculateDelayUntil(targetHour: Int): Long {
            val now = java.time.LocalTime.now()
            val target = java.time.LocalTime.of(targetHour, 0)
            val minutes = if (now.isBefore(target))
                java.time.Duration.between(now, target).toMinutes()
            else
                java.time.Duration.between(now, target.plusHours(24)).toMinutes()
            return minutes
        }
    }
}
