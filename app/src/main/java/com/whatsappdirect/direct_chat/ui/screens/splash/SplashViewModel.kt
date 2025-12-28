package com.whatsappdirect.direct_cha.ui.screens.splash

import androidx.lifecycle.ViewModel
import com.whatsappdirect.direct_cha.data.local.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    preferencesManager: PreferencesManager
) : ViewModel() {
    
    val isFirstLaunch: Flow<Boolean> = preferencesManager.isFirstLaunch
}
