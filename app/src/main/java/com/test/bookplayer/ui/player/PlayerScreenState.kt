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
    val keyPointText: String,
) {

    val hasNext get() = keyPoint + 1 <= keyPointsTotal
    val hasPrev get() = keyPoint - 1 > 0

    companion object {
        val initialState = Summary(
            keyPointsTotal = 0,
            keyPoint = 0,
            keyPointSubTitle = "",
            keyPointText = "",
        )
    }
}
