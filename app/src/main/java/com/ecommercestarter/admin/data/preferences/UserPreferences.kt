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
        val USER_DATA = stringPreferencesKey("user_data")
        val SERVER_URL = stringPreferencesKey("server_url")
        val BRANDING = stringPreferencesKey("branding")
    }

    val authToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.AUTH_TOKEN]
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

    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTH_TOKEN] = token
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

    suspend fun clearBranding() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.BRANDING)
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
