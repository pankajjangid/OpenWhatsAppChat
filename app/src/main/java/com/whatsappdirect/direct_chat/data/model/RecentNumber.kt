package com.whatsappdirect.direct_cha.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_numbers")
data class RecentNumber(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val phoneNumber: String,
    val countryCode: String,
    val displayName: String? = null,
    val lastUsed: Long = System.currentTimeMillis(),
    val usageCount: Int = 1,
    val isFavorite: Boolean = false
)

@Entity(tableName = "message_templates")
data class MessageTemplate(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    val usageCount: Int = 0
)

@Entity(tableName = "contact_groups")
data class ContactGroup(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "group_members",
    primaryKeys = ["groupId", "phoneNumber", "countryCode"]
)
data class GroupMember(
    val groupId: Long,
    val phoneNumber: String,
    val countryCode: String,
    val displayName: String? = null
)

@Entity(tableName = "scheduled_messages")
data class ScheduledMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val phoneNumber: String,
    val countryCode: String,
    val message: String,
    val scheduledTime: Long,
    val isWhatsAppBusiness: Boolean = false,
    val status: String = "PENDING", // PENDING, SENT, FAILED, CANCELLED
    val createdAt: Long = System.currentTimeMillis()
)
