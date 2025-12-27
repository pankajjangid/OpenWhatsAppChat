package com.whatsappdirect.direct_chat.ui.screens.contacts

import android.content.ContentResolver
import android.content.Context
import android.provider.ContactsContract
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whatsappdirect.direct_chat.data.local.RecentNumberDao
import com.whatsappdirect.direct_chat.data.model.RecentNumber
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class DeviceContact(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val photoUri: String? = null
)

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val recentNumberDao: RecentNumberDao,
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    val recentNumbers: Flow<List<RecentNumber>> = recentNumberDao.getRecentNumbers(50)
    val favorites: Flow<List<RecentNumber>> = recentNumberDao.getFavorites()
    
    private val _deviceContacts = MutableStateFlow<List<DeviceContact>>(emptyList())
    val deviceContacts: StateFlow<List<DeviceContact>> = _deviceContacts.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    fun loadDeviceContacts() {
        viewModelScope.launch {
            _isLoading.value = true
            val contacts = withContext(Dispatchers.IO) {
                fetchDeviceContacts(context.contentResolver)
            }
            _deviceContacts.value = contacts
            _isLoading.value = false
        }
    }
    
    private fun fetchDeviceContacts(contentResolver: ContentResolver): List<DeviceContact> {
        val contacts = mutableListOf<DeviceContact>()
        val contactsMap = mutableMapOf<String, DeviceContact>()
        
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI
        )
        
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        
        cursor?.use {
            val idIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val photoIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)
            
            while (it.moveToNext()) {
                val id = it.getString(idIndex) ?: continue
                val name = it.getString(nameIndex) ?: "Unknown"
                val number = it.getString(numberIndex)?.replace(Regex("[^+0-9]"), "") ?: continue
                val photoUri = it.getString(photoIndex)
                
                if (number.isNotEmpty() && !contactsMap.containsKey(id)) {
                    contactsMap[id] = DeviceContact(id, name, number, photoUri)
                }
            }
        }
        
        contacts.addAll(contactsMap.values)
        return contacts.sortedBy { it.name.lowercase() }
    }
    
    fun toggleFavorite(contact: RecentNumber) {
        viewModelScope.launch {
            recentNumberDao.update(contact.copy(isFavorite = !contact.isFavorite))
        }
    }
    
    fun deleteContact(contact: RecentNumber) {
        viewModelScope.launch {
            recentNumberDao.delete(contact)
        }
    }
    
    fun clearAllRecent() {
        viewModelScope.launch {
            recentNumberDao.clearNonFavorites()
        }
    }
}
