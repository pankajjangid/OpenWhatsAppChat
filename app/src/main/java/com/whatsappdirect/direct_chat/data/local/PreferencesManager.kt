package com.whatsappdirect.direct_chat.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore
    
    private object PreferencesKeys {
        val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
        val PREFERRED_APP = stringPreferencesKey("preferred_app")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val LAST_COUNTRY_CODE = stringPreferencesKey("last_country_code")
        val APP_LOCK_ENABLED = booleanPreferencesKey("app_lock_enabled")
        val APP_LOCK_PIN = stringPreferencesKey("app_lock_pin")
        val USE_BIOMETRIC = booleanPreferencesKey("use_biometric")
        val INCOGNITO_MODE = booleanPreferencesKey("incognito_mode")
    }
    
    val isFirstLaunch: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_FIRST_LAUNCH] ?: true
    }
    
    val preferredApp: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.PREFERRED_APP] ?: "whatsapp"
    }
    
    val darkMode: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DARK_MODE] ?: false
    }
    
    val lastCountryCode: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.LAST_COUNTRY_CODE] ?: ""
    }
    
    val appLockEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.APP_LOCK_ENABLED] ?: false
    }
    
    val appLockPin: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.APP_LOCK_PIN] ?: ""
    }
    
    val useBiometric: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USE_BIOMETRIC] ?: true
    }
    
    val incognitoMode: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.INCOGNITO_MODE] ?: false
    }
    
    suspend fun setFirstLaunchComplete() {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_FIRST_LAUNCH] = false
        }
    }
    
    suspend fun setPreferredApp(app: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PREFERRED_APP] = app
        }
    }
    
    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE] = enabled
        }
    }
    
    suspend fun setLastCountryCode(code: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_COUNTRY_CODE] = code
        }
    }
    
    suspend fun setAppLockEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_LOCK_ENABLED] = enabled
        }
    }
    
    suspend fun setAppLockPin(pin: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_LOCK_PIN] = pin
        }
    }
    
    suspend fun setUseBiometric(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USE_BIOMETRIC] = enabled
        }
    }
    
    suspend fun setIncognitoMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.INCOGNITO_MODE] = enabled
        }
    }
    
    suspend fun clearAllData() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
