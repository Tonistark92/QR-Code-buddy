package com.iscoding.qrcode.features.scan.storage.allimages

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iscoding.qrcode.domain.repos.MediaRepository
import com.iscoding.qrcode.domain.repos.QrCodeStorageAnalyzer
import com.iscoding.qrcode.features.scan.storage.allimages.intent.AllStorageImagesEffect
import com.iscoding.qrcode.features.scan.storage.allimages.intent.AllStorageImagesEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.InputStream

class AllStorageImagesViewModel(
    val imageStorageAnalyzer: QrCodeStorageAnalyzer,
    val mediaRepository: MediaRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AllStorageImagesUiState())
    val uiState get() = _uiState.asStateFlow()
    private val _effect = Channel<AllStorageImagesEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: AllStorageImagesEvent) {
        when (event) {
            is AllStorageImagesEvent.OnInitialPermissionCheck -> {
                _uiState.update { it.copy(hasStoragePermission = event.hasPermission) }
                loadInitialData()
                if (!event.hasPermission) {
                    requestPermission()
                }
            }

            is AllStorageImagesEvent.OnStoragePermissionResult -> {
                handlePermissionResult(event.granted, event.shouldShowRationale)
            }

            is AllStorageImagesEvent.OnPermissionStatusChanged -> {
                val previousPermission = _uiState.value.hasStoragePermission
                _uiState.update { it.copy(hasStoragePermission = event.hasPermission) }

                // Special handling when permission is newly granted
                if (!previousPermission && event.hasPermission) {
                    _uiState.update {
                        it.copy(
                            shouldPermissionDialog = false, // Dismiss permission dialog
                            shouldLaunchAppSettings = false, // Reset settings flag
                        )
                    }
                    viewModelScope.launch {
                        // Show success message
                        _effect.send(AllStorageImagesEffect.ShowToast("Camera permission granted!"))
                    }
                    onEvent(AllStorageImagesEvent.LoadInitialData)
                }
            }

            AllStorageImagesEvent.OnRequestStoragePermission -> {
                requestPermission()
            }

            AllStorageImagesEvent.OnDismissPermissionDialog -> {
                _uiState.update { it.copy(shouldPermissionDialog = false) }
            }

            AllStorageImagesEvent.OnOpenAppSettings -> {
                openAppSettings()
            }

            is AllStorageImagesEvent.OnStorageImageClicked -> {
                analyzeImage(event.uri, event.inputStream)
                _uiState.update { it.copy() }
            }

            is AllStorageImagesEvent.LoadInitialData -> loadInitialData()
            is AllStorageImagesEvent.SelectAlbum -> {
                _uiState.update { it.copy(selectedAlbum = event.albumName) }
                refreshImages()
            }

            is AllStorageImagesEvent.RefreshImages -> refreshImages()
//            is AllStorageImagesEvent.LoadImagesForAlbum -> filterByAlbum(event.albumName)
            is AllStorageImagesEvent.OnAnalyzeImage -> {
                analyzeImage(event.uri, event.inputStream)
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val photos = mediaRepository.loadPhotos()
                val albums = mediaRepository.loadAlbums()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        storageImagesList = photos,
                        albums = albums,
                        selectedAlbum = null, // Reset to show all
                        errorMessage = "",
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Unknown error")
                }
                _effect.send(AllStorageImagesEffect.ShowToast("Failed to load images: ${e.message}"))
            }
        }
    }

    private fun refreshImages() {
        if (_uiState.value.selectedAlbum != null) {
            filterByAlbum(_uiState.value.selectedAlbum!!)
        } else {
            loadInitialData()
        }
    }

    private fun filterByAlbum(albumName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val photos = mediaRepository.loadForSelectedAlbum(albumName)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        storageImagesList = photos,
                        errorMessage = "",
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Unknown error")
                }
                _effect.send(AllStorageImagesEffect.ShowToast("Failed to load album images: ${e.message}"))
            }
        }
    }

    private fun openAppSettings() {
        viewModelScope.launch {
            _effect.send(AllStorageImagesEffect.OpenAppSettings)
        }
    }

    private fun handlePermissionResult(
        granted: Boolean,
        shouldShowRationale: Boolean,
    ) {
        if (granted) {
            _uiState.update {
                it.copy(
                    hasStoragePermission = true,
                    shouldPermissionDialog = false,
                    shouldLaunchAppSettings = false,
                )
            }
            loadInitialData()
        } else {
            // Permission denied
            _uiState.update {
                it.copy(
                    hasStoragePermission = false,
                    shouldPermissionDialog = true,
                    shouldLaunchAppSettings = !shouldShowRationale, // If no rationale, go to settings
                )
            }

            viewModelScope.launch {
                if (shouldShowRationale) {
                    _effect.send(AllStorageImagesEffect.ShowToast("Camera permission is needed to scan QR codes"))
                } else {
                    _effect.send(
                        AllStorageImagesEffect.ShowToast("Permission permanently denied. Please enable in settings."),
                    )
                }
            }
        }
    }

    private fun requestPermission() {
        viewModelScope.launch {
            _effect.send(AllStorageImagesEffect.RequestStoragePermission)
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
                        _effect.send(AllStorageImagesEffect.ShowToast("No Qr code in that image or Error happened"))
                    }
                },
                onQrCodeScanned = { qr ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            scannedData = qr,
                            clickedImageUri = uri.toString(),
                        )
                    }

                    viewModelScope.launch {
                        delay(1000)
                        _effect.send(
                            AllStorageImagesEffect.NavigateToQrDetailsScreen(
                                _uiState.value.scannedData,
                                _uiState.value.clickedImageUri!!,
                            ),
                        )
                    }
                },
            )
        }
    }
}
