package com.example.instrumenttrainer.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

private data class HaloSpec(
    val pad: Dp,
    val breatheMs: Int,
    val wobbleMs: Int,
    val scaleRange: ClosedFloatingPointRange<Float>,
    val scaleYRange: ClosedFloatingPointRange<Float>,
    val opacityRange: ClosedFloatingPointRange<Float>,
    val borderWidth: Dp,
    val rotateRange: ClosedFloatingPointRange<Float>,
)

private val HALO_SPECS = listOf(
    HaloSpec(4.dp, 1650, 920, 1f..1.12f, 1.04f..0.94f, 0.2f..0.42f, 1.2.dp, -5f..6f),
    HaloSpec(12.dp, 2000, 740, 1.03f..1.17f, 0.94f..1.06f, 0.14f..0.32f, 1.dp, 7f..-4f),
    HaloSpec(22.dp, 2350, 1080, 1.05f..1.2f, 1.05f..0.92f, 0.1f..0.24f, 0.8.dp, -8f..9f),
)

@Composable
fun BreathingHaloField(
    size: Dp,
    ringRadius: Dp,
    color: Color,
    amplitude: Float,
    modifier: Modifier = Modifier,
) {
    if (amplitude < 0.015f) return

    val intensity = amplitude.coerceIn(0f, 1f)
    val speedFactor = max(0.35f, 1f - intensity * 0.65f)
    val ringDiameter = ringRadius * 2 + 4.dp

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        HALO_SPECS.forEachIndexed { index, spec ->
            BreathingHalo(
                spec = spec,
                ringDiameter = ringDiameter,
                color = color,
                speedFactor = speedFactor,
                phaseOffset = index * 0.33f,
            )
        }
    }
}

@Composable
private fun BreathingHalo(
    spec: HaloSpec,
    ringDiameter: Dp,
    color: Color,
    speedFactor: Float,
    phaseOffset: Float,
) {
    val transition = rememberInfiniteTransition(label = "halo")
    val breatheDuration = (spec.breatheMs * speedFactor).toInt().coerceAtLeast(400)
    val wobbleDuration = (spec.wobbleMs * speedFactor).toInt().coerceAtLeast(300)

    val breathe by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(breatheDuration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "breathe",
    )
    val wobble by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(wobbleDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "wobble",
    )

    val adjustedBreathe = ((breathe + phaseOffset) % 1f).let { if (it < 0f) it + 1f else it }
    val scale = lerp(spec.scaleRange.start, spec.scaleRange.endInclusive, adjustedBreathe)
    val scaleY = lerp(spec.scaleYRange.start, spec.scaleYRange.endInclusive, adjustedBreathe) *
        lerp(1f, if (wobble < 0.5f) 1.05f else 0.96f, wobble)
    val opacity = lerp(spec.opacityRange.start, spec.opacityRange.endInclusive, adjustedBreathe)
    val rotate = lerp(spec.rotateRange.start, spec.rotateRange.endInclusive, wobble)
    val translateX = lerp(0f, if (wobble < 0.5f) 3.5f else -2.5f, wobble)
    val translateY = lerp(0f, -3f, adjustedBreathe)
    val haloSize = ringDiameter + spec.pad * 2

    Box(
        modifier = Modifier
            .size(haloSize)
            .graphicsLayer {
                this.scaleX = scale
                this.scaleY = scaleY
                this.alpha = opacity
                this.rotationZ = rotate
                this.translationX = translateX
                this.translationY = translateY
            }
            .border(spec.borderWidth, color.copy(alpha = 0.82f), CircleShape),
    )
}

private fun lerp(start: Float, end: Float, fraction: Float): Float =
    start + (end - start) * fraction.coerceIn(0f, 1f)
