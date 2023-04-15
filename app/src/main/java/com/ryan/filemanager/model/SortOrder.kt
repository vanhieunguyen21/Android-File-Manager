package com.ryan.filemanager.model

enum class SortOrder {
    Ascending, Descending;

    companion object {
        fun fromString(str: String): SortOrder {
            return when(str) {
                Ascending.toString() -> Ascending
                Descending.toString() -> Descending
                else -> Ascending
            }
        }
    }
}