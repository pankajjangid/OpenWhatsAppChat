package com.whatsappdirect.direct_chat.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.whatsappdirect.direct_chat.data.model.MessageTemplate
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageTemplateDao {
    
    @Query("SELECT * FROM message_templates ORDER BY usageCount DESC, createdAt DESC")
    fun getAllTemplates(): Flow<List<MessageTemplate>>
    
    @Query("SELECT * FROM message_templates WHERE id = :id")
    suspend fun getById(id: Long): MessageTemplate?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(template: MessageTemplate)
    
    @Update
    suspend fun update(template: MessageTemplate)
    
    @Delete
    suspend fun delete(template: MessageTemplate)
    
    @Query("DELETE FROM message_templates")
    suspend fun clearAll()
}
