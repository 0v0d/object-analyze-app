package com.example.mlkit

import android.graphics.Rect
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions

data class DetectedObject(
    val boundingBox: Rect,
    val labels: List<String>,
    val trackingId: Int?
)

class ObjectDetectorAnalyzer(
    private val onObjectsDetected: (List<DetectedObject>, Int, Int) -> Unit
) : ImageAnalysis.Analyzer {

    private val objectDetector: ObjectDetector

    init {
        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .build()

        objectDetector = ObjectDetection.getClient(options)
    }

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            objectDetector.process(image)
                .addOnSuccessListener { detectedObjects ->
                    val objects = detectedObjects.map { obj ->
                        DetectedObject(
                            boundingBox = obj.boundingBox,
                            labels = obj.labels.map { label ->
                                "${label.text} (${String.format("%.1f", label.confidence * 100)}%)"
                            },
                            trackingId = obj.trackingId
                        )
                    }
                    onObjectsDetected(objects, image.width, image.height)
                }
                .addOnFailureListener { /* Handle error */ }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    fun close() {
        objectDetector.close()
    }
}
