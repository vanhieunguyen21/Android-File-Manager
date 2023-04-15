package com.ryan.filemanager.util

import java.text.DateFormat
import java.util.*

fun getFullDateTimeString(timestamp: Long): String {
    return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
        .format(Date(timestamp))
}