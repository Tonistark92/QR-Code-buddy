package com.iscoding.qrcode.features.generatecode

import android.graphics.Bitmap

data class GenerateQRCodeState(
    var formattedText: String = "",
    var formattedSMS: String = "",
    var formattedEvent: String = "",
    var formattedGeo: String = "",
    var formattedMail: String = "",
    var formattedTel: String = "",
    var formattedUrl: String = "",


    var pickedType: String = "Text",



    var wifiSSID: String = "",
    var errorMessageWifiSSID: String = "Enter the Network Name",
    var shouldShowErrorWifiSSID: Boolean = false,
    var choosenEncryption: String = "",
    var choosenWifiEncryptionType: String = "nopass",
    var wifiPassword: String = "",
    var shouldShowErrorWifiPassword: Boolean = false,
    var errorMessageWifiPassword: String = "Enter a valid Wifi Password (More than 8 digits)",
    var isWifiHidden: Boolean = false,
    var smsNumber: String = "",
    var smsData: String = "",
    var errorMessageSmsNumber: String = "Enter a valid SMS Number",
    var errorMessageSmsData: String = "Enter a valid SMS Data (more than 4 digits)",
    var shouldShowErrorSmsNumber: Boolean = false,
    var shouldShowErrorSmsData: Boolean = false,
    var tel: String = "",
    var errorMessageTel: String = "Enter a valid Tel Number",
    var shouldShowErrorTel: Boolean = false,
    var mail: String = "",
    var errorMessageMail: String = "Enter a valid Mail",
    var shouldShowErrorMail: Boolean = false,
    var plainText: String = "",
    var errorMessagePlainText: String = "Enter text more than 4 char",
    var shouldShowErrorPlainText: Boolean = false,
    var url: String = "",
    var errorMessageUrl: String = "Enter a valid URL",
    var shouldShowErrorUrl: Boolean = false,
    var geoLatitude: String = "",
    var errorMessageGeoLatitude: String = "",
    var shouldShowErrorGeoLatitude: Boolean = false,
    var geoLongitude: String = "",
    var errorMessageGeoLongitude: String = "",
    var shouldShowErrorGeoLongitude: Boolean = false,
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
    var qrBitmap: Bitmap? = null,









    var isLoading: Boolean = false,
    var errorMessage: String? = null,

    )