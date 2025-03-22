package com.test.bookplayer.ui.player

import androidx.compose.runtime.Stable

@Stable
data class MediaScreenState(
    val description: String,
    val mediaStatus: MediaStatus,
    val cover: String,
    val summary: Summary,
    val contentIndex: Int,
) {
    companion object {
        val initialState = MediaScreenState(
            description = "",
            mediaStatus = MediaStatus.initialState,
            cover = "",
            summary = Summary.initialState,
            contentIndex = 0,
        )
    }
}

@Stable
data class MediaStatus(
    val durationFormatted: String,
    val progressFormatted: String,
    val progress: Float,
    val isPlaying: Boolean = false,
) {
    companion object {
        val initialState = MediaStatus(
            durationFormatted = "00:00",
            progressFormatted = "00:00",
            progress = 0f,
            isPlaying = false,
        )
    }
}

@Stable
data class Summary(
    val keyPointsTotal: Int,
    val keyPoint: Int,
    val keyPointSubTitle: String,
) {

    val hasNext get() = keyPoint + 1 <= keyPointsTotal
    val nextIndex get() = if (keyPoint + 1 <= keyPointsTotal) keyPoint else keyPoint - 1
    val hasPrev get() = keyPoint - 1 > 0
    val prevIndex get() = if (keyPoint - 1 > 0) keyPoint - 2 else keyPoint - 1

    companion object {
        val initialState = Summary(
            keyPointsTotal = 0,
            keyPoint = 0,
            keyPointSubTitle = "",
        )
    }
}
