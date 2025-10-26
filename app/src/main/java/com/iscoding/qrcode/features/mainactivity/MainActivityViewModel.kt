package com.iscoding.qrcode.features.mainactivity

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iscoding.qrcode.domain.repos.QrCodeStorageAnalyzer
import com.iscoding.qrcode.features.mainactivity.intent.MainActivityEffect
import com.iscoding.qrcode.features.mainactivity.intent.MainActivityEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.InputStream

/**
 * ViewModel for MainActivity in the QR Code app.
 *
 * Responsibilities:
 * - Maintain UI state through [MainActivityUiState].
 * - Handle events from the UI via [MainActivityEvent].
 * - Trigger one-time effects via [MainActivityEffect], such as navigation or toast messages.
 *
 * @property imageStorageAnalyzer Used to analyze QR codes from images stored on the device.
 */
class MainActivityViewModel(
    val imageStorageAnalyzer: QrCodeStorageAnalyzer,
) : ViewModel() {

    // -------------------------------
    // UI State
    // -------------------------------

    /**
     * Backing state for the UI. Updates here will automatically be reflected in Compose when collected.
     */
    private val _uiState = MutableStateFlow(MainActivityUiState())

    /**
     * Exposed immutable state flow to the UI.
     */
    val uiState = _uiState.asStateFlow()

    // -------------------------------
    // Side Effects
    // -------------------------------

    /**
     * Channel for one-time effects like navigation or toast messages.
     * Using a channel ensures events are handled only once.
     */
    private val _effect = Channel<MainActivityEffect>(Channel.BUFFERED)

    /** Exposed as Flow to allow Compose/Activity to collect effects. */
    val effect = _effect.receiveAsFlow()

    // -------------------------------
    // Event Handling
    // -------------------------------

    /**
     * Handles UI events sent from the MainActivity UI.
     */
    fun onEvent(event: MainActivityEvent) {
        when (event) {
            is MainActivityEvent.OnNewIntentReceived -> {
                // Trigger effect to analyze a newly received image intent
                viewModelScope.launch {
                    _effect.send(MainActivityEffect.AnalyzeImage)
                }
            }

            is MainActivityEvent.OnAnalyzeImage -> {
                // Analyze the selected image from URI or InputStream
                analyzeImage(event.uri, event.inputStream)
            }
        }
    }

    // -------------------------------
    // Private Helpers
    // -------------------------------

    /**
     * Handles image analysis using [QrCodeStorageAnalyzer].
     *
     * Updates UI state to show loading, error, or scanned QR code results.
     */
    private fun analyzeImage(uri: Uri, inputStream: InputStream) {
        viewModelScope.launch {
            // Show loading indicator
            _uiState.update { it.copy(isLoading = true) }

            imageStorageAnalyzer.analyze(
                uri,
                inputStream,
                onNoQRCodeFound = {
                    // Update state to reflect no QR code found
                    _uiState.update { it.copy(isLoading = false, noQrFound = true) }

                    // Trigger a toast message effect
                    viewModelScope.launch {
                        _effect.send(
                            MainActivityEffect.ShowToast(
                                "No QR code in that image or an error occurred",
                            ),
                        )
                    }
                },
                onQrCodeScanned = { qr ->
                    // Update UI with scanned QR code and image URI
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            qrCode = qr,
                            imageUri = uri,
                        )
                    }

                    // Trigger navigation effect to details screen
                    viewModelScope.launch {
                        _effect.send(
                            MainActivityEffect.NavigateToQrDetailsScreen(qr, uri),
                        )
                    }
                },
            )
        }
    }
}
