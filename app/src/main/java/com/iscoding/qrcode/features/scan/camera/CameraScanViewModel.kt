package com.iscoding.qrcode.features.scan.camera

import androidx.camera.core.ImageAnalysis
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iscoding.qrcode.domain.repos.QrCodeScanner
import com.iscoding.qrcode.features.scan.camera.intent.CameraScanEffect
import com.iscoding.qrcode.features.scan.camera.intent.CameraScanEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Camera QR Code Scanning screen.
 *
 * This ViewModel manages:
 * 1. Camera permission checks and requests.
 * 2. Real-time QR code scanning via [QrCodeScanner].
 * 3. Validating scanned data (e.g., URLs).
 * 4. Showing permission dialogs, URL dialogs, and other UI effects.
 * 5. Handling tap-to-focus events on the camera preview.
 *
 * It follows an MVI-like unidirectional data flow:
 * - [state] represents the current UI state.
 * - [uiEvent] emits one-time UI effects like toasts or navigation actions.
 *
 * @property qrCodeScanner The QR code scanner repository used to analyze camera frames.
 */
class CameraScanViewModel(
    private val qrCodeScanner: QrCodeScanner,
) : ViewModel() {

    /** Mutable state representing the UI state of the screen. */
    private val _state = MutableStateFlow(CameraScanUiState())
    val state get() = _state.asStateFlow()

    /** Channel for one-time UI effects (toasts, navigation, clipboard actions). */
    private val _uiEvent = Channel<CameraScanEffect>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    /**
     * Analyzer for the camera preview.
     * When a QR code is detected, it triggers [CameraScanEvent.OnScannedQrCode]
     * and then validates the scanned text as a URL.
     */
    val analyzer: ImageAnalysis.Analyzer =
        qrCodeScanner.getAnalyzer { result ->
            onEvent(CameraScanEvent.OnScannedQrCode(result))
            onEvent(CameraScanEvent.OnValidateRegexForUrl)
        }

    /**
     * Handles all UI events from the Compose layer.
     *
     * @param event The event to handle, represented by [CameraScanEvent].
     */
    fun onEvent(event: CameraScanEvent) {
        when (event) {
            is CameraScanEvent.OnInitialPermissionCheck -> {
                _state.update { it.copy(hasCamPermission = event.hasPermission) }
                if (!event.hasPermission) requestPermission()
            }

            is CameraScanEvent.OnCameraPermissionResult -> handlePermissionResult(
                event.granted,
                event.shouldShowRationale,
            )

            is CameraScanEvent.OnPermissionStatusChanged -> {
                val previousPermission = _state.value.hasCamPermission
                _state.update { it.copy(hasCamPermission = event.hasPermission) }

                if (!previousPermission && event.hasPermission) {
                    _state.update {
                        it.copy(
                            shouldPermissionDialog = false,
                            shouldLaunchAppSettings = false,
                        )
                    }
                    viewModelScope.launch {
                        _uiEvent.send(CameraScanEffect.ShowToast("Camera permission granted!"))
                    }
                }
            }

            CameraScanEvent.OnRequestCameraPermission -> requestPermission()

            CameraScanEvent.OnDismissPermissionDialog -> {
                _state.update { it.copy(shouldPermissionDialog = false) }
            }

            CameraScanEvent.OnOpenAppSettings -> openAppSettings()

            CameraScanEvent.OnValidateRegexForUrl -> validateScannedUrl()

            is CameraScanEvent.OnScannedQrCode -> handleScannedQrCode(event.data)

            CameraScanEvent.OnOpenUrlFromScannedQrCode -> openScannedUrl()

            CameraScanEvent.OnDismissUrlDialog -> {
                _state.update { it.copy(shouldURLDialog = false) }
            }

            is CameraScanEvent.OnTapToFocus -> {
                _state.update { it.copy(tapPosition = event.offset) }
            }

            CameraScanEvent.OnClearTapToFocus -> {
                _state.update { it.copy(tapPosition = null) }
            }

            is CameraScanEvent.OnTextLongPressed -> handleTextLongPress()
        }
    }

    /** Sends a UI effect to open the app settings. */
    private fun openAppSettings() {
        viewModelScope.launch {
            _uiEvent.send(CameraScanEffect.OpenAppSettings)
        }
    }

    /**
     * Handles camera permission results.
     *
     * @param granted True if permission was granted.
     * @param shouldShowRationale True if the system suggests showing rationale.
     */
    private fun handlePermissionResult(granted: Boolean, shouldShowRationale: Boolean) {
        if (granted) {
            _state.update {
                it.copy(
                    hasCamPermission = true,
                    shouldPermissionDialog = false,
                    shouldLaunchAppSettings = false,
                )
            }
        } else {
            _state.update {
                it.copy(
                    hasCamPermission = false,
                    shouldPermissionDialog = true,
                    shouldLaunchAppSettings = !shouldShowRationale,
                )
            }
            viewModelScope.launch {
                _uiEvent.send(
                    CameraScanEffect.ShowToast(
                        if (shouldShowRationale) {
                            "Camera permission is needed to scan QR codes"
                        } else {
                            "Permission permanently denied. Please enable in settings."
                        },
                    ),
                )
            }
        }
    }

    /** Requests camera permission via UI effect. */
    private fun requestPermission() {
        viewModelScope.launch { _uiEvent.send(CameraScanEffect.RequestCameraPermission) }
    }

    /** Updates the scanned QR code text in the state. */
    private fun handleScannedQrCode(data: String) {
        _state.update { it.copy(scannedData = data) }
    }

    /** Validates the scanned QR code text as a URL and triggers a dialog if valid. */
    private fun validateScannedUrl() {
        val currentData = _state.value.scannedData
        val isValidUrl = isValidUrlWithRegex(currentData)

        _state.update {
            it.copy(
                isGoodUrlRegex = isValidUrl,
                scannedUrl = currentData,
                shouldURLDialog = isValidUrl,
            )
        }

        if (!isValidUrl) {
            viewModelScope.launch {
                _uiEvent.send(CameraScanEffect.ShowToast("Scanned QR code is not a valid URL"))
            }
        }
    }

    /** Opens the scanned URL if it is valid, otherwise shows a toast. */
    private fun openScannedUrl() {
        if (_state.value.isGoodUrlRegex) {
            viewModelScope.launch { _uiEvent.send(CameraScanEffect.OpenUrl) }
            _state.update { it.copy(shouldURLDialog = false) }
        } else {
            viewModelScope.launch { _uiEvent.send(CameraScanEffect.ShowToast("Invalid URL")) }
        }
    }

    /** Checks if a string matches the URL pattern using regex. */
    private fun isValidUrlWithRegex(url: String): Boolean {
        val urlPattern =
            Regex(
                """https?://(?:www\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)""",
            )
        return urlPattern.matches(url)
    }

    /** Handles long-press on scanned text to copy to clipboard. */
    private fun handleTextLongPress() {
        if (_state.value.scannedData.isNotEmpty()) {
            viewModelScope.launch {
                _uiEvent.send(CameraScanEffect.CopyToTheClipBoard(_state.value.scannedData))
                _uiEvent.send(CameraScanEffect.ShowToast("Text copied to clipboard"))
            }
        } else {
            viewModelScope.launch {
                _uiEvent.send(CameraScanEffect.ShowToast("You need to scan first!"))
            }
        }
    }
}
