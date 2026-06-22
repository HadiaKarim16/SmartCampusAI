package com.smartcampus.ai.util

import java.text.SimpleDateFormat
import java.util.*

// ─────────────────────────────────────────────
//  DATE / TIME EXTENSIONS
// ─────────────────────────────────────────────
fun Long.toFormattedDate(pattern: String = "MMM dd, yyyy"): String =
    SimpleDateFormat(pattern, Locale.getDefault()).format(Date(this))

fun Long.toFormattedDateTime(): String =
    SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(this))

fun Long.toRelativeTime(): String {
    val now = System.currentTimeMillis()
    val diff = this - now
    val absDiff = Math.abs(diff)
    val isPast = diff < 0

    return when {
        absDiff < 60_000 -> "just now"
        absDiff < 3_600_000 -> "${absDiff / 60_000} min ${if (isPast) "ago" else "left"}"
        absDiff < 86_400_000 -> "${absDiff / 3_600_000} hr ${if (isPast) "ago" else "left"}"
        absDiff < 604_800_000 -> "${absDiff / 86_400_000} day${if (absDiff / 86_400_000 > 1) "s" else ""} ${if (isPast) "ago" else "left"}"
        else -> toFormattedDate()
    }
}

fun Long.startOfDay(): Long {
    val cal = Calendar.getInstance().apply {
        timeInMillis = this@startOfDay
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis
}

fun Long.endOfDay(): Long {
    val cal = Calendar.getInstance().apply {
        timeInMillis = this@endOfDay
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }
    return cal.timeInMillis
}

// ─────────────────────────────────────────────
//  STRING EXTENSIONS
// ─────────────────────────────────────────────
fun String.toTimeMillis(pattern: String = "HH:mm"): Long? {
    return try {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        val cal = Calendar.getInstance()
        val parsed = sdf.parse(this) ?: return null
        val timeCal = Calendar.getInstance().apply { time = parsed }
        cal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY))
        cal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE))
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.timeInMillis
    } catch (e: Exception) { null }
}

fun Int.toMinutesString(): String =
    if (this >= 60) "${this / 60}h ${this % 60}m" else "${this}m"

// ─────────────────────────────────────────────
//  VALIDATION HELPERS
// ─────────────────────────────────────────────
fun String.isValidEmail(): Boolean =
    android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isValidPassword(): Boolean = this.length >= 6

fun String.isValidTime(): Boolean =
    Regex("^([01]\\d|2[0-3]):[0-5]\\d$").matches(this)
