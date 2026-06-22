package com.smartcampus.ai

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * SmartCampusApp - Main Application class
 * Initializes Hilt DI, WorkManager, and Notification Channels
 */
@HiltAndroidApp
class SmartCampusApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    CHANNEL_REMINDERS,
                    "Task Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Reminders for assignments and deadlines"
                    enableVibration(true)
                },
                NotificationChannel(
                    CHANNEL_POMODORO,
                    "Pomodoro Timer",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Focus timer notifications"
                },
                NotificationChannel(
                    CHANNEL_CLASS_ALERTS,
                    "Class Alerts",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Upcoming class schedule notifications"
                    enableVibration(true)
                },
                NotificationChannel(
                    CHANNEL_ATTENDANCE,
                    "Attendance Alerts",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Low attendance warnings"
                }
            )
            val manager = getSystemService(NotificationManager::class.java)
            channels.forEach { manager.createNotificationChannel(it) }
        }
    }

    companion object {
        const val CHANNEL_REMINDERS = "channel_reminders"
        const val CHANNEL_POMODORO = "channel_pomodoro"
        const val CHANNEL_CLASS_ALERTS = "channel_class_alerts"
        const val CHANNEL_ATTENDANCE = "channel_attendance"
    }
}
