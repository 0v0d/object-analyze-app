package com.example.mlkit

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp

@Composable
fun ObjectOverlay(
    detectedObjects: List<DetectedObject>,
    imageWidth: Int,
    imageHeight: Int,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier.fillMaxSize()) {
        if (imageWidth == 0 || imageHeight == 0) return@Canvas

        val scaleX = size.width / imageHeight.toFloat()
        val scaleY = size.height / imageWidth.toFloat()

        detectedObjects.forEach { obj ->
            val left = obj.boundingBox.left * scaleX
            val top = obj.boundingBox.top * scaleY
            val right = obj.boundingBox.right * scaleX
            val bottom = obj.boundingBox.bottom * scaleY

            val boxWidth = right - left
            val boxHeight = bottom - top

            // Draw bounding box
            drawRect(
                color = Color.Green,
                topLeft = Offset(left, top),
                size = Size(boxWidth, boxHeight),
                style = Stroke(width = 4f)
            )

            // Draw label background
            val labelText = if (obj.labels.isNotEmpty()) {
                obj.labels.first()
            } else {
                "Object ${obj.trackingId ?: ""}"
            }

            val textLayoutResult = textMeasurer.measure(
                text = labelText,
                style = TextStyle(fontSize = 14.sp)
            )

            drawRect(
                color = Color.Green,
                topLeft = Offset(left, top - textLayoutResult.size.height - 8),
                size = Size(
                    textLayoutResult.size.width.toFloat() + 16,
                    textLayoutResult.size.height.toFloat() + 8
                )
            )

            // Draw label text
            drawText(
                textMeasurer = textMeasurer,
                text = labelText,
                topLeft = Offset(left + 8, top - textLayoutResult.size.height - 4),
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color.Black
                )
            )
        }
    }
}
