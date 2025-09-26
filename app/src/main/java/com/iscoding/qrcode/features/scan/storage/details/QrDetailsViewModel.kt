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

/**
 * ViewModel for the QR Details screen.
 *
 * Handles the UI state and one-time effects for the screen that displays
 * information about a scanned QR code and its source image.
 */
class QrDetailsViewModel : ViewModel() {

    /** Internal mutable state for the screen UI. */
    private val _uiState = MutableStateFlow(QrDetailsUiState())

    /** Public immutable state exposed to the UI. */
    val uiState get() = _uiState.asStateFlow()

    /** Channel for one-time effects, e.g., navigation, toast messages. */
    private val _effect = Channel<QrDetailsEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    /**
     * Handles events coming from the UI.
     *
     * @param event The [QrDetailsEvent] triggered by the UI.
     */
    fun onEvent(event: QrDetailsEvent) {
        when (event) {
            is QrDetailsEvent.OnInitialDetailsScreen -> {
                // Update state with QR data, image URL, and validity check
                _uiState.update {
                    it.copy(
                        qrData = event.qrCodeData,
                        imageUrl = event.imageUri,
                        isValidUrl = isValidUrlWithRegex(event.qrCodeData),
                    )
                }

                // Simulate loading for 1 second, then mark as loaded
                viewModelScope.launch {
                    delay(1000)
                    _uiState.update { it.copy(isLoading = false) }
                }
            }

            QrDetailsEvent.OnOpenUrl -> {
                viewModelScope.launch {
                    // Trigger navigation or intent to open the URL
                    _effect.send(QrDetailsEffect.OnOpenUrl(_uiState.value.imageUrl))
                }
            }

            QrDetailsEvent.OnQrDataLongPressed -> {
                viewModelScope.launch {
                    // Trigger effects for long press: copy text and show toast
                    _effect.send(QrDetailsEffect.OnQrDataLongPressed(_uiState.value.qrData))
                    _effect.send(QrDetailsEffect.ShowToast("Text copied to clipboard"))
                }
            }
        }
    }

    /**
     * Checks whether a given string is a valid URL using regex.
     *
     * @param url The string to validate.
     * @return True if the string is a valid URL, false otherwise.
     */
    private fun isValidUrlWithRegex(url: String): Boolean {
        val urlPattern =
            Regex(
                """https?://(?:www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&//=]*)""",
            )
        return urlPattern.matches(url)
    }
}
