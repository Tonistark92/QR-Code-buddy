package com.iscoding.qrcode.features.scan.storage.details

data class QrDetailsUiState(

    val qrData: String = "",
    val imageUrl: String = "",
    val errorMessage: String = "",
    val isLoading: Boolean = true,
    val isValidUrl: Boolean = false,

)
