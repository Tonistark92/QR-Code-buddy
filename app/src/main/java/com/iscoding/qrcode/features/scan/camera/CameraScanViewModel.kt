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
                    _state.update { it.copy(hasCamPermission = event.granted) }
//                    if (!_state.value.hasCamPermission){
//                    onEvent(CameraScanEvent.OnRequestCameraPermission)
//                        _state.update { it.copy(shouldPermissionDialog = true) }
//
//                    }
                }

                CameraScanEvent.OnRequestCameraPermission -> {
                    _uiEvent.send(CameraScanUiEvent.RequestCameraPermission)
                }

                CameraScanEvent.OnDismissPermissionDialog -> {
                    _state.update {
                        it.copy(shouldPermissionDialog = false)
                    }
                }

                CameraScanEvent.OnOpenAppSettings -> {
                    _uiEvent.send(OpenAppSettings)
                }

                is CameraScanEvent.OnValidateRegexForUrl -> {
                    val isValid = isValidUrlWithRegex(_state.value.scannedData)
                    _state.update { it.copy(isGoodUrlRegex = isValid, scannedUrl = _state.value.scannedData, shouldURLDialog = true) }


                }

                is CameraScanEvent.OnScannedQrCode -> {
                    _state.update { it.copy(scannedData = event.data) }

                    // If it’s a URL, validate
//                if (isValidUrlWithRegex(event.data)) {
//                    onEvent(CameraScanEvent.OnValidateRegexForUrl(event.data))
//                }
//                else {
//                    _uiEvent.trySend(CameraScanUiEvent.ShowToast("Invalid URL"))
//
//                }
                }


                CameraScanEvent.OnOpenUrlFromScannedQrCode -> {
                    if (_state.value.isGoodUrlRegex) {
                        _uiEvent.send(OpenUrl)
                        _state.update {
                            it.copy(shouldURLDialog = false)
                        }
                    } else {
                        _uiEvent.send(ShowToast("Invalid URL"))
                    }
                }

                CameraScanEvent.OnDismissUrlDialog -> {
                    _state.update {
                        it.copy(shouldURLDialog = false)
                    }
                }
            }

        }
    }

    fun isValidUrlWithRegex(url: String): Boolean {
        val urlPattern = Regex("""https?://(?:www\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)""")
        return urlPattern.matches(url)
    }
}

