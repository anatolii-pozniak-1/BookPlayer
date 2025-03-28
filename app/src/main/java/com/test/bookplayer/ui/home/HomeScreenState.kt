package com.test.bookplayer.ui.home

import com.test.bookplayer.data.AudioBook
import com.test.bookplayer.data.SummaryKeyPoint

data class HomeScreenState(
    val index: Int,
    val audioBooks: List<AudioBook>,
    val keyPoints: List<SummaryKeyPoint>,
) {
    val selectedBook get() = audioBooks[index]

    companion object HomeScreenState {
        val initial
            get() = HomeScreenState(
                index = 0,
                audioBooks = emptyList(),
                keyPoints = emptyList()
            )
    }


}