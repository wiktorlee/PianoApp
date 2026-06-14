package com.example.instrumenttrainer.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.instrumenttrainer.ui.theme.AppTheme
import com.example.instrumenttrainer.ui.theme.BrandCoral

@Composable
fun NoteDetectionRing(
    noteText: String,
    sublabel: String?,
    progress: Float,
    ringColor: Color,
    amplitude: Float = 0f,
    modifier: Modifier = Modifier,
    ringSize: Dp = 236.dp,
    strokeWidth: Dp = 10.dp,
) {
    val extra = AppTheme.extra
    val ringRadius = (ringSize - strokeWidth) / 2
    val fieldSize = ringSize + 56.dp
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 500),
        label = "ringProgress",
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(fieldSize),
            contentAlignment = Alignment.Center,
        ) {
            BreathingHaloField(
                size = fieldSize,
                ringRadius = ringRadius,
                color = MaterialTheme.colorScheme.primary,
                amplitude = amplitude,
                modifier = Modifier.align(Alignment.Center),
            )

            Canvas(modifier = Modifier.size(ringSize)) {
                val stroke = strokeWidth.toPx()
                val diameter = size.minDimension - stroke
                val topLeft = Offset(
                    (size.width - diameter) / 2f,
                    (size.height - diameter) / 2f,
                )
                val arcSize = Size(diameter, diameter)

                drawArc(
                    color = extra.ringTrack,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke),
                )
                drawArc(
                    color = ringColor,
                    startAngle = -90f,
                    sweepAngle = 360f * animatedProgress,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = noteText,
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
                if (sublabel != null) {
                    Text(
                        text = sublabel,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp),
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Composable
fun AmplitudeBar(
    amplitude: Float,
    modifier: Modifier = Modifier,
    height: Dp = 14.dp,
) {
    val extra = AppTheme.extra
    val clamped = amplitude.coerceIn(0f, 1f)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val corner = size.height / 2f
            drawRoundRect(
                color = extra.ringTrack,
                cornerRadius = CornerRadius(corner, corner),
            )
            if (clamped > 0f) {
                drawRoundRect(
                    color = BrandCoral,
                    size = Size(size.width * clamped, size.height),
                    cornerRadius = CornerRadius(corner, corner),
                )
            }
        }
    }
}
