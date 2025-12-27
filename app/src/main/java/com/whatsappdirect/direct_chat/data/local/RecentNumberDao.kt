package com.whatsappdirect.direct_chat.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.whatsappdirect.direct_chat.data.model.RecentNumber
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentNumberDao {
    
    @Query("SELECT * FROM recent_numbers ORDER BY lastUsed DESC LIMIT :limit")
    fun getRecentNumbers(limit: Int = 20): Flow<List<RecentNumber>>
    
    @Query("SELECT * FROM recent_numbers WHERE isFavorite = 1 ORDER BY displayName ASC")
    fun getFavorites(): Flow<List<RecentNumber>>
    
    @Query("SELECT * FROM recent_numbers WHERE phoneNumber = :phoneNumber AND countryCode = :countryCode LIMIT 1")
    suspend fun findByNumber(phoneNumber: String, countryCode: String): RecentNumber?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recentNumber: RecentNumber)
    
    @Update
    suspend fun update(recentNumber: RecentNumber)
    
    @Delete
    suspend fun delete(recentNumber: RecentNumber)
    
    @Query("DELETE FROM recent_numbers WHERE isFavorite = 0")
    suspend fun clearNonFavorites()
    
    @Query("DELETE FROM recent_numbers")
    suspend fun clearAll()
}
