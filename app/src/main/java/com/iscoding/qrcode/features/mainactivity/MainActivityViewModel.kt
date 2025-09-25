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

class MainActivityViewModel(val imageStorageAnalyzer: QrCodeStorageAnalyzer) : ViewModel() {
    private val _uiState = MutableStateFlow(MainActivityUiState())
    val uiState get() = _uiState.asStateFlow()

//    val state
//        get() = _uiState
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(5000),
//                initialValue = MainActivityUiState()
//            )

    private val _effect = Channel<MainActivityEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: MainActivityEvent) {
        when (event) {
            is MainActivityEvent.OnNewIntentReceived -> {
                viewModelScope.launch {
                    _effect.send(MainActivityEffect.AnalyzeImage)
                }
            }

            is MainActivityEvent.OnAnalyzeImage -> {
                analyzeImage(event.uri, event.inputStream)
            }
        }
    }

    private fun analyzeImage(uri: Uri, inputStream: InputStream) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            imageStorageAnalyzer.analyze(
                uri,
                inputStream,
                onNoQRCodeFound = {
                    _uiState.update { it.copy(isLoading = false, noQrFound = true) }
                    viewModelScope.launch {
                        _effect.send(MainActivityEffect.ShowToast("No Qr code in that image or Error happened"))
                    }
                },

                onQrCodeScanned = { qr ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            qrCode = qr,
                            imageUri = uri,
                        )
                    }

                    viewModelScope.launch {
                        // Pass the data directly - no delay needed!
                        _effect.send(MainActivityEffect.NavigateToQrDetailsScreen(qr, uri))
                    }
                },
            )
        }
    }
}
