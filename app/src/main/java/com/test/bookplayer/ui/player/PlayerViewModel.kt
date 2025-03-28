package com.test.bookplayer.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.test.bookplayer.common.formatToMMSS
import com.test.bookplayer.common.setState
import com.test.bookplayer.data.AudioBook
import com.test.bookplayer.data.BooksDataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class PlayerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MediaScreenState.initialState)
    val uiState = _uiState.asStateFlow()

    private val mediaStatusState = MutableStateFlow(MediaStatus.initialState)
    private val audioBookState = MutableStateFlow<AudioBook?>(null)
    private var mediaController: MediaController? = null

    init {
        loadAudioBook()
        setupScreenState()
        setupMediaSyncTimer()
    }

    private fun loadAudioBook() {
        viewModelScope.launch {
            delay(2.seconds)
            val audioBook = BooksDataStore.books.first()
            audioBookState.value = audioBook
            setupMediaContent(audioBook)
        }
    }

    private fun stateCombiner(
        mediaStatus: MediaStatus,
        audioBook: AudioBook?,
    ): MediaScreenState {
        val book = audioBook ?: return MediaScreenState.initialState
        val keyPoint =
            book.summary.getOrNull(mediaStatus.mediaIndex) ?: return MediaScreenState.initialState

        return MediaScreenState(
            cover = book.cover,
            contentIndex = 0,
            mediaStatus = mediaStatus,
            description = book.description,
            summary = Summary(
                keyPointsTotal = book.summary.size,
                keyPoint = mediaStatus.mediaIndex + 1,
                keyPointSubTitle = keyPoint.title,
                keyPointText = keyPoint.textContent.description
            )
        )
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
                mediaController?.mediaStatus?.let {
                    mediaStatusState.value = it
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
                mediaIndex = currentMediaItemIndex,
                isPlaying = isPlaying
            )
        }

    private fun doOnPlayPauseClicked() {
        mediaStatusState.setState {
            if (isPlaying) {
                mediaController?.pause()
            } else {
                mediaController?.prepare()
                mediaController?.play()
            }

            copy(isPlaying = !isPlaying)
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

    private fun changePage(index: Int) = _uiState.setState { copy(contentIndex = index) }

    private fun nextMedia() {
        val summary = uiState.value.summary
        if (summary.hasNext.not()) return
        mediaController?.seekToNextMediaItem()
        mediaStatusState.changeMediaIndex(index = summary.keyPoint)
    }

    private fun prevMedia() {
        val summary = uiState.value.summary
        if (summary.hasPrev.not()) return
        mediaController?.seekToPreviousMediaItem()
        mediaStatusState.changeMediaIndex(index = summary.keyPoint - 2)
    }

    private fun setupScreenState() {
        viewModelScope.launch {
            combine(
                flow = mediaStatusState,
                flow2 = audioBookState,
                transform = ::stateCombiner
            ).collect {
                _uiState.value = it
            }
        }
    }

    fun setMediaController(mediaController: MediaController) {
        this.mediaController = mediaController
        mediaController.addListener(
            object : Player.Listener {
                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    mediaStatusState.changeMediaIndex(mediaController.currentMediaItemIndex)
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    mediaStatusState.setState { copy(isPlaying = isPlaying) }
                }
            },
        )

        audioBookState.value?.let { audioBook ->
            setupMediaContent(audioBook)
        }
    }

    fun releaseMediaController() = mediaController?.release()

    private fun setupMediaContent(audioBook: AudioBook) {
        mediaController?.run {
            if (mediaItemCount == 0) {
                setMediaItems(audioBook.mediaItems)
            } else {
                mediaStatusState.value = mediaStatus.copy(
                    mediaIndex = currentMediaItemIndex
                )
            }
        }
    }

    private fun MutableStateFlow<MediaStatus>.changeMediaIndex(index: Int) =
        setState { copy(mediaIndex = index) }
}
