package com.ryan.filemanager.data

import android.content.Context
import android.content.res.Configuration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.appThemeDatastore: DataStore<Preferences> by preferencesDataStore(name = "app_theme")

val DARK_THEME = booleanPreferencesKey("dark_theme")

fun isSystemDarkTheme(context:Context): Boolean {
    return ((context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)
}

fun getDarkTheme(context: Context): Flow<Boolean> {
    return context.appThemeDatastore.data.map { preferences ->
        preferences[DARK_THEME] ?: isSystemDarkTheme(context)
    }
}

suspend fun saveDarkTheme(context: Context, darkTheme: Boolean) {
    context.appThemeDatastore.edit {preferences ->
        preferences[DARK_THEME] = darkTheme
    }
}

suspend fun toggleDarkTheme(context: Context) {
    context.appThemeDatastore.edit { preferences ->
        val darkTheme = preferences[DARK_THEME] ?: isSystemDarkTheme(context)
        preferences[DARK_THEME] = !darkTheme
    }
}