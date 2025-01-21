package com.iscoding.qrcode.features.scancode.fromcamera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Size
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.MeteringPoint
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat

@SuppressLint("ClickableViewAccessibility")
@Composable
fun ScanCodeScreen() {
    var code by remember {
        mutableStateOf("")
    }
    val clipboardManager = LocalClipboardManager.current // ClipboardManager instance

    var showDialog by remember { mutableStateOf(false) }
    var scannedUrl by remember { mutableStateOf("") }
    var tapPosition by remember { mutableStateOf<Offset?>(null) } // Track tap position

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }
    var hasCamPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCamPermission = granted
        }
    )
    LaunchedEffect(key1 = true) {
        launcher.launch(Manifest.permission.CAMERA)
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (hasCamPermission) {
            Box(modifier = Modifier.fillMaxSize().weight(1f)) {
                // Camera Preview with touch to focus functionality
                AndroidView(
                    factory = { context ->
                        val previewView = PreviewView(context)
                        val preview = Preview.Builder().build()
                        val selector = CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build()
                        preview.setSurfaceProvider(previewView.surfaceProvider)
                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                        imageAnalysis.setAnalyzer(
                            ContextCompat.getMainExecutor(context),
                            QrCodeAnalyzer { result ->
                                code = result
                                if (isValidUrlWithRegex(result)) {
                                    scannedUrl = result
                                    showDialog = true
                                }
//                                if (isValidUrlWithRegex(result)) {
//                                    val intent = Intent(Intent.ACTION_VIEW).apply {
//                                        data = android.net.Uri.parse(result)
//                                    }
//                                    context.startActivity(intent)
//                                }
                            }
                        )

                        // Attach touch listener for focus
                        previewView.setOnTouchListener { _, event ->
                            if (event.action == MotionEvent.ACTION_DOWN) {
                                val x = event.x
                                val y = event.y
                                tapPosition = Offset(x, y) // Update tap position

                                val factory = previewView.meteringPointFactory
                                val meteringPoint = factory.createPoint(x, y)
                                val action = FocusMeteringAction.Builder(meteringPoint)
                                    .addPoint(meteringPoint, FocusMeteringAction.FLAG_AF) // Autofocus
                                    .build()

                                cameraProviderFuture.get()
                                    .bindToLifecycle(lifecycleOwner, selector, preview, imageAnalysis)
                                    .cameraControl
                                    .startFocusAndMetering(action)

                                true
                            } else {
                                false
                            }
                        }

                        try {
                            cameraProviderFuture.get().bindToLifecycle(
                                lifecycleOwner,
                                selector,
                                preview,
                                imageAnalysis
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Add focus overlay
                TapToFocusOverlay(tapPosition = tapPosition)

                // Reset tap position after 1 second
                LaunchedEffect(tapPosition) {
                    if (tapPosition != null) {
                        kotlinx.coroutines.delay(1000)
                        tapPosition = null
                    }
                }

                // Scanning window (white box)
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(200.dp)
                        .border(2.dp, Color.White)
                )
            }
        }
        // Text with long press to copy functionality
        Text(
            text = code,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            clipboardManager.setText(AnnotatedString(code))
                            Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
        )

    }
    // Dialog to confirm opening the URL
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Open URL") },
            text = { Text(text = "Do you want to open this URL?\n$scannedUrl") },
            confirmButton = {
                TextButton(onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(scannedUrl))
                    context.startActivity(intent)
                    showDialog = false
                }) {
                    Text("Open")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
@Composable
fun TapToFocusOverlay(tapPosition: Offset?) {
    if (tapPosition != null) {
        Box(
            modifier = Modifier
                .offset { IntOffset(tapPosition.x.toInt()-100 , tapPosition.y.toInt()-100 ) }
                .size(70.dp)
                .border(2.dp, Color.White, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(55.dp)
                    .border(2.dp, Color.White.copy(alpha = 0.5f), shape = CircleShape)
            )
        }
    }
}
fun isValidUrlWithRegex(url: String): Boolean {
    val urlPattern = Regex("""https?://(?:www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&//=]*)""")
    return urlPattern.matches(url)
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun Myprev(){
    ScanCodeScreen()
}
