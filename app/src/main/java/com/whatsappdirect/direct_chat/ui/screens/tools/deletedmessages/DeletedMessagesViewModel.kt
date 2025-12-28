package com.whatsappdirect.direct_cha.ui.screens.tools.deletedmessages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whatsappdirect.direct_cha.data.local.DeletedMessageDao
import com.whatsappdirect.direct_cha.data.model.DeletedMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeletedMessagesViewModel @Inject constructor(
    private val deletedMessageDao: DeletedMessageDao
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()
    
    private val _filterType = MutableStateFlow(FilterType.ALL)
    val filterType = _filterType.asStateFlow()
    
    private val allMessages = deletedMessageDao.getAllDeletedMessages()
    
    val deletedMessages: StateFlow<List<DeletedMessage>> = combine(
        allMessages,
        _searchQuery,
        _filterType
    ) { messages, query, filter ->
        var filtered = messages
        
        // Apply filter
        filtered = when (filter) {
            FilterType.ALL -> filtered
            FilterType.WHATSAPP -> filtered.filter { it.packageName == "com.whatsapp" }
            FilterType.WHATSAPP_BUSINESS -> filtered.filter { it.packageName == "com.whatsapp.w4b" }
            FilterType.GROUPS -> filtered.filter { it.isGroupMessage }
            FilterType.PERSONAL -> filtered.filter { !it.isGroupMessage }
        }
        
        // Apply search
        if (query.isNotBlank()) {
            filtered = filtered.filter {
                it.senderName.contains(query, ignoreCase = true) ||
                it.messageText.contains(query, ignoreCase = true) ||
                it.groupName?.contains(query, ignoreCase = true) == true
            }
        }
        
        filtered
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    val messagesCount = deletedMessageDao.getDeletedMessagesCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )
    
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun setFilterType(type: FilterType) {
        _filterType.value = type
    }
    
    fun deleteMessage(messageId: Long) {
        viewModelScope.launch {
            deletedMessageDao.deleteMessage(messageId)
        }
    }
    
    fun clearAllMessages() {
        viewModelScope.launch {
            deletedMessageDao.clearAll()
        }
    }
}

enum class FilterType {
    ALL,
    WHATSAPP,
    WHATSAPP_BUSINESS,
    GROUPS,
    PERSONAL
}
