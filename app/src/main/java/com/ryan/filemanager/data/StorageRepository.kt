package com.ryan.filemanager.data

import androidx.core.content.ContextCompat
import com.ryan.filemanager.app.BaseApplication
import java.io.File
import javax.inject.Inject

class StorageRepository @Inject constructor(
    private val app: BaseApplication
) {
    fun getStorageVolumes(): List<File> {
        return ContextCompat.getExternalFilesDirs(app, null)
            .toList()
            .map { File(it.absolutePath.replaceFirst(Regex("Android/.*"), "")) }
    }

    fun listFiles(path: String): List<File> {
        val parent = File(path)
        return parent.listFiles()?.toList() ?: listOf()
    }
}