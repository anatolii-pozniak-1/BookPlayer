package com.test.bookplayer.ui.player

import kotlin.time.Duration

sealed class PlayerScreenEvent {
    data object OnMediaPlayPauseClicked : PlayerScreenEvent()
    data object OnMediaSpeedClicked : PlayerScreenEvent()
    data object OnMediaNextClicked : PlayerScreenEvent()
    data object OnMediaPreviousClicked : PlayerScreenEvent()

    data class OnMediaSeekByClicked(val duration: Duration) : PlayerScreenEvent()
    data class OnPageChanged(val index: Int) : PlayerScreenEvent()
}
