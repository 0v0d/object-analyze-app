package com.example.mlkit.ui.view

import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.mlkit.model.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions

@Composable
fun CameraPreviewWithObjectDetection() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var detectedObjects by remember { mutableStateOf<List<DetectedObject>>(emptyList()) }

    val objectDetector = remember {
        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .build()

        ObjectDetection.getClient(options)
    }

    val cameraController = remember {
        LifecycleCameraController(context).apply {
            // Preview is always on; only enable analysis (no photo capture)
            setEnabledUseCases(CameraController.IMAGE_ANALYSIS)

            val mainExecutor = ContextCompat.getMainExecutor(context)
            setImageAnalysisAnalyzer(
                mainExecutor,
                MlKitAnalyzer(
                    listOf(objectDetector),
                    // Bounding boxes come back in PreviewView coordinates
                    ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED,
                    mainExecutor
                ) { result ->
                    detectedObjects = result.getValue(objectDetector).orEmpty().map { obj ->
                        DetectedObject(
                            boundingBox = obj.boundingBox,
                            label = obj.labels.firstOrNull()?.let { label ->
                                "${label.text} (${String.format("%.1f", label.confidence * 100)}%)"
                            },
                            trackingId = obj.trackingId
                        )
                    }
                }
            )
        }
    }

    DisposableEffect(Unit) {
        cameraController.bindToLifecycle(lifecycleOwner)
        onDispose {
            cameraController.unbind()
            objectDetector.close()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply { controller = cameraController }
            },
            modifier = Modifier.fillMaxSize()
        )

        ObjectOverlay(
            detectedObjects = detectedObjects,
            modifier = Modifier.fillMaxSize()
        )
    }
}
