package com.whatsappdirect.direct_chat.ui.screens.directchat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.whatsappdirect.direct_chat.data.local.MessageTemplateDao
import com.whatsappdirect.direct_chat.data.local.PreferencesManager
import com.whatsappdirect.direct_chat.data.local.RecentNumberDao
import com.whatsappdirect.direct_chat.data.model.MessageTemplate
import com.whatsappdirect.direct_chat.data.model.RecentNumber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DirectChatUiState(
    val countryCode: String = "91",
    val phoneNumber: String = "",
    val message: String = "",
    val isValidNumber: Boolean? = null,
    val showTemplates: Boolean = false
)

@HiltViewModel
class DirectChatViewModel @Inject constructor(
    private val recentNumberDao: RecentNumberDao,
    private val messageTemplateDao: MessageTemplateDao,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    
    private val phoneNumberUtil = PhoneNumberUtil.getInstance()
    
    private val _uiState = MutableStateFlow(DirectChatUiState())
    val uiState: StateFlow<DirectChatUiState> = _uiState.asStateFlow()
    
    val recentNumbers: Flow<List<RecentNumber>> = recentNumberDao.getRecentNumbers(10)
    val messageTemplates: Flow<List<MessageTemplate>> = messageTemplateDao.getAllTemplates()
    val incognitoMode: Flow<Boolean> = preferencesManager.incognitoMode
    
    private var isIncognito = false
    
    init {
        viewModelScope.launch {
            preferencesManager.incognitoMode.collect { incognito ->
                isIncognito = incognito
            }
        }
        viewModelScope.launch {
            preferencesManager.lastCountryCode.collect { code ->
                if (code.isNotEmpty()) {
                    _uiState.update { it.copy(countryCode = code) }
                }
            }
        }
    }
    
    fun updateCountryCode(code: String) {
        _uiState.update { it.copy(countryCode = code) }
        viewModelScope.launch {
            preferencesManager.setLastCountryCode(code)
        }
        validatePhoneNumber()
    }
    
    fun updatePhoneNumber(number: String) {
        val cleanNumber = number.filter { it.isDigit() }
        _uiState.update { it.copy(phoneNumber = cleanNumber) }
        validatePhoneNumber()
    }
    
    fun updateMessage(message: String) {
        _uiState.update { it.copy(message = message) }
    }
    
    fun clearPhoneNumber() {
        _uiState.update { it.copy(phoneNumber = "", isValidNumber = null) }
    }
    
    fun clearMessage() {
        _uiState.update { it.copy(message = "") }
    }
    
    private fun validatePhoneNumber() {
        val state = _uiState.value
        if (state.phoneNumber.isEmpty()) {
            _uiState.update { it.copy(isValidNumber = null) }
            return
        }
        
        try {
            val fullNumber = "+${state.countryCode}${state.phoneNumber}"
            val parsedNumber = phoneNumberUtil.parse(fullNumber, null)
            val isValid = phoneNumberUtil.isValidNumber(parsedNumber)
            _uiState.update { it.copy(isValidNumber = isValid) }
        } catch (e: Exception) {
            _uiState.update { it.copy(isValidNumber = false) }
        }
    }
    
    fun saveRecentNumber() {
        val state = _uiState.value
        if (state.phoneNumber.isEmpty()) return
        
        // Don't save if incognito mode is enabled
        if (isIncognito) return
        
        viewModelScope.launch {
            val existing = recentNumberDao.findByNumber(state.phoneNumber, state.countryCode)
            if (existing != null) {
                recentNumberDao.update(
                    existing.copy(
                        lastUsed = System.currentTimeMillis(),
                        usageCount = existing.usageCount + 1
                    )
                )
            } else {
                recentNumberDao.insert(
                    RecentNumber(
                        phoneNumber = state.phoneNumber,
                        countryCode = state.countryCode
                    )
                )
            }
        }
    }
    
    fun showTemplates() {
        _uiState.update { it.copy(showTemplates = true) }
    }
    
    fun hideTemplates() {
        _uiState.update { it.copy(showTemplates = false) }
    }
    
    fun addTemplate(title: String, content: String) {
        viewModelScope.launch {
            messageTemplateDao.insert(
                MessageTemplate(title = title, content = content)
            )
        }
    }
    
    fun updateTemplate(template: MessageTemplate) {
        viewModelScope.launch {
            messageTemplateDao.update(template)
        }
    }
    
    fun deleteTemplate(template: MessageTemplate) {
        viewModelScope.launch {
            messageTemplateDao.delete(template)
        }
    }
    
    fun selectTemplate(template: MessageTemplate) {
        _uiState.update { it.copy(message = template.content, showTemplates = false) }
        viewModelScope.launch {
            messageTemplateDao.update(template.copy(usageCount = template.usageCount + 1))
        }
    }
}
