package com.whatsappdirect.direct_chat.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whatsappdirect.direct_chat.data.local.MessageTemplateDao
import com.whatsappdirect.direct_chat.data.local.PreferencesManager
import com.whatsappdirect.direct_chat.data.local.RecentNumberDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val recentNumberDao: RecentNumberDao,
    private val messageTemplateDao: MessageTemplateDao,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    
    val darkMode: Flow<Boolean> = preferencesManager.darkMode
    val incognitoMode: Flow<Boolean> = preferencesManager.incognitoMode
    
    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setDarkMode(enabled)
        }
    }
    
    fun setIncognitoMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setIncognitoMode(enabled)
        }
    }
    
    fun clearAllData() {
        viewModelScope.launch {
            recentNumberDao.clearAll()
            messageTemplateDao.clearAll()
        }
    }
}
