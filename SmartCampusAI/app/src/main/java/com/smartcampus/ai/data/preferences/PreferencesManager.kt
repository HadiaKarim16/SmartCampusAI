package com.smartcampus.ai.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "smart_campus_prefs")

/**
 * PreferencesManager - Handles all app-wide preferences via DataStore
 */
@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_ID = intPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val POMODORO_DURATION = intPreferencesKey("pomodoro_duration")
        val BREAK_DURATION = intPreferencesKey("break_duration")
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val ATTENDANCE_THRESHOLD = floatPreferencesKey("attendance_threshold")
        val REMINDER_BEFORE_MINUTES = intPreferencesKey("reminder_before_minutes")
    }

    val isLoggedIn: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[IS_LOGGED_IN] ?: false }

    val userId: Flow<Int> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[USER_ID] ?: -1 }

    val userName: Flow<String> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[USER_NAME] ?: "" }

    val userEmail: Flow<String> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[USER_EMAIL] ?: "" }

    val isDarkMode: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[IS_DARK_MODE] ?: true }

    val notificationsEnabled: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[NOTIFICATIONS_ENABLED] ?: true }

    val pomodoroDuration: Flow<Int> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[POMODORO_DURATION] ?: 25 }

    val breakDuration: Flow<Int> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[BREAK_DURATION] ?: 5 }

    val attendanceThreshold: Flow<Float> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[ATTENDANCE_THRESHOLD] ?: 75f }

    suspend fun setLoggedIn(userId: Int, name: String, email: String) {
        dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = true
            prefs[USER_ID] = userId
            prefs[USER_NAME] = name
            prefs[USER_EMAIL] = email
        }
    }

    suspend fun logout() {
        dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = false
            prefs[USER_ID] = -1
            prefs[USER_NAME] = ""
            prefs[USER_EMAIL] = ""
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { it[IS_DARK_MODE] = enabled }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { it[NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun setPomodoroDuration(minutes: Int) {
        dataStore.edit { it[POMODORO_DURATION] = minutes }
    }

    suspend fun setBreakDuration(minutes: Int) {
        dataStore.edit { it[BREAK_DURATION] = minutes }
    }

    suspend fun setAttendanceThreshold(threshold: Float) {
        dataStore.edit { it[ATTENDANCE_THRESHOLD] = threshold }
    }
}
