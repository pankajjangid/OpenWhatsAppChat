package com.whatsappdirect.direct_cha.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.whatsappdirect.direct_cha.data.model.ContactGroup
import com.whatsappdirect.direct_cha.data.model.GroupMember
import com.whatsappdirect.direct_cha.data.model.MessageTemplate
import com.whatsappdirect.direct_cha.data.model.RecentNumber
import com.whatsappdirect.direct_cha.data.model.ScheduledMessage

@Database(
    entities = [RecentNumber::class, MessageTemplate::class, ContactGroup::class, GroupMember::class, ScheduledMessage::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recentNumberDao(): RecentNumberDao
    abstract fun messageTemplateDao(): MessageTemplateDao
    abstract fun contactGroupDao(): ContactGroupDao
    abstract fun scheduledMessageDao(): ScheduledMessageDao
}
