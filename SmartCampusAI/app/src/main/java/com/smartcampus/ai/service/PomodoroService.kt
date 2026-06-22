package com.smartcampus.ai.service

import android.app.*
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.smartcampus.ai.SmartCampusApp
import com.smartcampus.ai.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

/**
 * PomodoroService — Foreground service that keeps the Pomodoro timer
 * running even when the app is in the background.
 */
@AndroidEntryPoint
class PomodoroService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var timerJob: Job? = null
    private var timeLeftSeconds = 0
    private var isPaused = false

    companion object {
        const val NOTIFICATION_ID = 1001
        const val ACTION_START = "action_start"
        const val ACTION_PAUSE = "action_pause"
        const val ACTION_RESUME = "action_resume"
        const val ACTION_STOP = "action_stop"
        const val EXTRA_DURATION = "extra_duration"

        const val ACTION_TICK = "com.smartcampus.ai.TIMER_TICK"
        const val EXTRA_TIME_LEFT = "time_left"
        const val EXTRA_FINISHED = "finished"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                timeLeftSeconds = intent.getIntExtra(EXTRA_DURATION, 25 * 60)
                isPaused = false
                startForeground(NOTIFICATION_ID, buildNotification(timeLeftSeconds))
                startTicking()
            }
            ACTION_PAUSE -> {
                isPaused = true
                updateNotification(timeLeftSeconds, paused = true)
            }
            ACTION_RESUME -> {
                isPaused = false
                updateNotification(timeLeftSeconds, paused = false)
                startTicking()
            }
            ACTION_STOP -> {
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun startTicking() {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (timeLeftSeconds > 0 && !isPaused) {
                delay(1000L)
                if (!isPaused) {
                    timeLeftSeconds--
                    broadcastTick(timeLeftSeconds)
                    updateNotification(timeLeftSeconds)
                }
            }
            if (timeLeftSeconds == 0) {
                broadcastTick(0, finished = true)
                showCompletionNotification()
                stopSelf()
            }
        }
    }

    private fun broadcastTick(timeLeft: Int, finished: Boolean = false) {
        sendBroadcast(Intent(ACTION_TICK).apply {
            putExtra(EXTRA_TIME_LEFT, timeLeft)
            putExtra(EXTRA_FINISHED, finished)
        })
    }

    private fun buildNotification(timeLeft: Int, paused: Boolean = false): Notification {
        val minutes = timeLeft / 60
        val seconds = timeLeft % 60
        val timeStr = String.format("%02d:%02d", minutes, seconds)

        val mainIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, PomodoroService::class.java).apply { action = ACTION_STOP }
        val stopPending = PendingIntent.getService(
            this, 1, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val pauseResumeIntent = Intent(this, PomodoroService::class.java).apply {
            action = if (paused) ACTION_RESUME else ACTION_PAUSE
        }
        val pauseResumePending = PendingIntent.getService(
            this, 2, pauseResumeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, SmartCampusApp.CHANNEL_POMODORO)
            .setContentTitle(if (paused) "⏸ Focus Timer Paused" else "🍅 Focus Session Active")
            .setContentText(timeStr)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .addAction(android.R.drawable.ic_media_pause, if (paused) "Resume" else "Pause", pauseResumePending)
            .addAction(android.R.drawable.ic_delete, "Stop", stopPending)
            .setProgress(timeLeftSeconds, timeLeft, false)
            .build()
    }

    private fun updateNotification(timeLeft: Int, paused: Boolean = false) {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, buildNotification(timeLeft, paused))
    }

    private fun showCompletionNotification() {
        val manager = getSystemService(NotificationManager::class.java)
        val mainIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, SmartCampusApp.CHANNEL_REMINDERS)
            .setContentTitle("🎉 Focus Session Complete!")
            .setContentText("Great job! Time for a well-deserved break.")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        manager.notify(NOTIFICATION_ID + 1, notification)
    }

    override fun onDestroy() {
        timerJob?.cancel()
        serviceScope.cancel()
        super.onDestroy()
    }
}
