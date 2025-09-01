package com.iscoding.qrcode.features.scan.camera

import android.Manifest
import android.app.Activity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iscoding.qrcode.domain.repos.QrCodeScanner
import com.iscoding.qrcode.features.scan.camera.event.CameraScanEvent
import com.iscoding.qrcode.features.scan.camera.event.CameraScanUiEvent
import com.iscoding.qrcode.features.scan.camera.event.CameraScanUiEvent.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CameraScanViewModel(
    qrCodeScanner: QrCodeScanner
) : ViewModel() {
    private val _state = MutableStateFlow(CameraScanUiState())
    val state get() = _state.asStateFlow()

    private val _uiEvent = Channel<CameraScanUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: CameraScanEvent) {
        viewModelScope.launch {
            when (event) {
                is CameraScanEvent.OnCameraPermissionResult -> {
                    handlePermissionResult(event.granted)
                }

                CameraScanEvent.OnRequestCameraPermission -> {
                    _uiEvent.send(CameraScanUiEvent.RequestCameraPermission)
                }

                CameraScanEvent.OnDismissPermissionDialog -> {
                    _state.update { it.copy(shouldPermissionDialog = false) }
                }

                CameraScanEvent.OnOpenAppSettings -> {
                    _uiEvent.send(CameraScanUiEvent.OpenAppSettings)
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
            }
        }
    }

    private fun handlePermissionResult(granted: Boolean) {
        if (granted) {
            _state.update {
                it.copy(
                    hasCamPermission = true,
                    shouldPermissionDialog = false,
                    shouldLaunchAppSettings = false
                )
            }
        } else {
            // Permission denied - we need to check if we should show rationale
            _state.update {
                it.copy(
                    hasCamPermission = false,
                    shouldPermissionDialog = true,
                    // This will be set properly in the composable when we know the rationale status
                    shouldLaunchAppSettings = false
                )
            }
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
                shouldURLDialog = isValidUrl
            )
        }

        if (!isValidUrl) {
            viewModelScope.launch {
                _uiEvent.send(CameraScanUiEvent.ShowToast("Scanned QR code is not a valid URL"))
            }
        }
    }

    private fun openScannedUrl() {
        if (_state.value.isGoodUrlRegex) {
            viewModelScope.launch {
                _uiEvent.send(CameraScanUiEvent.OpenUrl)
            }
            _state.update { it.copy(shouldURLDialog = false) }
        } else {
            viewModelScope.launch {
                _uiEvent.send(CameraScanUiEvent.ShowToast("Invalid URL"))
            }
        }
    }

    fun checkInitialPermission(hasPermission: Boolean) {
        _state.update { it.copy(hasCamPermission = hasPermission) }
    }

    fun setPermissionRationaleStatus(shouldShowRationale: Boolean) {
        _state.update {
            it.copy(shouldLaunchAppSettings = !shouldShowRationale && !it.hasCamPermission)
        }
    }

    private fun isValidUrlWithRegex(url: String): Boolean {
        val urlPattern = Regex("""https?://(?:www\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)""")
        return urlPattern.matches(url)
    }
}
