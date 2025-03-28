package com.test.bookplayer.ui.components

import androidx.annotation.FloatRange
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


/**
 * Clickable modifier but with disable ripple effect. See [clickable]
 *
 * @param enabled Controls the enabled state. When `false`, [onClick], and this modifier will
 * appear disabled for accessibility services
 * @param onClickLabel semantic / accessibility label for the [onClick] action
 * @param role the type of user interface element. Accessibility services might use this
 * to describe the element or do customizations
 * @param onClick will be called when user clicks on the element
 * @return new modifier with applied [clickable] modifier but without ripple effect
 */
fun Modifier.clickableNoRipple(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit,
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "clickable"
        properties["enabled"] = enabled
        properties["onClickLabel"] = onClickLabel
        properties["role"] = role
        properties["onClick"] = onClick
    },
) {
    Modifier.clickable(
        enabled = enabled,
        onClickLabel = onClickLabel,
        onClick = onClick,
        role = role,
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
    )
}

/**
 * Scale the contents of the composable by the following scale factors along the horizontal
 * and vertical axis respectively. Negative scale factors can be used to mirror content across
 * the corresponding horizontal or vertical axis.
 *
 * @param scaleX Multiplier to scale content along the horizontal axis
 * @param scaleY Multiplier to scale content along the vertical axis
 * @param pivotFractionX position along the x-axis that should be used as the origin for scale
 * transformations.This is represented as a fraction of the width of the content.
 * A value of 0.5f represents the midpoint between the left and right bounds of the content
 * @param pivotFractionY position along the y-axis that should be used as the origin for scale
 * transformations. This is represented as a fraction of the height of the content.
 * A value of 0.5f represents the midpoint between the top and bottom bounds of the content
 */
fun Modifier.scale(
    scaleX: Float,
    scaleY: Float = scaleX,
    @FloatRange(from = 0.0, to = 1.0) pivotFractionX: Float,
    @FloatRange(from = 0.0, to = 1.0) pivotFractionY: Float = pivotFractionX,
): Modifier {
    return if (scaleX != 1.0f || scaleY != 1.0f) {
        graphicsLayer(
            scaleX = scaleX,
            scaleY = scaleY,
            transformOrigin = TransformOrigin(pivotFractionX, pivotFractionY),
        )
    } else {
        this
    }
}

/**
 * Add vertical shadows for [androidx.compose.foundation.lazy.LazyColumn].
 * The shadow at the top appears dynamically when scrolling down, while the shadow at the bottom
 * is static and visible all the time
 *
 * @param scrollState state of the [androidx.compose.foundation.lazy.LazyColumn] to which
 * the modifier will be applied
 * @param topShadowColor colour of the shadow at the top of the column
 * @param bottomShadowColor colour of the shadow at the bottom of the column
 * @param shadowHeight height of top and bottom shadows. Default: 8 dp
 *
 */
fun Modifier.scrollShadows(
    scrollState: LazyListState,
    topShadowColor: Color = Color.Gray.copy(alpha = 0.2f),
    bottomShadowColor: Color = Color.Gray.copy(alpha = 0.2f),
    shadowHeight: Dp = 8.dp,
): Modifier = composed {
    val density = LocalDensity.current
    val shadowHeightPx = with(density) { shadowHeight.toPx() }

    val bottomShadowHeightPx = remember { mutableFloatStateOf(shadowHeightPx) }
    val topShadowHeightPx = remember {
        derivedStateOf {
            if (scrollState.firstVisibleItemIndex == 0) {
                minOf(scrollState.firstVisibleItemScrollOffset.toFloat(), shadowHeightPx)
            } else {
                shadowHeightPx
            }
        }
    }

    scrollShadows(
        topShadowColor = topShadowColor,
        bottomShadowColor = bottomShadowColor,
        topShadowHeightPx = topShadowHeightPx,
        bottomShadowHeightPx = bottomShadowHeightPx,
    )
}

/**
 * Canvas shadow drawer for top and bottom of the composable
 *
 * @param topShadowColor colour of the shadow at the top of the composable
 * @param bottomShadowColor colour of the shadow at the bottom of the composable
 * @param topShadowHeightPx height in pixels of the shadow at the top
 * @param bottomShadowHeightPx height in pixels of the shadow at the bottom
 *
 */
private fun Modifier.scrollShadows(
    topShadowColor: Color,
    bottomShadowColor: Color,
    topShadowHeightPx: State<Float>,
    bottomShadowHeightPx: State<Float>,
): Modifier {
    return drawWithCache {
        val topGradient = Brush.verticalGradient(
            colors = listOf(topShadowColor, Color.Transparent),
            endY = topShadowHeightPx.value,
        )

        val bottomGradient = Brush.verticalGradient(
            colors = listOf(bottomShadowColor, Color.Transparent),
            startY = size.height,
            endY = size.height - bottomShadowHeightPx.value,
        )

        onDrawWithContent {
            drawContent()
            drawRect(
                brush = topGradient,
                size = Size(size.width, topShadowHeightPx.value),
            )
            drawRect(
                brush = bottomGradient,
                topLeft = Offset(0f, size.height - bottomShadowHeightPx.value),
                size = Size(size.width, bottomShadowHeightPx.value),
            )
        }
    }
}

/**
 *  Rectangular dimming rendered with [Modifier.drawWithContent]
 *
 * @param color colour of the dimming
 * @param alpha the alpha value to be applied to the dimming
 */
fun Modifier.dim(
    color: Color = Color.Black,
    alpha: () -> Float = { 0.3f },
): Modifier {
    return drawWithContent {
        drawContent()
        drawRect(
            color = color,
            alpha = alpha(),
        )
    }
}

/**
 * Apply given [modifier] only if [condition] is met
 */
inline fun Modifier.conditional(
    condition: Boolean,
    modifier: Modifier.() -> Modifier,
): Modifier {
    return if (condition) then(modifier(Modifier)) else this
}

/**
 * Creates a [graphicsLayer] that draws a shadow. The [elevation] defines the visual
 * depth of the physical object. The physical object has a shape specified by [shape].
 *
 * If the passed [shape] is concave the shadow will not be drawn on Android versions less than 10.
 *
 * Note that [elevation] is only affecting the shadow size and doesn't change the drawing order.
 * Use a [androidx.compose.ui.zIndex] modifier if you want to draw the elements with larger
 * [elevation] after all the elements with a smaller one.
 *
 * Usage of this API renders this composable into a separate graphics layer
 * @see graphicsLayer
 *
 * Example usage:
 *
 * @sample androidx.compose.ui.samples.ShadowSample
 *
 * @param elevation The elevation for the shadow in pixels
 * @param shape Defines a shape of the physical object
 * @param color Color of the shadow effect
 * @param clip When active, the content drawing clips to the shape.
 */
fun Modifier.shadow(
    elevation: Dp,
    clip: Boolean = elevation > 0.dp,
    shape: Shape = RectangleShape,
    color: Color = DefaultShadowColor,
): Modifier {
    return shadow(
        elevation = elevation,
        shape = shape,
        clip = clip,
        ambientColor = color,
        spotColor = color,
    )
}
