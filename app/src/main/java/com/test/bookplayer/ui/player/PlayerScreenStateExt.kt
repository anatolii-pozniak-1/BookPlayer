package com.test.bookplayer.ui.player

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.test.bookplayer.data.AudioBook
import com.test.bookplayer.data.SummaryKeyPoint

val AudioBook.mediaItems get() = summary.map { it.toMediaItem(author) }

fun SummaryKeyPoint.toMediaItem(author: String): MediaItem {
    return MediaItem.Builder()
        .setMediaId(audioContent.uri)
        .setUri(audioContent.uri)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setArtist(author)
                .setTitle(title)
                .build()
        )
        .build()
}
