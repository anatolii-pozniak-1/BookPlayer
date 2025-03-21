package com.test.bookplayer

import Material2StyledSlider
import android.content.ComponentName
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.test.bookplayer.ui.components.DynamicTabSelector
import com.test.bookplayer.ui.theme.BookPlayerTheme
import kotlinx.coroutines.launch
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
                    MediaScreen(
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
                        onSpeedClicked = {},
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    )
                }
            }
        }
    }

    @Composable
    private fun MediaScreen(
        onPlayClick: () -> Unit,
        onPauseClick: () -> Unit,
        onSpeedClicked: () -> Unit,
        onSeekBy: (seek: Duration) -> Unit,
        modifier: Modifier = Modifier,
    ) {
        var selectedMediaType by remember { mutableIntStateOf(0) }
        val pagerState = rememberPagerState(initialPage = 0) { 2 }
        val coroutineScope = rememberCoroutineScope()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier,
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
                    .padding(top = 32.dp, bottom = 24.dp),
            ) { index ->
                if (index == 0) {
                    AudioContent(
                        onPlayClick = onPlayClick,
                        onPauseClick = onPauseClick,
                        onSpeedClicked = onSpeedClicked,
                        onSeekBy = onSeekBy,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    TextContent(
                        text = "Lorem ipsum",
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            DynamicTabSelector(
                selectedOption = selectedMediaType,
                selectorHeight = 56.dp,
                icons = listOf(R.drawable.ic_headphones, R.drawable.ic_short_text),
                onTabSelected = {
                    selectedMediaType = it
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(it)
                    }
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    @Composable
    private fun TextContent(
        text: String,
        modifier: Modifier = Modifier,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier,
        ) {
            Text(
                text = text,
            )
        }
    }

    @Composable
    private fun AudioContent(
        onPlayClick: () -> Unit,
        onPauseClick: () -> Unit,
        onSpeedClicked: () -> Unit,
        onSeekBy: (seek: Duration) -> Unit,
        modifier: Modifier = Modifier,
    ) {
        val progress by remember { mutableFloatStateOf(0.2f) }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .padding(horizontal = 16.dp)
        ) {
            BookCoverSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .weight(0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            KeyPointSection(
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            AudioProgressBar(
                progress = progress,
            )
            Spacer(modifier = Modifier.height(12.dp))
            SpeedChip(text = "Speed x1", onClick = onSpeedClicked)
            Spacer(modifier = Modifier.height(16.dp))
            PlaybackControls(
                onPlayClick = onPlayClick,
                onPauseClick = onPauseClick,
                onSeekBy = onSeekBy,
            )

        }
    }


    @Composable
    private fun BookCoverSection(
        modifier: Modifier = Modifier,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = "Book Cover",
            contentScale = ContentScale.FillHeight,
            modifier = modifier
        )
    }

    @Composable
    private fun KeyPointSection(
        modifier: Modifier = Modifier,
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "KEY POINT 2 OF 10",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Design is not how a thing looks, but how it works",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }

    @Composable
    private fun AudioProgressBar(
        progress: Float,
        modifier: Modifier = Modifier
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "00:28",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Material2StyledSlider(
                sliderPosition = progress,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                    activeTickColor = Color.Transparent,
                    inactiveTickColor = Color.Transparent
                ),
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "02:12",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            )
        }
    }

    @Composable
    private fun PlaybackControls(
        onPlayClick: () -> Unit,
        onPauseClick: () -> Unit,
        onSeekBy: (seek: Duration) -> Unit,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { /* Rewind 5s */ },
                modifier = Modifier.size(64.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_skip_previous),
                    contentDescription = "Skip Previous",
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(48.dp),
                )
            }
            IconButton(
                onClick = { onSeekBy((-5).seconds) },
                modifier = Modifier.size(64.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_replay_5),
                    contentDescription = "Rewind",
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(48.dp),
                )
            }
            IconButton(
                onClick = { onPlayClick() },
                modifier = Modifier.size(72.dp),
            ) {
                Icon(
                    painter = rememberVectorPainter(Icons.Default.PlayArrow),
                    contentDescription = "Play/Pause",
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(64.dp),
                )
            }
            IconButton(
                onClick = { onSeekBy(10.seconds) },
                modifier = Modifier.size(64.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_forward_10),
                    contentDescription = "Forward",
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(48.dp),
                )
            }
            IconButton(
                onClick = { /* Forward 10s */ },
                modifier = Modifier.size(64.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_skip_next),
                    contentDescription = "Skip next",
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(48.dp),
                )
            }
        }
    }

    @Composable
    fun SpeedChip(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
    ) {
        FilledTonalButton(
            onClick = onClick,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.filledTonalButtonColors().copy(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.secondary
            ),
            modifier = modifier,
        ) {
            Text(
                text = text,
                fontWeight = FontWeight.SemiBold,
            )
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
                MediaScreen(
                    onPlayClick = {},
                    onPauseClick = {},
                    onSeekBy = {},
                    onSpeedClicked = {},
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

