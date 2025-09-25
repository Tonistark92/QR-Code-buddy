package com.iscoding.qrcode.features.scan.storage.allimages

import androidx.compose.ui.geometry.Offset
import com.iscoding.qrcode.domain.model.SharedStoragePhoto

data class AllStorageImagesUiState(
    val storageImagesList: List<SharedStoragePhoto> = emptyList(),
    val albums: List<String> = emptyList(),
    val selectedAlbum: String? = null,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMoreImages: Boolean = true,
    val currentPage: Int = 0,
    val pageSize: Int = 50,
    val noQrFound: Boolean = false,
    val clickedImageUri: String? = null,
    val hasStoragePermission: Boolean = false,
    val shouldPermissionDialog: Boolean = false,
    val shouldURLDialog: Boolean = false,
    val shouldLaunchAppSettings: Boolean = false,
    val scannedData: String = "",
    val errorMessage: String = "",
    val isGoodUrlRegex: Boolean = false,
    var tapPosition: Offset? = null,

)
