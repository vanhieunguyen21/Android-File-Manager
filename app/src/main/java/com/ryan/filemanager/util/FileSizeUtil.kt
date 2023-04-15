package com.ryan.filemanager.util

import java.text.StringCharacterIterator

fun getFileSizeShortString(sizeByte: Long): String {
    if (-1000 < sizeByte && sizeByte < 1000) {
        return "$sizeByte B"
    }
    val ci = StringCharacterIterator("kMGTPE")
    var bytes = sizeByte
    while (bytes <= -999950 || bytes >= 999950) {
        bytes /= 1000
        ci.next()
    }
    return "%.2f %cB".format(bytes / 1000.0, ci.current())
}