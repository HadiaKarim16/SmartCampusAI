package com.smartcampus.ai.util

import android.content.Context
import androidx.work.*
import com.smartcampus.ai.worker.ClassAlertWorker
import com.smartcampus.ai.worker.ReminderWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * NotificationHelper — Schedules WorkManager jobs for reminders and class alerts.
 */
@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)

    /**
     * Schedule a one-time reminder notification at a specific time.
     */
    fun scheduleReminder(taskId: Int, title: String, message: String, triggerAt: Long) {
        val delay = triggerAt - System.currentTimeMillis()
        if (delay <= 0) return

        val inputData = workDataOf(
            ReminderWorker.KEY_TASK_ID to taskId,
            ReminderWorker.KEY_TITLE to title,
            ReminderWorker.KEY_MESSAGE to message
        )

        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("reminder_$taskId")
            .build()

        workManager.enqueueUniqueWork(
            "reminder_$taskId",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    /**
     * Cancel a scheduled reminder by task ID.
     */
    fun cancelReminder(taskId: Int) {
        workManager.cancelUniqueWork("reminder_$taskId")
    }

    /**
     * Schedule a class alert to fire X minutes before the class starts.
     */
    fun scheduleClassAlert(classId: Int, subject: String, room: String, startTimeMillis: Long, minutesBefore: Int = 10) {
        val triggerAt = startTimeMillis - (minutesBefore * 60 * 1000L)
        val delay = triggerAt - System.currentTimeMillis()
        if (delay <= 0) return

        val inputData = workDataOf(
            ClassAlertWorker.KEY_SUBJECT to subject,
            ClassAlertWorker.KEY_ROOM to room,
            ClassAlertWorker.KEY_START_TIME to startTimeMillis.toString()
        )

        val request = OneTimeWorkRequestBuilder<ClassAlertWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("class_alert_$classId")
            .build()

        workManager.enqueueUniqueWork(
            "class_alert_$classId",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    /**
     * Cancel all pending work for this application.
     */
    fun cancelAll() {
        workManager.cancelAllWork()
    }
}
