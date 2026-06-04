package com.example.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.DonutSegment
import com.example.ValutifyViewModel
import com.example.ui.theme.ChartBarColor
import com.example.ui.theme.TooltipBg
import com.example.ui.theme.TooltipText
import kotlin.math.cos
import kotlin.math.sin

/**
 * Custom-drawn high-fidelity Sparkline for the Sales card.
 */
@Composable
fun Sparkline(
    modifier: Modifier = Modifier,
    data: List<Float> = listOf(14f, 18f, 15f, 22f, 20f, 26f, 24f),
    lineColor: Color = Color(0xFF1E5235)
) {
    Canvas(modifier = modifier) {
        if (data.isEmpty()) return@Canvas
        
        val width = size.width
        val height = size.height
        val maxVal = data.maxOrNull() ?: 1f
        val minVal = data.minOrNull() ?: 0f
        val delta = if (maxVal - minVal == 0f) 1f else (maxVal - minVal)

        val points = data.mapIndexed { idx, value ->
            val x = (idx.toFloat() / (data.size - 1)) * width
            val y = height - ((value - minVal) / delta) * (height * 0.7f) - (height * 0.15f)
            Offset(x, y)
        }

        val path = Path().apply {
            moveTo(points[0].x, points[0].y)
            for (i in 1 until points.size) {
                // Smooth cubic bezier curves for refined appearance
                val p0 = points[i - 1]
                val p1 = points[i]
                val controlX1 = p0.x + (p1.x - p0.x) / 2
                cubicTo(controlX1, p0.y, controlX1, p1.y, p1.x, p1.y)
            }
        }

        // Fill trail under the line
        val fillPath = Path().apply {
            addPath(path)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(lineColor.copy(alpha = 0.25f), Color.Transparent),
                startY = 0f,
                endY = height
            )
        )

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}


/**
 * Custom styled Donut Segment Chart with segment labeling and total sum inside.
 */
@Composable
fun DonutChart(
    segments: List<DonutSegment>,
    totalAmount: Double,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(230.dp)
                .aspectRatio(1f)
        ) {
            val strokeWidthVal = 42.dp.toPx()
            val canvasSize = size
            val radius = (canvasSize.width - strokeWidthVal) / 2f
            val centerOffset = Offset(canvasSize.width / 2f, canvasSize.height / 2f)

            var startAngle = -90f

            segments.forEach { segment ->
                val sweepAngle = (segment.percentage / 100f) * 360f

                // Draw the segment stroke
                drawArc(
                    color = Color(segment.colorHex),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(strokeWidthVal / 2f, strokeWidthVal / 2f),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidthVal, cap = StrokeCap.Butt)
                )

                // Add subtle spacer line
                drawArc(
                    color = Color.White,
                    startAngle = startAngle,
                    sweepAngle = 1.2f,
                    useCenter = false,
                    topLeft = Offset(strokeWidthVal / 2f, strokeWidthVal / 2f),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidthVal + 2f)
                )

                // Draw miniature absolute dollar amount tag pills (similar to image tags)
                val midAngleRad = Math.toRadians((startAngle + sweepAngle / 2f).toDouble())
                val tagDistance = radius + 15f
                val tagX = (centerOffset.x + tagDistance * cos(midAngleRad)).toFloat()
                val tagY = (centerOffset.y + tagDistance * sin(midAngleRad)).toFloat()

                // Small connector dot
                drawCircle(
                    color = Color(segment.colorHex),
                    radius = 3.dp.toPx(),
                    center = Offset(
                        (centerOffset.x + radius * cos(midAngleRad)).toFloat(),
                        (centerOffset.y + radius * sin(midAngleRad)).toFloat()
                    )
                )

                startAngle += sweepAngle
            }
        }

        // Inside labels: Center total sum representation
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total",
                fontSize = 13.sp,
                color = Color(0xFF6F8B7B),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = ValutifyViewModel.formatCurrency(totalAmount),
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF142B1E),
                letterSpacing = (-0.5).sp
            )
        }
    }
}
