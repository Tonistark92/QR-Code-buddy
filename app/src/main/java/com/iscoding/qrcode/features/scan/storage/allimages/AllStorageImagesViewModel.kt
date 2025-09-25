package com.iscoding.qrcode.features.scan.storage.allimages

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iscoding.qrcode.domain.repos.MediaRepository
import com.iscoding.qrcode.domain.repos.QrCodeStorageAnalyzer
import com.iscoding.qrcode.features.scan.storage.allimages.intent.AllStorageImagesEffect
import com.iscoding.qrcode.features.scan.storage.allimages.intent.AllStorageImagesEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import logcat.logcat
import java.io.InputStream

class AllStorageImagesViewModel(
    val imageStorageAnalyzer: QrCodeStorageAnalyzer,
    val mediaRepository: MediaRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AllStorageImagesUiState())
    val uiState get() = _uiState.asStateFlow()
    private val _effect = Channel<AllStorageImagesEffect>()
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
                logcat("ISLAMMM") { "image clicked !$event.uri" }
                onEvent(AllStorageImagesEvent.OnAnalyzeImage(event.uri, event.inputStream))
                _uiState.update { it.copy(clickedImageUri = event.uri.toString()) }
            }

            is AllStorageImagesEvent.LoadInitialData -> loadInitialData()
//
//            is AllStorageImagesEvent.SelectAlbum -> {
//                _uiState.update {
//                    it.copy(
//                        selectedAlbum = event.albumName,
//                        storageImagesList = emptyList(), // Clear existing images
//                        currentPage = 0,
//                        hasMoreImages = true
//                    )
//                }
//                refreshImages()
//            }

            is AllStorageImagesEvent.RefreshImages -> refreshImages()
//            is AllStorageImagesEvent.LoadImagesForAlbum -> filterByAlbum(event.albumName)
            is AllStorageImagesEvent.OnAnalyzeImage -> {
                analyzeImage(event.uri, event.inputStream)
            }

            is AllStorageImagesEvent.LoadMoreImages -> loadMoreImages()

            is AllStorageImagesEvent.SelectAlbum -> {
                _uiState.update {
                    it.copy(
                        selectedAlbum = event.albumName,
                        storageImagesList = emptyList(), // Clear existing images
                        currentPage = 0,
                        hasMoreImages = true,
                    )
                }
                if (event.albumName != null) {
                    loadImagesForAlbum(event.albumName, 0)
                } else {
                    loadInitialData()
                }
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, currentPage = 0, hasMoreImages = true) }
            try {
                val photos = mediaRepository.loadPhotos(
                    limit = _uiState.value.pageSize,
                    offset = 0,
                )
                val albums = mediaRepository.loadAlbums()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        storageImagesList = photos,
                        albums = albums,
                        selectedAlbum = null,
                        errorMessage = "",
                        hasMoreImages = photos.size == _uiState.value.pageSize,
                        currentPage = 1,
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

    private fun loadMoreImages() {
        val currentState = _uiState.value
        if (currentState.isLoadingMore || !currentState.hasMoreImages) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            try {
                val offset = currentState.currentPage * currentState.pageSize
                val newPhotos = if (currentState.selectedAlbum != null) {
                    mediaRepository.loadPhotosForAlbum(
                        album = currentState.selectedAlbum,
                        limit = currentState.pageSize,
                        offset = offset,
                    )
                } else {
                    mediaRepository.loadPhotos(
                        limit = currentState.pageSize,
                        offset = offset,
                    )
                }

                _uiState.update {
                    it.copy(
                        isLoadingMore = false,
                        storageImagesList = it.storageImagesList + newPhotos,
                        hasMoreImages = newPhotos.size == currentState.pageSize,
                        currentPage = it.currentPage + 1,
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoadingMore = false)
                }
                _effect.send(AllStorageImagesEffect.ShowToast("Failed to load more images: ${e.message}"))
            }
        }
    }

    private fun refreshImages() {
        _uiState.update {
            it.copy(
                storageImagesList = emptyList(),
                currentPage = 0,
                hasMoreImages = true,
            )
        }
        if (_uiState.value.selectedAlbum != null) {
            loadImagesForAlbum(_uiState.value.selectedAlbum!!, 0)
        } else {
            loadInitialData()
        }
    }

    private fun loadImagesForAlbum(albumName: String, page: Int) {
        viewModelScope.launch {
            if (page == 0) {
                _uiState.update { it.copy(isLoading = true) }
            } else {
                _uiState.update { it.copy(isLoadingMore = true) }
            }

            try {
                val offset = page * _uiState.value.pageSize
                val photos = mediaRepository.loadPhotosForAlbum(
                    album = albumName,
                    limit = _uiState.value.pageSize,
                    offset = offset,
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        storageImagesList = if (page == 0) photos else it.storageImagesList + photos,
                        errorMessage = "",
                        hasMoreImages = photos.size == _uiState.value.pageSize,
                        currentPage = if (page == 0) 1 else it.currentPage + 1,
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        errorMessage = e.message ?: "Unknown error",
                    )
                }
                _effect.send(AllStorageImagesEffect.ShowToast("Failed to load album images: ${e.message}"))
            }
        }
    }

//    private fun loadInitialData() {
//        viewModelScope.launch {
//            _uiState.update { it.copy(isLoading = true) }
//            try {
//                val photos = mediaRepository.loadPhotos()
//                val albums = mediaRepository.loadAlbums()
//
//                _uiState.update {
//                    it.copy(
//                        isLoading = false,
//                        storageImagesList = photos,
//                        albums = albums,
//                        selectedAlbum = null, // Reset to show all
//                        errorMessage = "",
//                    )
//                }
//            } catch (e: Exception) {
//                _uiState.update {
//                    it.copy(isLoading = false, errorMessage = e.message ?: "Unknown error")
//                }
//                _effect.send(AllStorageImagesEffect.ShowToast("Failed to load images: ${e.message}"))
//            }
//        }
//    }
//
//    private fun refreshImages() {
//        if (_uiState.value.selectedAlbum != null) {
//            filterByAlbum(_uiState.value.selectedAlbum!!)
//        } else {
//            loadInitialData()
//        }
//    }

    private fun filterByAlbum(albumName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val photos =
                    mediaRepository.loadForSelectedAlbum(albumName, _uiState.value.pageSize, 0)
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
        logcat("ISLAMMM") { "image clicked and now analyze " }

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
