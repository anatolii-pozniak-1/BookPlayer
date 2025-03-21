import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Material2StyledSlider(
    sliderPosition: Float,
    colors: SliderColors,
    modifier: Modifier = Modifier,
    onValueChange: (Float) -> Unit = {},
) {

    Slider(
        value = sliderPosition,
        onValueChange = onValueChange,
        colors = colors,
        track = { sliderState ->
            SliderDefaults.Track(
                modifier = Modifier.height(4.dp),
                sliderState = sliderState,
                drawStopIndicator = null,
                thumbTrackGapSize = 0.dp,
                colors = colors
            )
        },
        thumb = {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(colors.thumbColor, CircleShape)
            )
        },
        modifier = modifier,
    )
}

@Preview
@Composable
private fun Material2StyledSliderPreview() {
    Material2StyledSlider(
        sliderPosition = 0.2f,
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.primary,
            activeTrackColor = MaterialTheme.colorScheme.primary,
            inactiveTrackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
            activeTickColor = Color.Transparent,
            inactiveTickColor = Color.Transparent
        ),

    )
}

