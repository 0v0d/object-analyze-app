package com.example.mlkit.ui.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mlkit.model.DetectedObject

@Composable
fun ObjectOverlay(
    detectedObjects: List<DetectedObject>,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier.fillMaxSize()) {
        // Space between the label text and the edge of its background
        val labelPaddingH = 8.dp.toPx()
        val labelPaddingV = 4.dp.toPx()

        detectedObjects.forEach { obj ->
            // Already in PreviewView coordinates, so no scaling is needed
            val box = obj.boundingBox.toComposeRect()

            // Draw bounding box
            drawRect(
                color = Color.Green,
                topLeft = box.topLeft,
                size = box.size,
                style = Stroke(width = 4f)
            )

            val labelText = obj.label ?: "Object ${obj.trackingId ?: ""}"

            val textLayoutResult = textMeasurer.measure(
                text = labelText,
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color.Black
                )
            )
            val textWidth = textLayoutResult.size.width
            val textHeight = textLayoutResult.size.height

            // Draw label background
            drawRect(
                color = Color.Green,
                topLeft = Offset(box.left, box.top - textHeight - labelPaddingV * 2),
                size = Size(
                    textWidth + labelPaddingH * 2,
                    textHeight + labelPaddingV * 2
                )
            )

            // Draw label text
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(box.left + labelPaddingH, box.top - textHeight - labelPaddingV)
            )
        }
    }
}
