package com.test.bookplayer.ui.player

import Material2StyledSlider
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
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.test.bookplayer.R
import com.test.bookplayer.ui.components.DynamicTabSelector
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Composable
fun PlayerScreen(
    state: State<MediaScreenState>,
    postAction: (PlayerScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentIndex by remember { derivedStateOf { state.value.contentIndex } }
    val pagerState = rememberPagerState(initialPage = contentIndex) { 2 }
    val coroutineScope = rememberCoroutineScope()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .padding(top = 32.dp, bottom = 24.dp),
        ) { index ->
            if (index == 0) {
                AudioContent(
                    state = state,
                    postAction = postAction,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                TextContent(
                    state = state,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        DynamicTabSelector(
            selectedOption = contentIndex,
            selectorHeight = 56.dp,
            icons = listOf(R.drawable.ic_headphones, R.drawable.ic_short_text),
            onTabSelected = {
                postAction(PlayerScreenEvent.OnPageChanged(it))
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
    state: State<MediaScreenState>,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier,
    ) {
    }
}

@Composable
private fun AudioContent(
    state: State<MediaScreenState>,
    postAction: (PlayerScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cover by remember { derivedStateOf { state.value.cover } }
    val isPlaying by remember { derivedStateOf { state.value.mediaStatus.isPlaying } }
    val summary = remember { derivedStateOf { state.value.summary } }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = 16.dp)
    ) {
        BookCoverSection(
            cover = cover,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .weight(0.6f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        KeyPointSection2(
            summary = summary.value,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        AudioProgressBar(
            state = state
        )
        Spacer(modifier = Modifier.height(12.dp))
        SpeedChip(
            text = "Speed x1",
            onClick = { postAction(PlayerScreenEvent.OnMediaSpeedClicked) })
        Spacer(modifier = Modifier.height(16.dp))
        PlaybackControls(
            isPlaying = isPlaying,
            onPlayPauseClick = { postAction(PlayerScreenEvent.OnMediaPlayPauseClicked) },
            onSeekByClick = { seek -> postAction(PlayerScreenEvent.OnMediaSeekByClicked(seek)) },
            onPreviousClick = { postAction(PlayerScreenEvent.OnMediaPreviousClicked) },
            onNextClick = { postAction(PlayerScreenEvent.OnMediaNextClicked) },
        )
    }
}


@Composable
private fun BookCoverSection(
    cover: String,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = cover,
        contentDescription = "Book Cover",
        contentScale = ContentScale.FillHeight,
        modifier = modifier
    )
}

@Composable
private fun KeyPointSection2(
    summary: Summary,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(
                R.string.key_point_sub_title,
                summary.keyPoint,
                summary.keyPointsTotal
            ),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = summary.keyPointSubTitle,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            minLines = 2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AudioProgressBar(
    state: State<MediaScreenState>,
    modifier: Modifier = Modifier,
) {
    val mediaStatus = state.value.mediaStatus

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = mediaStatus.progressFormatted,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Material2StyledSlider(
            sliderPosition = mediaStatus.progress,
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
            text = mediaStatus.durationFormatted,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        )
    }
}

@Composable
private fun PlaybackControls(
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onSeekByClick: (seek: Duration) -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPreviousClick,
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
            onClick = { onSeekByClick((-5).seconds) },
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
            onClick = { onPlayPauseClick() },
            modifier = Modifier.size(72.dp),
        ) {
            Icon(
                painter = if (isPlaying) {
                    painterResource(R.drawable.ic_pause)
                } else {
                    rememberVectorPainter(Icons.Default.PlayArrow)
                },
                contentDescription = "Play/Pause",
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(64.dp),
            )
        }
        IconButton(
            onClick = { onSeekByClick(10.seconds) },
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
            onClick = onNextClick,
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