package com.iscoding.qrcode.features.generate

import android.graphics.Bitmap
import com.iscoding.qrcode.features.generate.util.QrDataType

/**
 * UI State for the Generate QR Code screen.
 *
 * This class holds all input values, validation flags, error messages,
 * and the final formatted data needed for QR code generation.
 *
 * Following MVI principles:
 * - State is immutable (all properties are `val`).
 * - UI reads state via [GenerateQRCodeViewModel].
 * - Events update this state through reducer functions.
 */
data class GenerateQRCodeState(

    // -------------------------------
    // Final results for each QR type
    // -------------------------------
    val formattedText: String = "",
    val formattedSMS: String = "",
    val formattedEvent: String = "",
    val formattedGeo: String = "",
    val formattedMail: String = "",
    val formattedTel: String = "",
    val formattedUrl: String = "",
    val formattedWifi: String = "",

    // -------------------------------
    // Selected QR type
    // -------------------------------
    var pickedType: QrDataType = QrDataType.TEXT,

    // -------------------------------
    // WiFi QR data
    // -------------------------------
    val wifiSSID: String = "",
    val shouldShowErrorWifiSSID: Boolean = false,
    val errorMessageWifiSSID: String = "Enter the Network Name",

    val wifiPassword: String = "",
    val shouldShowErrorWifiPassword: Boolean = false,
    val errorMessageWifiPassword: String = "Enter a valid WiFi Password (more than 8 characters)",

    val chosenWifiEncryptionType: String = "nopass", // e.g., WEP, WPA, WPA2, nopass
    val isWifiHidden: Boolean = false,

    // -------------------------------
    // SMS QR data
    // -------------------------------
    val smsNumber: String = "",
    val smsData: String = "",
    val shouldShowErrorSmsNumber: Boolean = false,
    val errorMessageSmsNumber: String = "Enter a valid SMS Number",
    val shouldShowErrorSmsData: Boolean = false,
    val errorMessageSmsData: String = "Enter a valid SMS Data (more than 4 characters)",

    // -------------------------------
    // Telephone QR data
    // -------------------------------
    val tel: String = "",
    val shouldShowErrorTel: Boolean = false,
    val errorMessageTel: String = "Enter a valid telephone number",

    // -------------------------------
    // Email QR data
    // -------------------------------
    val mail: String = "",
    val shouldShowErrorMail: Boolean = false,
    val errorMessageMail: String = "Enter a valid email address",

    // -------------------------------
    // Plain Text QR data
    // -------------------------------
    val plainText: String = "",
    val shouldShowErrorPlainText: Boolean = false,
    val errorMessagePlainText: String = "Enter text with more than 4 characters",

    // -------------------------------
    // URL QR data
    // -------------------------------
    val url: String = "",
    val shouldShowErrorUrl: Boolean = false,
    val errorMessageUrl: String = "Enter a valid URL",

    // -------------------------------
    // Geo QR data
    // -------------------------------
    val geoLatitude: String = "",
    val shouldShowErrorGeoLatitude: Boolean = false,
    val errorMessageGeoLatitude: String = "Enter a valid latitude (-90 to 90)",

    val geoLongitude: String = "",
    val shouldShowErrorGeoLongitude: Boolean = false,
    val errorMessageGeoLongitude: String = "Enter a valid longitude (-180 to 180)",

    // -------------------------------
    // Event QR data
    // -------------------------------
    val eventSubject: String = "",
    val shouldShowErrorEventSubject: Boolean = false,
    val errorMessageEventSubject: String = "The Subject should be more than 4 characters",

    val eventDTStart: String = "",
    val shouldShowErrorEventDTStart: Boolean = false,
    val errorMessageEventDTStart: String = "Add the time in this format: 20240622T190000",

    val eventDTEnd: String = "",
    val shouldShowErrorEventDTEnd: Boolean = false,
    val errorMessageEventDTEnd: String = "Add the time in this format: 20240622T190000",

    val eventLocation: String = "",
    val shouldShowErrorEventLocation: Boolean = false,
    val errorMessageEventLocation: String = "The Location should be more than 4 characters",

    // -------------------------------
    // Generated QR Image
    // -------------------------------
    var qrBitmap: Bitmap? = null,

    // -------------------------------
    // UI Loading State
    // -------------------------------
    val isLoading: Boolean = false,
)
