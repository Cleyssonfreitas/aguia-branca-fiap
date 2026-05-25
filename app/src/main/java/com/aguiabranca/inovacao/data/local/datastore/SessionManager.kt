package com.aguiabranca.inovacao.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session_prefs")

class SessionManager(private val context: Context) {

    companion object {
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_ROLE_KEY = stringPreferencesKey("user_role")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val THEME_PREF_KEY = androidx.datastore.preferences.core.intPreferencesKey("theme_pref")
    }

    val userIdFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_ID_KEY]
    }

    val userRoleFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_ROLE_KEY]
    }

    val userNameFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME_KEY]
    }

    val themeFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[THEME_PREF_KEY] ?: 0 // 0 = System, 1 = Light, 2 = Dark
    }

    suspend fun saveSession(userId: String, role: String, name: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[USER_ROLE_KEY] = role
            preferences[USER_NAME_KEY] = name
        }
    }

    suspend fun saveTheme(themeValue: Int) {
        context.dataStore.edit { preferences ->
            preferences[THEME_PREF_KEY] = themeValue
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_ID_KEY)
            preferences.remove(USER_ROLE_KEY)
            preferences.remove(USER_NAME_KEY)
            // Note: Not clearing theme preference on logout so it persists
        }
    }
}
