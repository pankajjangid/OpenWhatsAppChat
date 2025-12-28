package com.whatsappdirect.direct_cha.ui.screens.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whatsappdirect.direct_cha.data.local.ContactGroupDao
import com.whatsappdirect.direct_cha.data.model.ContactGroup
import com.whatsappdirect.direct_cha.data.model.GroupMember
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactGroupsViewModel @Inject constructor(
    private val contactGroupDao: ContactGroupDao
) : ViewModel() {
    
    val groups: Flow<List<ContactGroup>> = contactGroupDao.getAllGroups()
    
    fun createGroup(name: String) {
        viewModelScope.launch {
            contactGroupDao.insertGroup(ContactGroup(name = name))
        }
    }
    
    fun deleteGroup(group: ContactGroup) {
        viewModelScope.launch {
            contactGroupDao.deleteGroupWithMembers(group.id)
        }
    }
    
    fun getGroupMembers(groupId: Long): Flow<List<GroupMember>> {
        return contactGroupDao.getGroupMembers(groupId)
    }
    
    suspend fun getGroupMembersList(groupId: Long): List<GroupMember> {
        return contactGroupDao.getGroupMembersList(groupId)
    }
    
    fun getMemberCount(groupId: Long): Flow<Int> {
        return contactGroupDao.getGroupMemberCount(groupId)
    }
    
    fun addMember(groupId: Long, countryCode: String, phoneNumber: String, displayName: String?) {
        viewModelScope.launch {
            contactGroupDao.insertMember(
                GroupMember(
                    groupId = groupId,
                    countryCode = countryCode,
                    phoneNumber = phoneNumber,
                    displayName = displayName
                )
            )
        }
    }
    
    fun removeMember(groupId: Long, member: GroupMember) {
        viewModelScope.launch {
            contactGroupDao.deleteMember(member)
        }
    }
}
