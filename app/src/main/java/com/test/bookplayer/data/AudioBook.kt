package com.test.bookplayer.data

data class AudioBook(
    val title: String,
    val author: String,
    val cover: String,
    val description: String,
    val summary: List<SummaryKeyPoint>,
)
