package com.test.bookplayer.ui.home

import com.test.bookplayer.data.AudioBook
import com.test.bookplayer.data.SummaryKeyPoint

sealed class HomeScreenEvent {
    data class OnBookClicked(val item: AudioBook) : HomeScreenEvent()
    data class OnChapterClicked(val item: SummaryKeyPoint) : HomeScreenEvent()
    data class OnChapterPlayPauseClicked(val item: SummaryKeyPoint) : HomeScreenEvent()
}
