package com.andrew.tasky.core.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.andrew.tasky.agenda.data.agenda.AgendaApi
import com.andrew.tasky.agenda.data.agenda.AgendaItemDatabase
import com.andrew.tasky.agenda.data.agenda.AgendaRepositoryImpl
import com.andrew.tasky.agenda.data.event.EventDatabase
import com.andrew.tasky.agenda.data.event.EventRepositoryImpl
import com.andrew.tasky.agenda.data.reminder.ReminderDatabase
import com.andrew.tasky.agenda.data.reminder.ReminderRepositoryImpl
import com.andrew.tasky.agenda.domain.AgendaRepository
import com.andrew.tasky.agenda.domain.EventRepository
import com.andrew.tasky.agenda.domain.ReminderRepository
import com.andrew.tasky.agenda.domain.repository.AgendaItemRepository
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
import retrofit2.Retrofit
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
    fun provideAuthApi(): AuthApi {
        return Retrofit.Builder()
            .baseUrl("https://tasky.pl-coding.com")
            .client(OkHttpClient.Builder().addInterceptor(ApiKeyInterceptor).build())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun provideAgendaApi(prefs: SharedPreferences): AgendaApi {
        return Retrofit.Builder()
            .baseUrl("https://tasky.pl-coding.com")
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(ApiKeyInterceptor)
                    .addInterceptor(TokenInterceptor(prefs))
                    .build()
            )
            .addConverterFactory(MoshiConverterFactory.create())
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
    fun provideAgendaRepository(api: AgendaApi): AgendaRepository {
        return AgendaRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideAgendaItemDatabase(app: Application): AgendaItemDatabase {
        return Room.databaseBuilder(
            app,
            AgendaItemDatabase::class.java,
            "agenda_item_db.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideAgendaItemRepository(db: AgendaItemDatabase): AgendaItemRepository {
        return AgendaItemRepository(db)
    }

    @Provides
    @Singleton
    fun provideEventDatabase(app: Application): EventDatabase {
        return Room.databaseBuilder(
            app,
            EventDatabase::class.java,
            "event_db.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideEventRepository(db: EventDatabase): EventRepository {
        return EventRepositoryImpl(db)
    }

    @Provides
    @Singleton
    fun provideReminderDatabase(app: Application): ReminderDatabase {
        return Room.databaseBuilder(
            app,
            ReminderDatabase::class.java,
            "reminder_db.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideReminderRepository(db: EventDatabase): ReminderRepository {
        return ReminderRepositoryImpl(db)
    }
}
