package com.ryan.filemanager

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryan.filemanager.app.BaseApplication
import com.ryan.filemanager.data.getDarkTheme
import com.ryan.filemanager.data.isSystemDarkTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    app: BaseApplication,
) : ViewModel() {
    private val darkThemeFlow = getDarkTheme(app)
    var darkTheme by mutableStateOf(isSystemDarkTheme(app))

    init {
        // Collect theme change
        viewModelScope.launch {
            darkThemeFlow.collectLatest { darkTheme = it }
        }
    }
}