package com.whatsappdirect.direct_chat.di

import android.content.Context
import androidx.room.Room
import com.whatsappdirect.direct_chat.data.local.AppDatabase
import com.whatsappdirect.direct_chat.data.local.ContactGroupDao
import com.whatsappdirect.direct_chat.data.local.MessageTemplateDao
import com.whatsappdirect.direct_chat.data.local.RecentNumberDao
import com.whatsappdirect.direct_chat.data.local.ScheduledMessageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "whatsapp_direct_db"
        ).fallbackToDestructiveMigration()
        .build()
    }
    
    @Provides
    @Singleton
    fun provideRecentNumberDao(database: AppDatabase): RecentNumberDao {
        return database.recentNumberDao()
    }
    
    @Provides
    @Singleton
    fun provideMessageTemplateDao(database: AppDatabase): MessageTemplateDao {
        return database.messageTemplateDao()
    }
    
    @Provides
    @Singleton
    fun provideContactGroupDao(database: AppDatabase): ContactGroupDao {
        return database.contactGroupDao()
    }
    
    @Provides
    @Singleton
    fun provideScheduledMessageDao(database: AppDatabase): ScheduledMessageDao {
        return database.scheduledMessageDao()
    }
}
