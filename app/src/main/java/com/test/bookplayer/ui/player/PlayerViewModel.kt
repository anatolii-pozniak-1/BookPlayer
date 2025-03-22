package com.test.bookplayer.ui.player

import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.test.bookplayer.base.BaseViewModel
import com.test.bookplayer.common.formatToMMSS
import com.test.bookplayer.data.AudioBook
import com.test.bookplayer.data.BooksDataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class PlayerViewModel : BaseViewModel<MediaScreenState>(
    initialUiState = MediaScreenState.initialState
) {
    private var mediaController: MediaController? = null
    private var _audioBook: AudioBook? = null

    init {
        val audioBook = BooksDataStore.books.first()
        _audioBook = audioBook

        initState(audioBook = audioBook)
        setupMediaSyncTimer()
        setupMediaContent(audioBook)
    }

    fun handleEvent(event: PlayerScreenEvent) {
        when (event) {
            is PlayerScreenEvent.OnMediaPlayPauseClicked -> doOnPlayPauseClicked()
            is PlayerScreenEvent.OnMediaSeekByClicked -> doOnSeekByClicked(event.duration)
            is PlayerScreenEvent.OnMediaSpeedClicked -> TODO()
            is PlayerScreenEvent.OnMediaNextClicked -> nextMedia()
            is PlayerScreenEvent.OnMediaPreviousClicked -> prevMedia()

            is PlayerScreenEvent.OnPageChanged -> changePage(event.index)
        }
    }

    private fun setupMediaSyncTimer() {
        viewModelScope.launch {
            while (isActive) {
                mediaController?.let { mediaController ->
                    setState { copy(mediaStatus = mediaController.mediaStatus) }
                }
                delay(1.seconds)
            }
        }
    }

    private val MediaController.mediaStatus: MediaStatus
        get() {
            val total = max(duration, 0L)
            val progress = if (total == 0L) 0f else currentPosition.toFloat() / total
            return MediaStatus(
                progressFormatted = currentPosition.formatToMMSS(),
                durationFormatted = total.formatToMMSS(),
                progress = progress,
                isPlaying = isPlaying
            )
        }

    private fun doOnPlayPauseClicked() {
        setState {
            if (mediaStatus.isPlaying) {
                mediaController?.pause()
            } else {
                mediaController?.play()
            }
            copy(
                mediaStatus = mediaStatus.copy(isPlaying = !mediaStatus.isPlaying)
            )
        }
    }

    private fun doOnSeekByClicked(seek: Duration) {
        mediaController?.run {
            val position = currentPosition + seek.inWholeMilliseconds
            if (position in 0..duration) {
                seekTo(position)
            }
        }
    }

    private fun changePage(index: Int) = setState { copy(contentIndex = index) }

    private fun nextMedia() {
        val summary = uiState.value.summary
        if (summary.hasNext.not()) return
        mediaController?.seekToNextMediaItem()
        changeMediaItem(index = summary.keyPoint)
    }

    private fun prevMedia() {
        val summary = uiState.value.summary
        if (summary.hasPrev.not()) return
        mediaController?.seekToPreviousMediaItem()
        changeMediaItem(index = summary.keyPoint - 2)
    }

    private fun changeMediaItem(index: Int) {
        setState {
            copy(
                summary = summary.run {
                    copy(
                        keyPoint = index + 1,
                        keyPointSubTitle = _audioBook?.summary?.getOrNull(index)?.description.orEmpty()
                    )
                }
            )
        }
    }

    private fun initState(audioBook: AudioBook) {
        val keyPoint = audioBook.summary.getOrNull(0) ?: return
        setState {
            copy(
                cover = audioBook.cover,
                contentIndex = 0,
                summary = Summary(
                    keyPointsTotal = audioBook.summary.size,
                    keyPoint = 1,
                    keyPointSubTitle = keyPoint.description,
                )
            )
        }
    }

    fun setMediaController(mediaController: MediaController) {
        this.mediaController = mediaController
        _audioBook?.let { audioBook ->
            setupMediaContent(audioBook)
            mediaController.addListener(
                object : Player.Listener {
                    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                        changeMediaItem(mediaController.currentMediaItemIndex)
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        setState {
                            copy(mediaStatus = mediaStatus.copy(isPlaying = isPlaying))
                        }
                    }
                },
            )
        }
    }

    fun releaseMediaController() = mediaController?.release()

    private fun setupMediaContent(audioBook: AudioBook) {
        mediaController?.run {
            if (mediaItemCount == 0) {
                setMediaItems(audioBook.mediaItems)
                prepare()
            } else {
                changeMediaItem(index = currentMediaItemIndex)
            }
        }
    }
}
