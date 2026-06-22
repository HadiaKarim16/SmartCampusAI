package com.smartcampus.ai.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.smartcampus.ai.data.local.SmartCampusDatabase
import com.smartcampus.ai.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ── Database ──────────────────────────────
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SmartCampusDatabase =
        Room.databaseBuilder(
            context,
            SmartCampusDatabase::class.java,
            SmartCampusDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()

    // ── DAOs ──────────────────────────────────
    @Provides fun provideUserDao(db: SmartCampusDatabase): UserDao = db.userDao()
    @Provides fun provideAssignmentDao(db: SmartCampusDatabase): AssignmentDao = db.assignmentDao()
    @Provides fun provideNoteDao(db: SmartCampusDatabase): NoteDao = db.noteDao()
    @Provides fun provideAttendanceDao(db: SmartCampusDatabase): AttendanceDao = db.attendanceDao()
    @Provides fun provideTimetableDao(db: SmartCampusDatabase): TimetableDao = db.timetableDao()
    @Provides fun providePomodoroDao(db: SmartCampusDatabase): PomodoroDao = db.pomodoroDao()
    @Provides fun provideAiChatDao(db: SmartCampusDatabase): AiChatDao = db.aiChatDao()

    // ── Network ───────────────────────────────
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().setLenient().create()
}
