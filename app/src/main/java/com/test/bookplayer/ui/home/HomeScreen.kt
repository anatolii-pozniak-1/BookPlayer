package com.test.bookplayer.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.test.bookplayer.R
import com.test.bookplayer.data.AudioContent
import com.test.bookplayer.data.SummaryKeyPoint
import com.test.bookplayer.data.TextContent
import com.test.bookplayer.ui.components.scrollShadows
import com.test.bookplayer.ui.theme.BookPlayerTheme

@Composable
fun HomeScreen(
    state: State<HomeScreenState>,
    postEvent: (HomeScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.size(48.dp))
        LazyRow(
            contentPadding = PaddingValues(12.dp)
        ) {
            itemsIndexed(
                items = state.value.audioBooks,
                key = { _, item -> item.title.hashCode() },
            ) { index, item ->
                if (index != 0) {
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Book(
                    imageUrl = item.cover,
                    title = item.title,
                    onClick = { postEvent(HomeScreenEvent.OnBookClicked(item)) },
                    modifier = Modifier.width(200.dp)

                )
            }

        }
        val listState = rememberLazyListState()
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            state = listState,
            modifier = Modifier
                .scrollShadows(
                    scrollState = listState,
                    topShadowColor = Color.Gray.copy(0.1f),
                    bottomShadowColor = Color.Transparent,
                )
        ) {
            itemsIndexed(
                items = state.value.keyPoints,
                key = { _, item -> item.title.hashCode() },
            ) { index, item ->
                if (index != 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Chapter(
                    position = index + 1,
                    state = item,
                    onClick = { postEvent(HomeScreenEvent.OnChapterClicked(item)) },
                    onPlayPauseClick = {
                        postEvent(HomeScreenEvent.OnChapterPlayPauseClicked(item))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun Book(
    imageUrl: String,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.Transparent,
        onClick = onClick,
        modifier = modifier,
    ) {
        Column {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Book cover",
                placeholder = painterResource(R.drawable.image_placeholder),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .aspectRatio(ratio = 1f)
                    .fillMaxSize()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                maxLines = 2,
                minLines = 2,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp, end = 4.dp)
            )
        }
    }
}

@Composable
private fun Chapter(
    position: Int,
    state: SummaryKeyPoint,
    onClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
//    imageUrl = state.value.selectedBook.cover,
//    title = "%d. %s".format(index + 1, item.description),
//    time = "mar 23, 2025 * 24 mins",
    Surface(
        shape = RoundedCornerShape(12.dp),
        onClick = onClick,
    ) {
        Column(modifier = modifier) {
            Row(
                modifier = Modifier
            ) {
                Text(
                    text = "%d. %s".format(position, state.title),
                    maxLines = 2,
                    minLines = 1,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(bottom = 6.dp, end = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FilledTonalIconButton(
                    onClick = onPlayPauseClick,
                    modifier = Modifier
                ) {
                    Icon(
                        painter = rememberVectorPainter(Icons.Rounded.PlayArrow),
                        contentDescription = "",
                    )
                }
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    text = "mar 23, 2025 * 24 mins",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier,

                    )
            }
        }
    }
}

@Preview
@Composable
private fun BookPreview() {
    Surface {
        Row {
            repeat(2) {
                Book(
                    imageUrl = "https://images.unsplash.com/photo-1630486316787-4b3b3b3b3b3b",
                    title = "Title $it",
                    modifier = Modifier.size(width = 200.dp, height = 300.dp),
                    onClick = {},
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Preview
@Composable
private fun BookCardLongPreview() {
    BookPlayerTheme {
        Surface {
            Row {
                repeat(2) {
                    Chapter(
                        onClick = {},
                        onPlayPauseClick = {},
                        modifier = Modifier.fillMaxWidth(),
                        position = it,
                        state = SummaryKeyPoint(
                            title = "Title $it",
                            audioContent = AudioContent(mediaMetadata = "", mediaId = "", uri = ""),
                            textContent = TextContent(""),
                        ),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}