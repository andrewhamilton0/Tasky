package com.andrew.tasky.core.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import androidx.work.WorkManager
import com.andrew.tasky.agenda.data.agenda.AgendaApi
import com.andrew.tasky.agenda.data.agenda.AgendaRepositoryImpl
import com.andrew.tasky.agenda.data.database.AgendaDatabase
import com.andrew.tasky.agenda.data.reminder.ReminderApi
import com.andrew.tasky.agenda.data.reminder.ReminderRepositoryImpl
import com.andrew.tasky.agenda.data.task.TaskApi
import com.andrew.tasky.agenda.data.task.TaskRepositoryImpl
import com.andrew.tasky.agenda.domain.AgendaRepository
import com.andrew.tasky.agenda.domain.ReminderRepository
import com.andrew.tasky.agenda.domain.TaskRepository
import com.andrew.tasky.auth.data.*
import com.andrew.tasky.auth.domain.EmailPatternValidator
import com.andrew.tasky.core.data.ApiKeyInterceptor
import com.andrew.tasky.core.data.TokenInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit.Builder
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideEmailValidator(): EmailPatternValidator {
        return EmailPatternValidatorImpl()
    }

    @Provides
    @Singleton
    fun provideTaskyClient(): Builder {
        return Builder()
            .baseUrl("https://tasky.pl-coding.com")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(OkHttpClient())
    }

    @Provides
    @Singleton
    fun provideWorkManager(app: Application): WorkManager {
        return WorkManager.getInstance(app)
    }

    @Provides
    @Singleton
    fun provideAuthApi(taskyClient: Builder): AuthApi {
        val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        return taskyClient.client(
            OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(ApiKeyInterceptor)
                .build()
        )
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun provideSharedPref(app: Application): SharedPreferences {
        return app.getSharedPreferences("prefs", MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(api: AuthApi, prefs: SharedPreferences): AuthRepository {
        return AuthRepositoryImpl(api, prefs)
    }

    @Provides
    @Singleton
    fun provideAgendaApi(taskyClient: Builder, prefs: SharedPreferences): AgendaApi {
        val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        return taskyClient.client(
            OkHttpClient.Builder()
                .addInterceptor(ApiKeyInterceptor)
                .addInterceptor(TokenInterceptor(prefs))
                .addInterceptor(logging)
                .build()
        )
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun provideAgendaRepository(
        agendaApi: AgendaApi,
        reminderRepository: ReminderRepository,
        db: AgendaDatabase
    ): AgendaRepository {
        return AgendaRepositoryImpl(
            agendaApi = agendaApi,
            reminderRepository = reminderRepository,
            db = db
        )
    }

    @Provides
    @Singleton
    fun provideAgendaDatabase(app: Application): AgendaDatabase {
        return Room.databaseBuilder(
            app,
            AgendaDatabase::class.java,
            "agenda_db.db"
        ).build()
    }

    // @Provides
    // @Singleton
    // fun provideEventRepository(db: EventDatabase): EventRepository {
    //    return EventRepositoryImpl(db)
    // }

    @Provides
    @Singleton
    fun provideReminderRepository(
        api: ReminderApi,
        db: AgendaDatabase
    ): ReminderRepository {
        return ReminderRepositoryImpl(db = db, api = api)
    }

    @Provides
    @Singleton
    fun provideReminderApi(prefs: SharedPreferences, taskyClient: Builder): ReminderApi {
        val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        return taskyClient.client(
            OkHttpClient.Builder()
                .addInterceptor(ApiKeyInterceptor)
                .addInterceptor(TokenInterceptor(prefs))
                .addInterceptor(logging)
                .build()
        )
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun provideTaskRepository(
        api: TaskApi,
        db: AgendaDatabase
    ): TaskRepository {
        return TaskRepositoryImpl(db = db, api = api)
    }

    @Provides
    @Singleton
    fun provideTaskApi(prefs: SharedPreferences, taskyClient: Builder): TaskApi {
        val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        return taskyClient.client(
            OkHttpClient.Builder()
                .addInterceptor(ApiKeyInterceptor)
                .addInterceptor(TokenInterceptor(prefs))
                .addInterceptor(logging)
                .build()
        )
            .build()
            .create()
    }
}
