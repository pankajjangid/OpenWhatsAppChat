package com.whatsappdirect.direct_chat.ui.screens.tools.scheduler

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whatsappdirect.direct_chat.data.local.ScheduledMessageDao
import com.whatsappdirect.direct_chat.data.model.ScheduledMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageSchedulerViewModel @Inject constructor(
    private val scheduledMessageDao: ScheduledMessageDao
) : ViewModel() {
    
    val scheduledMessages: Flow<List<ScheduledMessage>> = scheduledMessageDao.getAllScheduledMessages()
    
    fun scheduleMessage(
        countryCode: String,
        phoneNumber: String,
        message: String,
        scheduledTime: Long,
        isWhatsAppBusiness: Boolean
    ) {
        viewModelScope.launch {
            scheduledMessageDao.insert(
                ScheduledMessage(
                    countryCode = countryCode,
                    phoneNumber = phoneNumber,
                    message = message,
                    scheduledTime = scheduledTime,
                    isWhatsAppBusiness = isWhatsAppBusiness
                )
            )
        }
    }
    
    fun deleteMessage(message: ScheduledMessage) {
        viewModelScope.launch {
            scheduledMessageDao.delete(message)
        }
    }
    
    fun cancelMessage(id: Long) {
        viewModelScope.launch {
            scheduledMessageDao.updateStatus(id, "CANCELLED")
        }
    }
    
    fun markAsSent(id: Long) {
        viewModelScope.launch {
            scheduledMessageDao.updateStatus(id, "SENT")
        }
    }
    
    fun markAsFailed(id: Long) {
        viewModelScope.launch {
            scheduledMessageDao.updateStatus(id, "FAILED")
        }
    }
}
