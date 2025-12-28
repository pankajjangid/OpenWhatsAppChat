package com.whatsappdirect.direct_cha.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.whatsappdirect.direct_cha.data.model.DeletedMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface DeletedMessageDao {
    
    @Query("SELECT * FROM deleted_messages ORDER BY deletedTimestamp DESC")
    fun getAllDeletedMessages(): Flow<List<DeletedMessage>>
    
    @Query("SELECT * FROM deleted_messages WHERE senderName LIKE '%' || :query || '%' OR messageText LIKE '%' || :query || '%' ORDER BY deletedTimestamp DESC")
    fun searchDeletedMessages(query: String): Flow<List<DeletedMessage>>
    
    @Query("SELECT * FROM deleted_messages WHERE packageName = :packageName ORDER BY deletedTimestamp DESC")
    fun getDeletedMessagesByPackage(packageName: String): Flow<List<DeletedMessage>>
    
    @Query("SELECT * FROM deleted_messages WHERE isGroupMessage = :isGroup ORDER BY deletedTimestamp DESC")
    fun getDeletedMessagesByType(isGroup: Boolean): Flow<List<DeletedMessage>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeletedMessage(message: DeletedMessage)
    
    @Query("DELETE FROM deleted_messages WHERE id = :messageId")
    suspend fun deleteMessage(messageId: Long)
    
    @Query("DELETE FROM deleted_messages")
    suspend fun clearAll()
    
    @Query("SELECT COUNT(*) FROM deleted_messages")
    fun getDeletedMessagesCount(): Flow<Int>
}
