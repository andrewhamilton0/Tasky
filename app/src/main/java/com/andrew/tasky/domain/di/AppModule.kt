package com.andrew.tasky.domain.di

import android.app.Application
import androidx.room.Room
import com.andrew.tasky.domain.db.AgendaItemDatabase
import com.andrew.tasky.domain.repository.AgendaItemRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

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
}
