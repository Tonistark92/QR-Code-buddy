package com.iscoding.qrcode.features.generate

import android.graphics.Bitmap
import com.iscoding.qrcode.features.generate.util.QrDataType

data class GenerateQRCodeState(
    // the final result for each data
    var formattedText: String = "",
    var formattedSMS: String = "",
    var formattedEvent: String = "",
    var formattedGeo: String = "",
    var formattedMail: String = "",
    var formattedTel: String = "",
    var formattedUrl: String = "",
    // the chosen type
    var pickedType: QrDataType = QrDataType.TEXT,
    // for wifi
    var wifiSSID: String = "",
    var errorMessageWifiSSID: String = "Enter the Network Name",
    var shouldShowErrorWifiSSID: Boolean = false,
    var chosenEncryption: String = "",
    var chosenWifiEncryptionType: String = "no pass",
    var wifiPassword: String = "",
    var shouldShowErrorWifiPassword: Boolean = false,
    var errorMessageWifiPassword: String = "Enter a valid Wifi Password (More than 8 digits)",
    var isWifiHidden: Boolean = false,
    // for SMS
    var smsNumber: String = "",
    var smsData: String = "",
    var errorMessageSmsNumber: String = "Enter a valid SMS Number",
    var errorMessageSmsData: String = "Enter a valid SMS Data (more than 4 digits)",
    var shouldShowErrorSmsNumber: Boolean = false,
    var shouldShowErrorSmsData: Boolean = false,
    // for tel
    var tel: String = "",
    var errorMessageTel: String = "Enter a valid Tel Number",
    var shouldShowErrorTel: Boolean = false,
    // for mail
    var mail: String = "",
    var errorMessageMail: String = "Enter a valid Mail",
    var shouldShowErrorMail: Boolean = false,
    // for text
    var plainText: String = "",
    var errorMessagePlainText: String = "Enter text more than 4 char",
    var shouldShowErrorPlainText: Boolean = false,
    // for url
    var url: String = "",
    var errorMessageUrl: String = "Enter a valid URL",
    var shouldShowErrorUrl: Boolean = false,
    // for geo
    var geoLatitude: String = "",
    var errorMessageGeoLatitude: String = "",
    var shouldShowErrorGeoLatitude: Boolean = false,
    var geoLongitude: String = "",
    var errorMessageGeoLongitude: String = "",
    var shouldShowErrorGeoLongitude: Boolean = false,
    // for event
    var eventSubject: String = "",
    var errorMessageEventSubject: String = "The Subject should be more than 4 letters",
    var shouldShowErrorEventSubject: Boolean = false,
    var eventDTStart: String = "",
    var errorMessageEventDTStart: String = "Add the time in this format: 20240622T190000",
    var shouldShowErrorEventDTStart: Boolean = false,
    var eventDTEnd: String = "",
    var errorMessageEventDTEnd: String = "Add the time in this format: 20240622T190000",
    var shouldShowErrorEventDTEnd: Boolean = false,
    var eventLocation: String = "",
    var errorMessageEventLocation: String = "The Location should be more than 4 letters",
    var shouldShowErrorEventLocation: Boolean = false,
    // for the qr img
    var qrBitmap: Bitmap? = null,
    // screen loading state
    var isLoading: Boolean = false,
//    var errorMessage: String? = null,
)
