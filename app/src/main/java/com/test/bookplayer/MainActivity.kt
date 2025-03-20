package com.test.bookplayer

import android.content.ComponentName
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.test.bookplayer.ui.theme.BookPlayerTheme
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class MainActivity : ComponentActivity() {
    private lateinit var _mediaController: MediaController
    private val mediaController: MediaController?
        get() = if (this::_mediaController.isInitialized) _mediaController else null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            BookPlayerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Screen(
                        onPlayClick = {
                            mediaController?.play()
                        },
                        onPauseClick = {
                            mediaController?.pause()
                        },
                        onSeekBy = { seek ->
                            mediaController?.run {
                                val position = currentPosition + seek.inWholeMilliseconds
                                if (position in 0..duration) {
                                    seekTo(position)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    )
                }
            }
        }
    }

    @Composable
    fun Screen(
        onPlayClick: () -> Unit,
        onPauseClick: () -> Unit,
        onSeekBy: (seek: Duration) -> Unit,
        modifier: Modifier = Modifier
    ) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                Button(onClick = onPlayClick) {
                    Text("Play")
                }
                Button(onClick = onPauseClick) {
                    Text("Pause")
                }
                Button(onClick = { onSeekBy(10.seconds) }) {
                    Text("Seek +10")
                }
                Button(onClick = { onSeekBy((-10).seconds) }) {
                    Text("Seek -10")
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val sessionToken = SessionToken(this, ComponentName(this, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture.addListener(
            {
                _mediaController = controllerFuture.get().apply {
                    setMediaItems(List(size = 3, init = ::createMediaItem))
                    prepare()
                }
            },
            MoreExecutors.directExecutor()
        )
    }

    private fun createMediaItem(
        index: Int,
    ) = MediaItem.Builder()
        .setMediaId("media-$index")
        .setUri(VIDEO_URI)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setArtist("Artist $index")
                .setTitle("Media $index")
                .build()
        )
        .build()

    @Preview
    @Composable
    private fun Preview() {
        BookPlayerTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Screen(
                    onPlayClick = {},
                    onPauseClick = {},
                    onSeekBy = {},
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }
        }
    }

    companion object {
        private const val VIDEO_URI =
            "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    }
}

