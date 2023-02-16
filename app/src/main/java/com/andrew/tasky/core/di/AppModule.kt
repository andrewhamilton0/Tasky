package com.andrew.tasky.core.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import androidx.work.WorkManager
import com.andrew.tasky.agenda.data.agenda.AgendaApi
import com.andrew.tasky.agenda.data.agenda.AgendaRepositoryImpl
import com.andrew.tasky.agenda.data.database.AgendaDatabase
import com.andrew.tasky.agenda.data.event.EventApi
import com.andrew.tasky.agenda.data.event.EventRepositoryImpl
import com.andrew.tasky.agenda.data.reminder.ReminderApi
import com.andrew.tasky.agenda.data.reminder.ReminderRepositoryImpl
import com.andrew.tasky.agenda.data.task.TaskApi
import com.andrew.tasky.agenda.data.task.TaskRepositoryImpl
import com.andrew.tasky.agenda.domain.AgendaRepository
import com.andrew.tasky.agenda.domain.EventRepository
import com.andrew.tasky.agenda.domain.ReminderRepository
import com.andrew.tasky.agenda.domain.TaskRepository
import com.andrew.tasky.auth.data.*
import com.andrew.tasky.auth.domain.AuthRepository
import com.andrew.tasky.auth.domain.EmailPatternValidator
import com.andrew.tasky.core.data.ApiKeyInterceptor
import com.andrew.tasky.core.data.TokenInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.Retrofit.Builder
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideIoDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }

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
    fun provideAgendaItemRetrofitBuilder(prefs: SharedPreferences, taskyClient: Builder): Retrofit {
        val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        return taskyClient.client(
            OkHttpClient.Builder()
                .addInterceptor(ApiKeyInterceptor)
                .addInterceptor(TokenInterceptor(prefs))
                .addInterceptor(logging)
                .build()
        )
            .build()
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
    fun provideAuthRepository(
        api: AuthApi,
        prefs: SharedPreferences,
        agendaRepository: AgendaRepository,
        workManager: WorkManager
    ): AuthRepository {
        return AuthRepositoryImpl(
            api = api,
            prefs = prefs,
            agendaRepository = agendaRepository,
            workManager = workManager
        )
    }

    @Provides
    @Singleton
    fun provideAgendaApi(agendaItemRetrofitBuilder: Retrofit): AgendaApi {
        return agendaItemRetrofitBuilder.create()
    }

    @Provides
    @Singleton
    fun provideAgendaRepository(
        agendaApi: AgendaApi,
        reminderRepository: ReminderRepository,
        taskRepository: TaskRepository,
        eventRepository: EventRepository,
        db: AgendaDatabase,
        ioDispatcher: CoroutineDispatcher
    ): AgendaRepository {
        return AgendaRepositoryImpl(
            agendaApi = agendaApi,
            reminderRepository = reminderRepository,
            taskRepository = taskRepository,
            eventRepository = eventRepository,
            db = db,
            ioDispatcher = ioDispatcher
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
    fun provideReminderApi(agendaItemRetrofitBuilder: Retrofit): ReminderApi {
        return agendaItemRetrofitBuilder.create()
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
    fun provideTaskApi(agendaItemRetrofitBuilder: Retrofit): TaskApi {
        return agendaItemRetrofitBuilder.create()
    }

    @Provides
    @Singleton
    fun provideEventRepository(
        api: EventApi,
        db: AgendaDatabase,
        app: Application,
        ioDispatcher: CoroutineDispatcher
    ): EventRepository {
        return EventRepositoryImpl(
            db = db,
            api = api,
            appContext = app,
            ioDispatcher = ioDispatcher
        )
    }

    @Provides
    @Singleton
    fun provideEventApi(agendaItemRetrofitBuilder: Retrofit): EventApi {
        return agendaItemRetrofitBuilder.create()
    }
}
