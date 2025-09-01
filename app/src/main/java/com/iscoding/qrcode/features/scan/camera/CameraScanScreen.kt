package com.iscoding.qrcode.features.scan.camera

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iscoding.qrcode.features.scan.camera.event.CameraScanEvent
import com.iscoding.qrcode.features.scan.camera.event.CameraScanUiEvent
import com.iscoding.qrcode.features.scan.widgets.PermissionDialog
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun ScanCodeScreen() {
    val viewModel = koinViewModel<CameraScanViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            // Check if we should show rationale BEFORE sending the result
            val shouldShowRationale = if (!granted) {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    context as Activity,
                    Manifest.permission.CAMERA
                )
            } else {
                false
            }

            // Update the rationale status in ViewModel
            viewModel.setPermissionRationaleStatus(shouldShowRationale)

            // Send permission result
            viewModel.onEvent(CameraScanEvent.OnCameraPermissionResult(granted))
        }
    )

    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        viewModel.checkInitialPermission(hasPermission)

        if (!hasPermission) {
            viewModel.onEvent(CameraScanEvent.OnRequestCameraPermission)
        }
    }

    LaunchedEffect(key1 = viewModel.uiEvent) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is CameraScanUiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }

                CameraScanUiEvent.OpenUrl -> {
                    val intent = Intent(Intent.ACTION_VIEW, state.value.scannedUrl.toUri())
                    context.startActivity(intent)
                    viewModel.onEvent(CameraScanEvent.OnDismissUrlDialog)
                }

                CameraScanUiEvent.RequestCameraPermission -> {
                    launcher.launch(Manifest.permission.CAMERA)
                }

                CameraScanUiEvent.OpenAppSettings -> {
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )
                    context.startActivity(intent)
                }
            }
        }
    }

    // Permission Dialog
    if (state.value.shouldPermissionDialog) {
        PermissionDialog(
            title = "Camera Permission Required",
            body = if (state.value.shouldLaunchAppSettings) {
                "Camera permission was permanently denied. Please enable it in app settings to scan QR codes."
            } else {
                "We need camera access to scan QR codes. Please grant the permission."
            },
            confirmButtonText = if (state.value.shouldLaunchAppSettings) "Open Settings" else "Grant Permission",
            onConfirm = {
                if (state.value.shouldLaunchAppSettings) {
                    viewModel.onEvent(CameraScanEvent.OnOpenAppSettings)
                } else {
                    viewModel.onEvent(CameraScanEvent.OnRequestCameraPermission)
                }
                viewModel.onEvent(CameraScanEvent.OnDismissPermissionDialog)
            },
            onDismiss = {
                viewModel.onEvent(CameraScanEvent.OnDismissPermissionDialog)
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (state.value.hasCamPermission) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                // Camera Preview
                AndroidView(
                    factory = { context ->
                        val previewView = PreviewView(context)
                        val preview = Preview.Builder().build()
                        val selector = CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build()
                        preview.setSurfaceProvider(previewView.surfaceProvider)

                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()

                        imageAnalysis.setAnalyzer(
                            ContextCompat.getMainExecutor(context),
                            QrCodeAnalyzer { result ->
                                viewModel.onEvent(CameraScanEvent.OnScannedQrCode(result))
                                viewModel.onEvent(CameraScanEvent.OnValidateRegexForUrl)
                            }
                        )

                        // Touch to focus
                        previewView.setOnTouchListener { _, event ->
                            if (event.action == MotionEvent.ACTION_DOWN) {
                                val x = event.x
                                val y = event.y

                                // Send tap position event to ViewModel if needed
                                // viewModel.onEvent(CameraScanEvent.OnTapToFocus(Offset(x, y)))

                                val factory = previewView.meteringPointFactory
                                val meteringPoint = factory.createPoint(x, y)
                                val action = FocusMeteringAction.Builder(meteringPoint)
                                    .addPoint(meteringPoint, FocusMeteringAction.FLAG_AF)
                                    .build()

                                try {
                                    cameraProviderFuture.get()
                                        .bindToLifecycle(
                                            lifecycleOwner,
                                            selector,
                                            preview,
                                            imageAnalysis
                                        )
                                        .cameraControl
                                        .startFocusAndMetering(action)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
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

                // Focus overlay (you might want to move this to state as well)
                TapToFocusOverlay(tapPosition = state.value.tapPosition)

                // Scanning window
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(200.dp)
                        .border(2.dp, Color.White)
                )
            }
        } else {
            // No permission state - show permission request UI
            PermissionRequestUI(
                onRequestPermission = {
                    viewModel.onEvent(CameraScanEvent.OnRequestCameraPermission)
                }
            )
        }

        // Scanned data display
        Text(
            text = state.value.scannedData,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            clipboardManager.setText(AnnotatedString(state.value.scannedData))
                            Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT)
                                .show()
                        }
                    )
                }
        )
    }

    // URL confirmation dialog
    if (state.value.shouldURLDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(CameraScanEvent.OnDismissUrlDialog) },
            title = { Text(text = "Open URL") },
            text = { Text(text = "Do you want to open this URL?\n${state.value.scannedUrl}") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onEvent(CameraScanEvent.OnOpenUrlFromScannedQrCode)
                }) {
                    Text("Open")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.onEvent(CameraScanEvent.OnDismissUrlDialog)
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun PermissionRequestUI(
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Camera Permission Required",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "This app needs camera access to scan QR codes",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onRequestPermission) {
            Text("Grant Camera Permission")
        }
    }
}

@Composable
fun PermissionDialog(
    title: String,
    body: String,
    confirmButtonText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(text = body) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun TapToFocusOverlay(tapPosition: Offset?) {
    if (tapPosition != null) {
        Box(
            modifier = Modifier
                .offset { IntOffset(tapPosition.x.toInt() - 100, tapPosition.y.toInt() - 100) }
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

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun Myprev() {

    ScanCodeScreen()
}
