package com.iscoding.qrcode.features.scan.storage.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iscoding.qrcode.features.scan.storage.details.intent.QrDetailsEffect
import com.iscoding.qrcode.features.scan.storage.details.intent.QrDetailsEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QrDetailsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(QrDetailsUiState())
    val uiState get() = _uiState.asStateFlow()
    private val _effect = Channel<QrDetailsEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: QrDetailsEvent) {
        when (event) {
            is QrDetailsEvent.OnInitialDetailsScreen -> {
                _uiState.update { it.copy(event.qrCodeData, event.imageUri, isValidUrl = isValidUrlWithRegex(event.qrCodeData)) }

                viewModelScope.launch {
                    delay(1000)
                    _uiState.update { it.copy(isLoading = false) }
                }
            }

            QrDetailsEvent.OnOpenUrl -> {
                viewModelScope.launch {
                    _effect.send(QrDetailsEffect.OnOpenUrl(_uiState.value.imageUrl))
                }
            }

            QrDetailsEvent.OnQrDataLongPressed -> {
                viewModelScope.launch {
                    _effect.send(QrDetailsEffect.OnQrDataLongPressed(_uiState.value.qrData))
                    _effect.send(QrDetailsEffect.ShowToast("Text copied to clipboard"))
                }
            }
        }
    }

    private fun isValidUrlWithRegex(url: String): Boolean {
        val urlPattern =
            Regex(
                """https?://(?:www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&//=]*)""",
            )
        return urlPattern.matches(url)
    }
}
