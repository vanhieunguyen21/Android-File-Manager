package com.ryan.filemanager.model

enum class SortType {
    Name, LastModified, Size;

    companion object {
        fun fromString(str: String) : SortType {
            return when(str) {
                Name.toString() -> Name
                LastModified.toString() -> LastModified
                Size.toString() -> Size
                else -> Name
            }
        }
    }
}