package com.example.mlkit.model

import android.graphics.Rect

data class DetectedObject(
    val boundingBox: Rect,
    val label: String?,
    val trackingId: Int?
)
