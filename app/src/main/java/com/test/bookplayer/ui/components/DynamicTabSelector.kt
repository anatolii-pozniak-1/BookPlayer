package com.test.bookplayer.ui.components

import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.test.bookplayer.R
import com.test.bookplayer.ui.theme.BookPlayerTheme

@Composable
fun DynamicTabSelector(
    icons: List<Int> = emptyList(),
    selectedOption: Int = 0,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    tabColor: Color = MaterialTheme.colorScheme.primary,
    selectedIconColor: Color = MaterialTheme.colorScheme.onPrimary,
    defaultIconColor: Color = MaterialTheme.colorScheme.onSurface,
    selectorHeight: Dp = 48.dp,
    spacing: Dp = 4.dp,
    onTabSelected: (selectedIndex: Int) -> Unit = {}
) {
    if (icons.size != 2) {
        throw IllegalArgumentException("DynamicTabSelector must have between 2 options")
    }

    // Use BoxWithConstraints to get the width of the container
    BoxWithConstraints(
        modifier = Modifier
            .clip(RoundedCornerShape(selectorHeight))
            .height(selectorHeight)
            .width(selectorHeight * 2)
            .background(containerColor)
            .border(
                (0.5).dp,
                color = defaultIconColor.copy(alpha = 0.3f),
                shape = RoundedCornerShape(selectorHeight)
            )
    ) {
        val segmentWidth = maxWidth / icons.size
        // Adjusted width for each tab, accounting for spacing
        val boxWidth = segmentWidth - spacing * 2
        val positions = icons.indices.map { index ->
            segmentWidth * index + (segmentWidth - boxWidth) / 2
        }
        // Animate the X offset of the selected tab to smoothly transition between tabs
        val animatedOffsetX by animateDpAsState(targetValue = positions[selectedOption], label = "")
        // Determine the maximum height available for alignment
        val containerHeight = maxHeight
        val tabHeight = containerHeight - spacing * 2
        // Center the tab selector vertically within the container
        val verticalOffset = (containerHeight - tabHeight) / 2

        Row(
            modifier = Modifier.fillMaxHeight(),
            // Ensures spacing between options
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icons.forEachIndexed { index, iconRes ->
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier
                        .width(segmentWidth)
                        .clickable(
                            indication = null,
                            // Avoids ripple effect for a cleaner look
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            // Trigger callback with the index of the selected tab
                            onTabSelected(index)
                        },
                    tint = defaultIconColor,
                )
            }
        }
        // Selected tab highlighted by applying the selected tab text style
        Box(
            modifier = Modifier
                // Position the selector dynamically based on the selected tab
                .offset(x = animatedOffsetX, y = verticalOffset)
                .clip(CircleShape)
                .width(boxWidth) // Updated box width
                .height(tabHeight)
                .background(tabColor)
        ) {
            Icon(
                painter = painterResource(id = icons[selectedOption]),
                contentDescription = null,
                modifier = Modifier.align(Alignment.Center),
                tint = selectedIconColor,
            )
        }
    }
}

@Preview
@Composable
fun DynamicTabSelectorPreview() {
    /**
     * This preview demonstrates the use of remember { mutableStateOf(0) } to maintain the
     * selected tab's state across recompositions in DynamicTabSelector, enabling dynamic
     * UI updates in Jetpack Compose.
     **/
    BookPlayerTheme {
        val selectedOption = remember { mutableIntStateOf(0) }
        val context = LocalContext.current
        Box(
            modifier = Modifier
                .background(Color.Gray)
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            DynamicTabSelector(
                icons = listOf(R.drawable.ic_headphones, R.drawable.ic_short_text),
                selectedOption = selectedOption.intValue
            ) {
                Toast.makeText(context, "Selected tab: ${it + 1}", Toast.LENGTH_SHORT).show()
                selectedOption.intValue = it
            }
        }
    }
}