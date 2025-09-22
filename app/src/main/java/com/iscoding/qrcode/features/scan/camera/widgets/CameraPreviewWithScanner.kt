package com.iscoding.qrcode.features.scan.camera.widgets

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.common.util.concurrent.ListenableFuture

@Composable
fun CameraPreviewWithScanner(
    onTapToFocus: (Offset) -> Unit,
    analyzer: ImageAnalysis.Analyzer,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraProviderFuture =
        remember {
            ProcessCameraProvider.getInstance(context)
        }

    AndroidView(
        factory = { context ->
            createCameraPreview(
                context = context,
                lifecycleOwner = lifecycleOwner,
                cameraProviderFuture = cameraProviderFuture,
                analyzer = analyzer,
                onTapToFocus = onTapToFocus,
            )
        },
        modifier = modifier,
    )
}

private fun createCameraPreview(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    cameraProviderFuture: ListenableFuture<ProcessCameraProvider>,
    analyzer: ImageAnalysis.Analyzer,
    onTapToFocus: (Offset) -> Unit,
): PreviewView {
    val previewView = PreviewView(context)

    // Setup preview
    val preview = Preview.Builder().build()
    val selector =
        CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
    preview.surfaceProvider = previewView.surfaceProvider

    // Setup image analysis
    val imageAnalysis =
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
    imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context), analyzer)

    // Bind camera
    try {
        cameraProviderFuture.get().bindToLifecycle(
            lifecycleOwner,
            selector,
            preview,
            imageAnalysis,
        )
    } catch (e: Exception) {
        Log.e("ISLAM", "Camera binding failed", e)
    }

    // Setup touch to focus
    setupTouchToFocus(
        previewView = previewView,
        cameraProviderFuture = cameraProviderFuture,
        lifecycleOwner = lifecycleOwner,
        selector = selector,
        preview = preview,
        imageAnalysis = imageAnalysis,
        onTapToFocus = onTapToFocus,
    )

    return previewView
}

private fun setupTouchToFocus(
    previewView: PreviewView,
    cameraProviderFuture: ListenableFuture<ProcessCameraProvider>,
    lifecycleOwner: LifecycleOwner,
    selector: CameraSelector,
    preview: Preview,
    imageAnalysis: ImageAnalysis,
    onTapToFocus: (Offset) -> Unit,
) {
    previewView.setOnTouchListener { view, event ->
        if (event.action == MotionEvent.ACTION_DOWN) {
            val x = event.x
            val y = event.y

            // Send tap position to callback
            onTapToFocus(Offset(x, y))

            val factory = previewView.meteringPointFactory
            val meteringPoint = factory.createPoint(x, y)
            val action =
                FocusMeteringAction.Builder(meteringPoint)
                    .addPoint(meteringPoint, FocusMeteringAction.FLAG_AF)
                    .build()

            try {
                cameraProviderFuture.get()
                    .bindToLifecycle(
                        lifecycleOwner,
                        selector,
                        preview,
                        imageAnalysis,
                    )
                    .cameraControl
                    .startFocusAndMetering(action)
            } catch (e: Exception) {
                Log.e("ISLAM", "Focus metering failed", e)
            }

            view.performClick()
            true
        } else {
            false
        }
    }
}
