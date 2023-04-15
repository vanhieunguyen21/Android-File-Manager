package com.ryan.filemanager.ui.screen

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryan.filemanager.app.BaseApplication
import com.ryan.filemanager.data.*
import com.ryan.filemanager.model.SortOrder
import com.ryan.filemanager.model.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

private const val TAG = "BrowserViewModel"

@HiltViewModel
class BrowserViewModel @Inject constructor(
    private val storageRepository: StorageRepository,
    private val app: BaseApplication,
) : ViewModel() {
    private val storageVolumes: List<File> = storageRepository.getStorageVolumes()

    private val sortTypeFlow: Flow<SortType> = getSortType(app)
    var sortType by mutableStateOf(SortType.Name)
        private set

    private val sortOrderFlow: Flow<SortOrder> = getSortOrder(app)
    var sortOrder by mutableStateOf(SortOrder.Ascending)
        private set

    private val _currentPathSegments = mutableListOf<String>()
    val currentPathSegment: List<String> = _currentPathSegments

    var currentPath by mutableStateOf("")
        private set

    var canGoBack by mutableStateOf(false)
        private set

    var files: List<File> by mutableStateOf(listOf())
        private set

    init {
        // Init file lists with root volumes
        setFileList(storageVolumes)
        // If there is only one volume, set it as root instead
        if (storageVolumes.size == 1) {
            goToDirectory(files[0])
        }

        // Collect sort settings
        viewModelScope.launch {
            sortTypeFlow.collectLatest {
                if (it != sortType) {
                    sortType = it
                    setFileList(files)
                }
            }
        }
        viewModelScope.launch {
            sortOrderFlow.collectLatest {
                if (it != sortOrder) {
                    sortOrder = it
                    setFileList(files)
                }
            }
        }
    }

    private fun setFileList(files: List<File>) {
        this.files = when(sortType) {
            SortType.Name -> when(sortOrder) {
                SortOrder.Ascending -> files.sortedWith(compareByDescending<File> { it.isDirectory }.thenBy { it.name })
                SortOrder.Descending -> files.sortedWith(compareByDescending<File> { it.isDirectory }.thenByDescending { it.name })
            }
            SortType.LastModified -> when(sortOrder) {
                SortOrder.Ascending -> files.sortedWith(compareByDescending<File> { it.isDirectory }.thenBy { it.lastModified() })
                SortOrder.Descending -> files.sortedWith(compareByDescending<File> { it.isDirectory }.thenByDescending { it.lastModified() })
            }
            SortType.Size -> when(sortOrder) {
                SortOrder.Ascending -> files.sortedWith(compareByDescending<File> { it.isDirectory }
                    .thenComparing { f1, f2 ->
                        if (f1.isDirectory && f2.isDirectory)
                            return@thenComparing f1.name.compareTo(f2.name) // Sort by name asc instead of size for directories
                        else return@thenComparing (f1.length() - f2.length()).toInt()
                    })
                SortOrder.Descending -> files.sortedWith(compareByDescending<File> { it.isDirectory }
                    .thenComparing { f1, f2 ->
                        if (f1.isDirectory && f2.isDirectory)
                            return@thenComparing f1.name.compareTo(f2.name) // Sort by name asc instead of size for directories
                        else return@thenComparing (f2.length() - f1.length()).toInt()
                    })
            }
        }
    }

    fun goToDirectory(directory: File) {
        viewModelScope.launch {
            if (_currentPathSegments.isEmpty()) {
                // First path segment is always volume path, take its full path
                _currentPathSegments.add(directory.path)
                // Do not enable go back if there is only one volume and current in the root
                canGoBack = storageVolumes.size != 1
            } else {
                _currentPathSegments.add(directory.name)
                canGoBack = true
            }
            currentPath = _currentPathSegments.joinToString("/")
            Log.d(TAG, "goToDirectory: $currentPath")
            setFileList(storageRepository.listFiles(currentPath))
        }
    }

    fun goBack() {
        viewModelScope.launch {
            // Do not go back if already in root
            if (_currentPathSegments.isEmpty()) return@launch
            // Do not go back if at the root of the only volume
            if (storageVolumes.size == 1 && _currentPathSegments.size == 1) return@launch

            _currentPathSegments.removeLast()
            currentPath = _currentPathSegments.joinToString("/")
            if (_currentPathSegments.isEmpty()) {
                setFileList(storageVolumes)
                // Do not enable go back if already in root
                canGoBack = false
            } else {
                setFileList(storageRepository.listFiles(currentPath))
                // Do not enable go back if there is only one volume and currently in the root
                canGoBack = !(storageVolumes.size == 1 && _currentPathSegments.size == 1)
            }
        }
    }

    fun onSortTypeChange(sortType: SortType) {
        viewModelScope.launch { saveSortType(app, sortType) }
    }

    fun onSortOrderChange(sortOrder: SortOrder) {
        viewModelScope.launch { saveSortOrder(app, sortOrder) }
    }

    fun toogleDarkTheme() {
        viewModelScope.launch { toggleDarkTheme(app) }
    }
}