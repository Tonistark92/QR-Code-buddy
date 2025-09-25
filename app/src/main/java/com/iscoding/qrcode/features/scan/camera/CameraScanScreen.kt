package com.iscoding.qrcode.features.scan.camera

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iscoding.qrcode.features.scan.camera.intent.CameraScanEffect
import com.iscoding.qrcode.features.scan.camera.intent.CameraScanEvent
import com.iscoding.qrcode.features.scan.camera.widgets.CameraPreviewWithScanner
import com.iscoding.qrcode.features.scan.camera.widgets.PermissionRequestWidget
import com.iscoding.qrcode.features.scan.camera.widgets.TapToFocusOverlay
import com.iscoding.qrcode.features.scan.camera.widgets.URLDialog
import com.iscoding.qrcode.features.scan.widgets.PermissionDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.androidx.compose.koinViewModel

@Composable
fun ScanCodeScreen() {
    val viewModel = koinViewModel<CameraScanViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { granted ->
            val shouldShowRationale =
                if (!granted) {
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        context as Activity,
                        Manifest.permission.CAMERA,
                    )
                } else {
                    false
                }

            viewModel.onEvent(
                CameraScanEvent.OnCameraPermissionResult(
                    granted = granted,
                    shouldShowRationale = shouldShowRationale,
                ),
            )
        }
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) {
            // Check permission when app resumes
            val currentPermission =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA,
                ) == PackageManager.PERMISSION_GRANTED

            // Only update if changed (prevents unnecessary events)
            if (currentPermission != state.hasCamPermission) {
                viewModel.onEvent(CameraScanEvent.OnPermissionStatusChanged(currentPermission))
            }
        }
    }

    LaunchedEffect(Unit) {
        val hasPermission =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA,
            ) == PackageManager.PERMISSION_GRANTED

        Log.d("ISLAM", "Initial permission check: $hasPermission")
        viewModel.onEvent(CameraScanEvent.OnInitialPermissionCheck(hasPermission))
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.distinctUntilChanged()
            .collectLatest { event ->

                Log.d("ISLAM", "UI Event received: $event")
                when (event) {
                    is CameraScanEffect.ShowToast -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    }

                    CameraScanEffect.OpenUrl -> {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, state.scannedUrl.toUri())
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            Toast.makeText(context, "Cannot open URL", Toast.LENGTH_SHORT).show()
                        }
                    }

                    CameraScanEffect.RequestCameraPermission -> {
                        Log.d("ISLAM", "Launching permission request")
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }

                    CameraScanEffect.OpenAppSettings -> {
                        Log.d("ISLAM", "Opening app settings")
                        try {
                            val intent =
                                Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", context.packageName, null),
                                )
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            // Fallback to general app settings
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            context.startActivity(intent)
                        }
                    }

                    is CameraScanEffect.CopyToTheClipBoard -> {
                        clipboardManager.setText(AnnotatedString(event.data))
                    }
                }
            }
    }
    LaunchedEffect(state.tapPosition) {
        if (state.tapPosition != null) {
            delay(1000)
            viewModel.onEvent(CameraScanEvent.OnClearTapToFocus)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (state.hasCamPermission) {
            Box(
                modifier =
                Modifier
                    .fillMaxSize()
                    .weight(1f),
            ) {
                CameraPreviewWithScanner(
                    onTapToFocus = { offset ->
                        viewModel.onEvent(CameraScanEvent.OnTapToFocus(offset))
                    },
                    analyzer = viewModel.analyzer,
                )
                // Focus overlay
                TapToFocusOverlay(tapPosition = state.tapPosition)

                // Scanning window
                Box(
                    modifier =
                    Modifier
                        .align(Alignment.Center)
                        .size(200.dp)
                        .border(2.dp, Color.White),
                )
            }
        } else {
            // No permission UI
            PermissionRequestWidget(
                onRequestPermission = {
                    viewModel.onEvent(CameraScanEvent.OnRequestCameraPermission)
                },
            )
        }

        // Scanned data display
        Text(
            text = state.scannedData,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(32.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            viewModel.onEvent(CameraScanEvent.OnTextLongPressed)
                        },
                    )
                },
        )
    }

    if (state.shouldPermissionDialog) {
        PermissionDialog(
            title = "Camera Permission Required",
            body =
            if (state.shouldLaunchAppSettings) {
                "Camera permission was permanently denied. Please enable it in app settings to scan QR codes."
            } else {
                "We need camera access to scan QR codes. Please grant the permission."
            },
            confirmButtonText = if (state.shouldLaunchAppSettings) "Open Settings" else "Grant Permission",
            onConfirm = {
                if (state.shouldLaunchAppSettings) {
                    viewModel.onEvent(CameraScanEvent.OnOpenAppSettings)
                } else {
                    viewModel.onEvent(CameraScanEvent.OnRequestCameraPermission)
                }
                viewModel.onEvent(CameraScanEvent.OnDismissPermissionDialog)
            },
            onDismiss = {
                viewModel.onEvent(CameraScanEvent.OnDismissPermissionDialog)
            },
        )
    }

    if (state.shouldURLDialog) {
        URLDialog(
            title = "Open URL",
            body = "Do you want to open this URL?\n${state.scannedUrl}",
            confirmButtonText = "Open",
            dismissButtonText = "Cancel",
            onDismiss = { viewModel.onEvent(CameraScanEvent.OnDismissUrlDialog) },
            onConfirm = { viewModel.onEvent(CameraScanEvent.OnOpenUrlFromScannedQrCode) },
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun Myprev() {
    ScanCodeScreen()
}
