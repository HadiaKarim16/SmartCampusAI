package com.smartcampus.ai.worker

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.smartcampus.ai.SmartCampusApp
import com.smartcampus.ai.ui.MainActivity
import dagger.assisted.*
import java.util.concurrent.TimeUnit
import android.app.PendingIntent
import android.content.Intent

/**
 * ReminderWorker — WorkManager Worker that fires a local notification
 * for assignment deadlines scheduled by the user.
 */
@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val KEY_TITLE = "notification_title"
        const val KEY_MESSAGE = "notification_message"
        const val KEY_TASK_ID = "task_id"
    }

    override suspend fun doWork(): Result {
        val title = inputData.getString(KEY_TITLE) ?: "Reminder"
        val message = inputData.getString(KEY_MESSAGE) ?: "You have a deadline approaching!"
        val taskId = inputData.getInt(KEY_TASK_ID, 0)

        showNotification(taskId, title, message)
        return Result.success()
    }

    private fun showNotification(taskId: Int, title: String, message: String) {
        val mainIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, taskId, mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, SmartCampusApp.CHANNEL_REMINDERS)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(taskId, notification)
    }
}

// ─────────────────────────────────────────────
//  CLASS ALERT WORKER
// ─────────────────────────────────────────────
@HiltWorker
class ClassAlertWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val KEY_SUBJECT = "subject"
        const val KEY_ROOM = "room"
        const val KEY_START_TIME = "start_time"
    }

    override suspend fun doWork(): Result {
        val subject = inputData.getString(KEY_SUBJECT) ?: "Class"
        val room = inputData.getString(KEY_ROOM) ?: ""
        val startTime = inputData.getString(KEY_START_TIME) ?: ""

        showClassAlert(subject, room, startTime)
        return Result.success()
    }

    private fun showClassAlert(subject: String, room: String, startTime: String) {
        val notification = NotificationCompat.Builder(context, SmartCampusApp.CHANNEL_CLASS_ALERTS)
            .setContentTitle("📚 Class Starting Soon")
            .setContentText("$subject${if (room.isNotEmpty()) " • Room $room" else ""} at $startTime")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(subject.hashCode(), notification)
    }
}
