package com.whatsappdirect.direct_cha.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whatsappdirect.direct_cha.data.local.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    
    fun completeOnboarding() {
        viewModelScope.launch {
            preferencesManager.setFirstLaunchComplete()
        }
    }
}
