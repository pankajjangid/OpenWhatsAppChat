package com.whatsappdirect.direct_cha.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deleted_messages")
data class DeletedMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val senderName: String,
    val senderNumber: String?,
    val messageText: String,
    val timestamp: Long,
    val deletedTimestamp: Long,
    val packageName: String, // com.whatsapp or com.whatsapp.w4b
    val isGroupMessage: Boolean = false,
    val groupName: String? = null,
    val hasMedia: Boolean = false,
    val mediaType: String? = null // "image", "video", "audio", "document"
)
