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

class CameraScanViewModel(
    private val qrCodeScanner: QrCodeScanner,
) : ViewModel() {
    private val _state = MutableStateFlow(CameraScanUiState())
    val state get() = _state.asStateFlow()

    private val _uiEvent = Channel<CameraScanEffect>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    val analyzer: ImageAnalysis.Analyzer =
        qrCodeScanner.getAnalyzer { result ->
            onEvent(CameraScanEvent.OnScannedQrCode(result))
            onEvent(CameraScanEvent.OnValidateRegexForUrl)
        }

    fun onEvent(event: CameraScanEvent) {
        when (event) {
            is CameraScanEvent.OnInitialPermissionCheck -> {
                _state.update { it.copy(hasCamPermission = event.hasPermission) }
                if (!event.hasPermission) {
                    requestPermission()
                }
            }

            is CameraScanEvent.OnCameraPermissionResult -> {
                handlePermissionResult(event.granted, event.shouldShowRationale)
            }

            is CameraScanEvent.OnPermissionStatusChanged -> {
                val previousPermission = _state.value.hasCamPermission
                _state.update { it.copy(hasCamPermission = event.hasPermission) }

                // Special handling when permission is newly granted
                if (!previousPermission && event.hasPermission) {
                    _state.update {
                        it.copy(
                            shouldPermissionDialog = false, // Dismiss permission dialog
                            shouldLaunchAppSettings = false, // Reset settings flag
                        )
                    }
                    viewModelScope.launch {
                        // Show success message
                        _uiEvent.send(CameraScanEffect.ShowToast("Camera permission granted!"))
                    }
                }
            }

            CameraScanEvent.OnRequestCameraPermission -> {
                requestPermission()
            }

            CameraScanEvent.OnDismissPermissionDialog -> {
                _state.update { it.copy(shouldPermissionDialog = false) }
            }

            CameraScanEvent.OnOpenAppSettings -> {
                openAppSettings()
            }

            CameraScanEvent.OnValidateRegexForUrl -> {
                validateScannedUrl()
            }

            is CameraScanEvent.OnScannedQrCode -> {
                handleScannedQrCode(event.data)
            }

            CameraScanEvent.OnOpenUrlFromScannedQrCode -> {
                openScannedUrl()
            }

            CameraScanEvent.OnDismissUrlDialog -> {
                _state.update { it.copy(shouldURLDialog = false) }
            }

            is CameraScanEvent.OnTapToFocus -> {
                _state.update { it.copy(tapPosition = event.offset) }
            }

            CameraScanEvent.OnClearTapToFocus -> {
                _state.update { it.copy(tapPosition = null) }
            }

            is CameraScanEvent.OnTextLongPressed -> {
                if (!_state.value.scannedData.isEmpty()) {
                    viewModelScope.launch {
                        _uiEvent.send(CameraScanEffect.CopyToTheClipBoard(_state.value.scannedData))
                        _uiEvent.send(CameraScanEffect.ShowToast("Text copied to clipboard"))
                    }
                } else {
                    viewModelScope.launch {
                        _uiEvent.send(CameraScanEffect.ShowToast("you need to scan first!"))
                    }
                }
            }
        }
    }

    private fun openAppSettings() {
        viewModelScope.launch {
            _uiEvent.send(CameraScanEffect.OpenAppSettings)
        }
    }

    private fun handlePermissionResult(
        granted: Boolean,
        shouldShowRationale: Boolean,
    ) {
        if (granted) {
            _state.update {
                it.copy(
                    hasCamPermission = true,
                    shouldPermissionDialog = false,
                    shouldLaunchAppSettings = false,
                )
            }
        } else {
            // Permission denied
            _state.update {
                it.copy(
                    hasCamPermission = false,
                    shouldPermissionDialog = true,
                    shouldLaunchAppSettings = !shouldShowRationale, // If no rationale, go to settings
                )
            }

            viewModelScope.launch {
                if (shouldShowRationale) {
                    _uiEvent.send(CameraScanEffect.ShowToast("Camera permission is needed to scan QR codes"))
                } else {
                    _uiEvent.send(
                        CameraScanEffect.ShowToast("Permission permanently denied. Please enable in settings."),
                    )
                }
            }
        }
    }

    private fun requestPermission() {
        viewModelScope.launch {
            _uiEvent.send(CameraScanEffect.RequestCameraPermission)
        }
    }

    private fun handleScannedQrCode(data: String) {
        _state.update { it.copy(scannedData = data) }
    }

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

    private fun openScannedUrl() {
        if (_state.value.isGoodUrlRegex) {
            viewModelScope.launch {
                _uiEvent.send(CameraScanEffect.OpenUrl)
            }
            _state.update { it.copy(shouldURLDialog = false) }
        } else {
            viewModelScope.launch {
                _uiEvent.send(CameraScanEffect.ShowToast("Invalid URL"))
            }
        }
    }

    private fun isValidUrlWithRegex(url: String): Boolean {
        val urlPattern =
            Regex(
                """https?://(?:www\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)""",
            )
        return urlPattern.matches(url)
    }
}
