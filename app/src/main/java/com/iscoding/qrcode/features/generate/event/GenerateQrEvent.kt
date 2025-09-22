package com.iscoding.qrcode.features.generate.event

import com.iscoding.qrcode.features.generate.util.QrDataType

sealed class GenerateQrEvent {
    //  Input Field Updates
    data class OnTextChanged(val text: String) : GenerateQrEvent()

    data class OnUrlChanged(val url: String) : GenerateQrEvent()

    data class OnMailChanged(val email: String) : GenerateQrEvent()

    data class OnTelChanged(val tel: String) : GenerateQrEvent()

    data class OnSmsNumberChanged(val number: String) : GenerateQrEvent()

    data class OnSmsMessageChanged(val message: String) : GenerateQrEvent()

    data class OnGeoLatitudeChanged(val latitude: String) : GenerateQrEvent()

    data class OnGeoLongitudeChanged(val longitude: String) : GenerateQrEvent()

    data class OnEventSubjectChanged(val subject: String) : GenerateQrEvent()

    data class OnEventDTStartChanged(val start: String) : GenerateQrEvent()

    data class OnEventDTEndChanged(val end: String) : GenerateQrEvent()

    data class OnEventLocationChanged(val location: String) : GenerateQrEvent()

    //  Type Selection
    data class OnTypePicked(val type: QrDataType) : GenerateQrEvent()

    //  Clear output
    data class ClearQRCode(val type: QrDataType) : GenerateQrEvent()

    //  Validation & Formatting
//    object ValidateAndFormat : GenerateQrEvent()

    //  Final QR Code Generation
    object GenerateQRCode : GenerateQrEvent()

    //  Share / Save
    object ShareQRCode : GenerateQrEvent()
}
