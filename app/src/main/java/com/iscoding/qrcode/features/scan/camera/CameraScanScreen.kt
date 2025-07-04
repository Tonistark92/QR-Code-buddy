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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
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

@SuppressLint("ClickableViewAccessibility")
@Composable
fun ScanCodeScreen(
) {
    val viewModel = koinViewModel<CameraScanViewModel>()

    val state = viewModel.state.collectAsStateWithLifecycle()
    val clipboardManager = LocalClipboardManager.current // ClipboardManager instance


    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (!granted) {
                if (!shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.CAMERA))
                state.value.shouldLaunchAppSettings = true

                state.value.shouldPermissionDialog = true

            } else {

                state.value.shouldPermissionDialog = false
                state.value.shouldLaunchAppSettings = false
            }
        }
    )
    LaunchedEffect(key1 = true) {
        Log.d("ISLAM", "   inside LaunchedEffect ")

        val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

        Log.d("ISLAM", "${granted}" + "    is granted ? ")
        Log.d("ISLAM", ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.CAMERA).toString())

        viewModel.onEvent(CameraScanEvent.OnCameraPermissionResult(granted))

        val hasCamPermission = state.value.hasCamPermission
        if (!hasCamPermission) {
            if (shouldShowRequestPermissionRationale(context, Manifest.permission.CAMERA))
            {
                Log.d(
                    "ISLAM",
                    "${state.value.shouldPermissionDialog}" + "    state.shouldPermissionDialog 1"
                )

                state.value.shouldPermissionDialog = true
                Log.d(
                    "ISLAM",
                    "${state.value.shouldPermissionDialog}" + "    state.shouldPermissionDialog 22"
                )

            }
            Log.d("ISLAM", "    ask for permision 1")

//            launcher.launch(Manifest.permission.CAMERA)
            viewModel.onEvent(CameraScanEvent.OnRequestCameraPermission)
        }

        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is CameraScanUiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }

                CameraScanUiEvent.OpenUrl -> {
                    val intent = Intent(Intent.ACTION_VIEW, state.value.scannedUrl.toUri())
                    context.startActivity(intent)
                    state.value.shouldURLDialog = false
                }

                CameraScanUiEvent.RequestCameraPermission -> {
                    Log.d("ISLAM", "    ask for permision 2")

                    launcher.launch(Manifest.permission.CAMERA)

                }

                CameraScanUiEvent.OpenAppSettings -> {
                    Log.d("ISLAM", "    go to settings ")

                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )
                    context.startActivity(intent)
                }
            }
        }
    }

    if (state.value.shouldPermissionDialog) {
        Log.d("ISLAM", "    Show dialog")

        PermissionDialog(
            title = "we Need The camera for this ",
            body = "Kindly approve that permission so we can assist",
            onConfirm = {
                state.value.shouldPermissionDialog = false

            },
            onDismiss = {
                state.value.shouldPermissionDialog = false
                if (state.value.shouldLaunchAppSettings) {
                    state.value.shouldLaunchAppSettings = false

                    Intent(
                        Settings.ACTION_APPLICATION_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    ).also {
                        val options = ActivityOptionsCompat.makeCustomAnimation(
                            context,
                            android.R.anim.fade_in,   // you can use any animation here
                            android.R.anim.fade_out
                        )
                        context.startActivity(it, options.toBundle())
                    }
                } else {
                    launcher.launch(Manifest.permission.CAMERA)

                }

            })

    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (state.value.hasCamPermission) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
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
//                                state.scannedData = result
                                viewModel.onEvent(CameraScanEvent.OnScannedQrCode(result))
                                viewModel.onEvent(CameraScanEvent.OnValidateRegexForUrl)

                            }
                        )

                        // Attach touch listener for focus
                        previewView.setOnTouchListener { _, event ->
                            if (event.action == MotionEvent.ACTION_DOWN) {
                                val x = event.x
                                val y = event.y
                                state.value.tapPosition = Offset(x, y) // Update tap position

                                val factory = previewView.meteringPointFactory
                                val meteringPoint = factory.createPoint(x, y)
                                val action = FocusMeteringAction.Builder(meteringPoint)
                                    .addPoint(
                                        meteringPoint,
                                        FocusMeteringAction.FLAG_AF
                                    ) // Autofocus
                                    .build()

                                cameraProviderFuture.get()
                                    .bindToLifecycle(
                                        lifecycleOwner,
                                        selector,
                                        preview,
                                        imageAnalysis
                                    )
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
                TapToFocusOverlay(tapPosition = state.value.tapPosition)

                // Reset tap position after 1 second
                LaunchedEffect(state.value.tapPosition) {
                    if (state.value.tapPosition != null) {
                        delay(1000)
                        state.value.tapPosition = null
                    }
                }

                // Scanning window Box (white box)
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
    // Dialog to confirm opening the URL
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
                TextButton(onClick = { viewModel.onEvent(CameraScanEvent.OnDismissUrlDialog) }) {
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
