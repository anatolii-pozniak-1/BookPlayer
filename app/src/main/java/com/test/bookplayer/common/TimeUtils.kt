package com.test.bookplayer.common

import java.util.Locale

fun Long.formatToMMSS(): String {
    val minutes = this / 60000
    val seconds = (this % 60000) / 1000
    return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}
