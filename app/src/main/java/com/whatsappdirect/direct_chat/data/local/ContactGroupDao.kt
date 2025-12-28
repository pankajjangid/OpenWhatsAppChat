package com.whatsappdirect.direct_cha.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.whatsappdirect.direct_cha.data.model.ContactGroup
import com.whatsappdirect.direct_cha.data.model.GroupMember
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactGroupDao {
    
    @Query("SELECT * FROM contact_groups ORDER BY name ASC")
    fun getAllGroups(): Flow<List<ContactGroup>>
    
    @Query("SELECT * FROM contact_groups WHERE id = :groupId")
    suspend fun getGroupById(groupId: Long): ContactGroup?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: ContactGroup): Long
    
    @Update
    suspend fun updateGroup(group: ContactGroup)
    
    @Delete
    suspend fun deleteGroup(group: ContactGroup)
    
    @Query("DELETE FROM contact_groups WHERE id = :groupId")
    suspend fun deleteGroupById(groupId: Long)
    
    @Query("SELECT * FROM group_members WHERE groupId = :groupId")
    fun getGroupMembers(groupId: Long): Flow<List<GroupMember>>
    
    @Query("SELECT * FROM group_members WHERE groupId = :groupId")
    suspend fun getGroupMembersList(groupId: Long): List<GroupMember>
    
    @Query("SELECT COUNT(*) FROM group_members WHERE groupId = :groupId")
    fun getGroupMemberCount(groupId: Long): Flow<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: GroupMember)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembers(members: List<GroupMember>)
    
    @Delete
    suspend fun deleteMember(member: GroupMember)
    
    @Query("DELETE FROM group_members WHERE groupId = :groupId")
    suspend fun deleteAllMembersFromGroup(groupId: Long)
    
    @Query("DELETE FROM group_members WHERE groupId = :groupId AND phoneNumber = :phoneNumber AND countryCode = :countryCode")
    suspend fun deleteMemberByNumber(groupId: Long, phoneNumber: String, countryCode: String)
    
    @Transaction
    suspend fun deleteGroupWithMembers(groupId: Long) {
        deleteAllMembersFromGroup(groupId)
        deleteGroupById(groupId)
    }
}
