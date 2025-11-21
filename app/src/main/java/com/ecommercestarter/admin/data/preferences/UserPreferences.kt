package com.ecommercestarter.admin.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ecommercestarter.admin.data.model.BrandingConfig
import com.ecommercestarter.admin.data.model.UserDto
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    private object PreferencesKeys {
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_DATA = stringPreferencesKey("user_data")
        val SERVER_URL = stringPreferencesKey("server_url")
        val BRANDING = stringPreferencesKey("branding")
        val DARK_MODE = stringPreferencesKey("dark_mode")
        val BIOMETRIC_ENABLED = stringPreferencesKey("biometric_enabled")
        val NOTIFICATIONS_ENABLED = stringPreferencesKey("notifications_enabled")
    }

    val authToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.AUTH_TOKEN]
    }

    val refreshToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.REFRESH_TOKEN]
    }

    val userData: Flow<UserDto?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_DATA]?.let {
            gson.fromJson(it, UserDto::class.java)
        }
    }

    val serverUrl: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SERVER_URL]
    }

    val branding: Flow<BrandingConfig?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.BRANDING]?.let {
            gson.fromJson(it, BrandingConfig::class.java)
        }
    }

    val darkMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DARK_MODE]?.toBoolean() ?: false
    }

    val biometricEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.BIOMETRIC_ENABLED]?.toBoolean() ?: false
    }
    
    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.NOTIFICATIONS_ENABLED]?.toBoolean() ?: true
    }

    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTH_TOKEN] = token
        }
    }

    suspend fun saveRefreshToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.REFRESH_TOKEN] = token
        }
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTH_TOKEN] = accessToken
            preferences[PreferencesKeys.REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun saveUserData(user: UserDto) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_DATA] = gson.toJson(user)
        }
    }

    suspend fun saveServerUrl(url: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SERVER_URL] = url
        }
    }

    suspend fun saveBranding(branding: BrandingConfig) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BRANDING] = gson.toJson(branding)
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE] = enabled.toString()
        }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BIOMETRIC_ENABLED] = enabled.toString()
        }
    }
    
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled.toString()
        }
    }

    suspend fun clearBranding() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.BRANDING)
        }
    }

    suspend fun clearAuthData() {
        context.dataStore.edit { preferences ->
            // Clear only auth-related data, preserve server URL, branding, and dark mode
            preferences.remove(PreferencesKeys.AUTH_TOKEN)
            preferences.remove(PreferencesKeys.REFRESH_TOKEN)
            preferences.remove(PreferencesKeys.USER_DATA)
            preferences.remove(PreferencesKeys.BIOMETRIC_ENABLED)
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            // Preserve server URL and branding when clearing auth data
            val savedServerUrl = preferences[PreferencesKeys.SERVER_URL]
            val savedBranding = preferences[PreferencesKeys.BRANDING]
            val savedDarkMode = preferences[PreferencesKeys.DARK_MODE]
            
            preferences.clear()
            
            // Restore preserved values
            savedServerUrl?.let { preferences[PreferencesKeys.SERVER_URL] = it }
            savedBranding?.let { preferences[PreferencesKeys.BRANDING] = it }
            savedDarkMode?.let { preferences[PreferencesKeys.DARK_MODE] = it }
        }
    }
}
