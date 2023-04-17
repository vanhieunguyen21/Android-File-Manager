package com.ryan.filemanager.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ryan.filemanager.model.SortOrder
import com.ryan.filemanager.model.SortType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.sortDataStore: DataStore<Preferences> by preferencesDataStore(name = "sort")

val SORT_TYPE = stringPreferencesKey("sort_type")
val SORT_ORDER = stringPreferencesKey("sort_order")

fun getSortType(context: Context): Flow<SortType> {
    return context.sortDataStore.data.map { preferences ->
        val savedValue = preferences[SORT_TYPE] ?: ""
        SortType.fromString(savedValue)
    }
}

suspend fun saveSortType(context: Context, sortType: SortType) {
    context.sortDataStore.edit { preferences ->
        preferences[SORT_TYPE] = sortType.toString()
    }
}

fun getSortOrder(context: Context): Flow<SortOrder> {
    return context.sortDataStore.data.map { preferences ->
        val savedValue = preferences[SORT_ORDER] ?: ""
        SortOrder.fromString(savedValue)
    }
}

suspend fun saveSortOrder(context: Context, sortOrder: SortOrder) {
    context.sortDataStore.edit { preferences ->
        preferences[SORT_ORDER] = sortOrder.toString()
    }
}