package com.aguiabranca.inovacao.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.dp

@Composable
fun CustomBarChart(
    data: List<Pair<String, Float>>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary,
    labelColor: Color = MaterialTheme.colorScheme.onSurface
) {
    if (data.isEmpty()) return

    val maxVal = data.maxOf { it.second }.coerceAtLeast(1f)

    Box(modifier = modifier.padding(top = 16.dp, bottom = 24.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val barWidth = (canvasWidth / (data.size * 2)).coerceAtMost(100f)
            val spacing = (canvasWidth - (barWidth * data.size)) / (data.size + 1)

            data.forEachIndexed { index, pair ->
                val (label, value) = pair
                val barHeight = (value / maxVal) * canvasHeight
                val startX = spacing + index * (barWidth + spacing)
                val startY = canvasHeight - barHeight

                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x = startX, y = startY),
                    size = Size(width = barWidth, height = barHeight),
                    cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                )

                drawIntoCanvas { canvas ->
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.parseColor(
                            String.format("#%06X", 0xFFFFFF and labelColor.hashCode())
                        )
                        textSize = 36f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    canvas.nativeCanvas.drawText(
                        label,
                        startX + barWidth / 2,
                        canvasHeight + 48f,
                        paint
                    )
                }
            }
        }
    }
}
