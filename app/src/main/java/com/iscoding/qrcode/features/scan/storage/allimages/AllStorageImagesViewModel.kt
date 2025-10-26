package com.iscoding.qrcode.features.scan.storage.allimages

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.iscoding.qrcode.data.errors.ErrorReporter
import com.iscoding.qrcode.domain.errors.MediaError
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
import logcat.logcat
import java.io.InputStream

/**
 * ViewModel for managing the state and events of the "Scan QR from Storage Images" screen.
 *
 * Responsibilities:
 * 1. Handle storage permission checks and requests.
 * 2. Load images and albums from device storage with pagination.
 * 3. Analyze images for QR codes.
 * 4. Emit one-time UI effects such as toasts and navigation events.
 *
 * @property imageStorageAnalyzer Handles QR code scanning from images.
 * @property mediaRepository Provides access to storage images and albums.
 */
class AllStorageImagesViewModel(
    val imageStorageAnalyzer: QrCodeStorageAnalyzer,
    val mediaRepository: MediaRepository,
) : ViewModel() {

    /** Internal mutable state for the UI */
    private val _uiState = MutableStateFlow(AllStorageImagesUiState())

    /** Immutable UI state exposed to the Composables */
    val uiState get() = _uiState.asStateFlow()

    /** Channel for emitting one-time UI effects (navigation, toasts, etc.) */
    private val _effect = Channel<AllStorageImagesEffect>()
    val effect = _effect.receiveAsFlow()

    /**
     * Handles all events coming from the UI (Composables).
     *
     * @param event [AllStorageImagesEvent] representing a user or system action.
     */
    fun onEvent(event: AllStorageImagesEvent) {
        when (event) {
            is AllStorageImagesEvent.OnInitialPermissionCheck -> {
                _uiState.update { it.copy(hasStoragePermission = event.hasPermission) }
                loadInitialData()
                if (!event.hasPermission) requestPermission()
            }

            is AllStorageImagesEvent.OnStoragePermissionResult -> {
                handlePermissionResult(event.granted, event.shouldShowRationale)
            }

            is AllStorageImagesEvent.OnPermissionStatusChanged -> {
                val previousPermission = _uiState.value.hasStoragePermission
                _uiState.update { it.copy(hasStoragePermission = event.hasPermission) }

                // Handle newly granted permission
                if (!previousPermission && event.hasPermission) {
                    _uiState.update {
                        it.copy(
                            shouldPermissionDialog = false,
                            shouldLaunchAppSettings = false,
                        )
                    }
                    viewModelScope.launch {
                        _effect.send(AllStorageImagesEffect.ShowToast("Camera permission granted!"))
                    }
                    onEvent(AllStorageImagesEvent.LoadInitialData)
                }
            }

            AllStorageImagesEvent.OnRequestStoragePermission -> requestPermission()

            AllStorageImagesEvent.OnDismissPermissionDialog -> {
                _uiState.update { it.copy(shouldPermissionDialog = false) }
            }

            AllStorageImagesEvent.OnOpenAppSettings -> openAppSettings()

            is AllStorageImagesEvent.OnStorageImageClicked -> {
                logcat("ISLAMMM") { "image clicked !$event.uri" }
                onEvent(AllStorageImagesEvent.OnAnalyzeImage(event.uri, event.inputStream))
                _uiState.update { it.copy(clickedImageUri = event.uri.toString()) }
            }

            is AllStorageImagesEvent.LoadInitialData -> loadInitialData()

            is AllStorageImagesEvent.RefreshImages -> refreshImages()

            is AllStorageImagesEvent.OnAnalyzeImage -> {
                analyzeImage(event.uri, event.inputStream)
            }

            is AllStorageImagesEvent.LoadMoreImages -> loadMoreImages()

            is AllStorageImagesEvent.SelectAlbum -> {
                _uiState.update {
                    it.copy(
                        selectedAlbum = event.albumName,
                        storageImagesList = emptyList(),
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

    /** Loads the first page of all images and albums. */
    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = "") }
            delay(600)

            val photosResult = mediaRepository.loadPhotos(limit = _uiState.value.pageSize, offset = 0)
            val albumsResult = mediaRepository.loadAlbums()

            when {
                photosResult is Either.Right && albumsResult is Either.Right -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            storageImagesList = photosResult.value,
                            albums = albumsResult.value,
                            hasMoreImages = photosResult.value.size == it.pageSize,
                            currentPage = 1,
                        )
                    }
                }

                photosResult is Either.Left -> handleError(photosResult.value)
                albumsResult is Either.Left -> handleError(albumsResult.value)
            }
        }
    }

    /** Loads more images for pagination based on current state. */
    private fun loadMoreImages() {
        val current = _uiState.value
        if (current.isLoadingMore || !current.hasMoreImages) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }

            val offset = current.currentPage * current.pageSize
            val result = if (current.selectedAlbum != null) {
                mediaRepository.loadPhotosForAlbum(current.selectedAlbum, current.pageSize, offset)
            } else {
                mediaRepository.loadPhotos(current.pageSize, offset)
            }

            when (result) {
                is Either.Right -> _uiState.update {
                    it.copy(
                        isLoadingMore = false,
                        storageImagesList = it.storageImagesList + result.value,
                        hasMoreImages = result.value.size == it.pageSize,
                        currentPage = it.currentPage + 1,
                    )
                }
                is Either.Left -> handleError(result.value)
            }
        }
    }

    /** Refreshes images based on current selected album or all images. */
    private fun refreshImages() {
        _uiState.update { it.copy(storageImagesList = emptyList(), currentPage = 0, hasMoreImages = true) }
        if (_uiState.value.selectedAlbum != null) {
            loadImagesForAlbum(_uiState.value.selectedAlbum!!, 0)
        } else {
            loadInitialData()
        }
    }

    /**
     * Loads images for a specific album with pagination support.
     *
     * @param albumName The name of the album.
     * @param page The page number to load (0-based).
     */
    private fun loadImagesForAlbum(albumName: String, page: Int) {
        viewModelScope.launch {
            if (page == 0) _uiState.update { it.copy(isLoading = true) }

            val offset = page * _uiState.value.pageSize
            val result = mediaRepository.loadPhotosForAlbum(albumName, _uiState.value.pageSize, offset)

            when (result) {
                is Either.Right -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        storageImagesList = if (page == 0) result.value else it.storageImagesList + result.value,
                        hasMoreImages = result.value.size == it.pageSize,
                        currentPage = if (page == 0) 1 else it.currentPage + 1,
                    )
                }
                is Either.Left -> handleError(result.value)
            }
        }
    }

    /** Opens the app settings for manually granting storage permission. */
    private fun openAppSettings() {
        viewModelScope.launch {
            _effect.send(AllStorageImagesEffect.OpenAppSettings)
        }
    }

    /**
     * Handles storage permission results.
     *
     * @param granted True if permission granted.
     * @param shouldShowRationale True if system suggests showing rationale.
     */
    private fun handlePermissionResult(granted: Boolean, shouldShowRationale: Boolean) {
        if (granted) {
            _uiState.update { it.copy(hasStoragePermission = true, shouldPermissionDialog = false, shouldLaunchAppSettings = false) }
            loadInitialData()
        } else {
            _uiState.update {
                it.copy(hasStoragePermission = false, shouldPermissionDialog = true, shouldLaunchAppSettings = !shouldShowRationale)
            }
            viewModelScope.launch {
                _effect.send(
                    AllStorageImagesEffect.ShowToast(
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

    /** Requests storage permission via effect. */
    private fun requestPermission() {
        viewModelScope.launch {
            _effect.send(AllStorageImagesEffect.RequestStoragePermission)
        }
    }

    /**
     * Analyzes the given image for QR codes and updates state accordingly.
     *
     * @param uri The URI of the selected image.
     * @param inputStream InputStream of the selected image.
     */
    private fun analyzeImage(uri: Uri, inputStream: InputStream) {
        logcat("ISLAMMM") { "image clicked and now analyze" }

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
                    _uiState.update { it.copy(isLoading = false, scannedData = qr, clickedImageUri = uri.toString()) }
                    viewModelScope.launch {
                        _effect.send(AllStorageImagesEffect.NavigateToQrDetailsScreen(_uiState.value.scannedData, _uiState.value.clickedImageUri!!))
                    }
                },
            )
        }
    }

    private suspend fun handleError(error: MediaError) {
        _uiState.update { it.copy(isLoading = false, isLoadingMore = false, errorMessage = error.toString()) }
        _effect.send(AllStorageImagesEffect.ShowToast("Error: ${error::class.simpleName}"))
        ErrorReporter.log(error) // logs to Sentry if needed
    }
}
