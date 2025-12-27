package com.whatsappdirect.direct_chat.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.whatsappdirect.direct_chat.data.model.ScheduledMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduledMessageDao {
    
    @Query("SELECT * FROM scheduled_messages ORDER BY scheduledTime ASC")
    fun getAllScheduledMessages(): Flow<List<ScheduledMessage>>
    
    @Query("SELECT * FROM scheduled_messages WHERE status = 'PENDING' ORDER BY scheduledTime ASC")
    fun getPendingMessages(): Flow<List<ScheduledMessage>>
    
    @Query("SELECT * FROM scheduled_messages WHERE status = 'PENDING' AND scheduledTime <= :currentTime")
    suspend fun getDueMessages(currentTime: Long): List<ScheduledMessage>
    
    @Query("SELECT * FROM scheduled_messages WHERE id = :id")
    suspend fun getById(id: Long): ScheduledMessage?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: ScheduledMessage): Long
    
    @Update
    suspend fun update(message: ScheduledMessage)
    
    @Delete
    suspend fun delete(message: ScheduledMessage)
    
    @Query("DELETE FROM scheduled_messages WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    @Query("UPDATE scheduled_messages SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)
    
    @Query("DELETE FROM scheduled_messages WHERE status != 'PENDING'")
    suspend fun clearCompletedMessages()
}
