package com.iscoding.qrcode.features.generate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iscoding.qrcode.domain.repos.QrCodeGenerator
import com.iscoding.qrcode.features.generate.event.GenerateQRCodeUiEvent
import com.iscoding.qrcode.features.generate.event.GenerateQrEvent
import com.iscoding.qrcode.features.generate.util.QrDataType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GenerateQRCodeViewModel(
    private val qrCodeGenerator: QrCodeGenerator,
) : ViewModel() {
    private val _state = MutableStateFlow(GenerateQRCodeState())
    val state get() = _state.asStateFlow()

    private val _uiEvent = Channel<GenerateQRCodeUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun validateInput(chosenData: QrDataType): Boolean {
        return when (chosenData) {
            QrDataType.MAIL -> {
                val emailPattern = Regex("""[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}""")

                if (_state.value.mail.isNotEmpty() && emailPattern.matches(_state.value.mail)) {
                    _state.update { it.copy(shouldShowErrorMail = false) }
                    true
                } else {
                    _state.update { it.copy(shouldShowErrorMail = true) }
                    false
                }
            }

            QrDataType.URL -> {
                val urlPattern =
                    Regex(
                        """https?://(?:www\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)""",
                    )

                if (_state.value.url.isNotEmpty() && urlPattern.matches(_state.value.url)) {
                    _state.update { it.copy(shouldShowErrorUrl = false) }
                    true
                } else {
                    _state.update { it.copy(shouldShowErrorUrl = true) }
                    false
                }
            }

            QrDataType.GEO -> {
                val latitudePattern = Regex("^-?([1-8]?[1-9]|[1-9]0)\\.{0,1}\\d{0,6}$")
                val longitudePattern = Regex("^-?((1?[0-7]?|[0-9]?)[0-9]|180)\\.{0,1}\\d{0,6}$")

                if (_state.value.geoLatitude.isNotEmpty() && latitudePattern.matches(_state.value.geoLatitude) &&
                    _state.value.geoLongitude.isNotEmpty() && longitudePattern.matches(_state.value.geoLongitude)
                ) {
                    _state.update { it.copy(shouldShowErrorGeoLatitude = false) }
                    _state.update { it.copy(shouldShowErrorGeoLongitude = false) }
                    true
                } else {
                    if (_state.value.geoLatitude.isEmpty() || !latitudePattern.matches(_state.value.geoLatitude)) {
                        _state.update { it.copy(shouldShowErrorGeoLatitude = true) }
                    } else {
                        _state.update { it.copy(shouldShowErrorGeoLatitude = false) }
                    }

                    if (_state.value.geoLongitude.isEmpty() || !longitudePattern.matches(_state.value.geoLongitude)) {
                        _state.update { it.copy(shouldShowErrorGeoLongitude = true) }
                    } else {
                        _state.update { it.copy(shouldShowErrorGeoLongitude = false) }
                    }

                    false
                }
            }

            QrDataType.EVENT -> {
                val dateTimePattern = Regex("^\\d{8}T\\d{6}$")

                val isSubjectValid = _state.value.eventSubject.isNotEmpty()
                val isLocationValid = _state.value.eventLocation.isNotEmpty()
                val isStartValid =
                    _state.value.eventDTStart.isNotEmpty() && dateTimePattern.matches(_state.value.eventDTStart)
                val isEndValid =
                    _state.value.eventDTEnd.isNotEmpty() && dateTimePattern.matches(_state.value.eventDTEnd)

                _state.update {
                    it.copy(
                        shouldShowErrorEventSubject = !isSubjectValid,
                        shouldShowErrorEventLocation = !isLocationValid,
                        shouldShowErrorEventDTStart = !isStartValid,
                        shouldShowErrorEventDTEnd = !isEndValid,
                    )
                }

                isSubjectValid && isLocationValid && isStartValid && isEndValid
            }

            QrDataType.SMS -> {
                val isNumberValid = _state.value.smsNumber.isNotEmpty()
                val isMessageValid = _state.value.smsData.isNotEmpty()

                _state.update {
                    it.copy(
                        shouldShowErrorSmsNumber = !isNumberValid,
                        shouldShowErrorSmsData = !isMessageValid,
                    )
                }

                isNumberValid && isMessageValid
            }

            QrDataType.TEL -> {
                val isValid = _state.value.tel.isNotEmpty()
                _state.update { it.copy(shouldShowErrorTel = !isValid) }
                isValid
            }

            QrDataType.TEXT -> {
                val isValid = _state.value.plainText.isNotEmpty()
                _state.update { it.copy(shouldShowErrorPlainText = !isValid) }
                isValid
            }
        }
    }

    fun formatData(type: QrDataType): Boolean {
        var isFormattedAndReady = false

        when (type) {
            QrDataType.URL -> {
                if (validateInput(QrDataType.URL)) {
                    _state.update { it.copy(formattedUrl = _state.value.url) }
                    isFormattedAndReady = true
                }
            }

            QrDataType.MAIL -> {
                if (validateInput(QrDataType.MAIL)) {
                    val formattedMail =
                        if (_state.value.mail.startsWith("mailto:")) {
                            _state.value.mail
                        } else {
                            "mailto:${_state.value.mail}"
                        }
                    _state.update { it.copy(formattedMail = formattedMail) }
                    isFormattedAndReady = true
                }
            }

            QrDataType.TEL -> {
                if (validateInput(QrDataType.TEL)) {
                    val formattedTel =
                        if (_state.value.tel.startsWith("tel:")) {
                            _state.value.tel
                        } else {
                            "tel:${_state.value.tel}"
                        }
                    _state.update { it.copy(formattedTel = formattedTel) }
                    isFormattedAndReady = true
                }
            }

            QrDataType.SMS -> {
                if (validateInput(QrDataType.SMS)) {
                    val formattedSMS =
                        if (_state.value.formattedSMS.startsWith("sms:")) {
                            "\n Tel: ${_state.value.smsNumber} \n data: ${_state.value.smsData}".trimMargin()
                        } else {
                            " \n sms: \n Tel: ${_state.value.smsNumber} \n data: ${_state.value.smsData}".trimMargin()
                        }

                    _state.update {
                        it.copy(
                            smsData = _state.value.smsData,
                            smsNumber = _state.value.smsNumber,
                            formattedSMS = formattedSMS,
                        )
                    }
                    isFormattedAndReady = true
                }
            }

            QrDataType.GEO -> {
                if (validateInput(QrDataType.GEO)) {
                    val formattedGeo =
                        if (_state.value.formattedGeo.startsWith("geo:")) {
                            """
                     lat: ${_state.value.geoLatitude}
                     long: ${_state.value.geoLongitude}
                            """.trimMargin()
                        } else {
                            """
                     geo:
                     lat: ${_state.value.geoLatitude}
                     long: ${_state.value.geoLongitude}
                            """.trimMargin()
                        }

                    _state.update { it.copy(formattedGeo = formattedGeo) }
                    isFormattedAndReady = true
                }
            }

            QrDataType.EVENT -> {
                if (validateInput(QrDataType.EVENT)) {
                    val formattedEvent =
                        """
                        BEGIN:EVENT
                        SUMMARY:${_state.value.eventSubject}
                        DTSTART:${_state.value.eventDTStart}
                        DTEND:${_state.value.eventDTEnd}
                        LOCATION:${_state.value.eventLocation}
                        END:EVENT
                        """.trimIndent()

                    _state.update { it.copy(formattedEvent = formattedEvent) }
                    isFormattedAndReady = true
                }
            }

            QrDataType.TEXT -> {
                if (validateInput(QrDataType.TEXT)) {
                    _state.update { it.copy(formattedText = _state.value.plainText) }
                    isFormattedAndReady = true
                }
            }
        }

        return isFormattedAndReady
    }

    fun onEvent(event: GenerateQrEvent) {
        when (event) {
            //  Text Input Fields
            is GenerateQrEvent.OnTextChanged -> {
                _state.update { it.copy(plainText = event.text) }
            }

            is GenerateQrEvent.OnUrlChanged -> {
                _state.update { it.copy(url = event.url) }
            }

            is GenerateQrEvent.OnMailChanged -> {
                _state.update { it.copy(mail = event.email) }
            }

            is GenerateQrEvent.OnTelChanged -> {
                _state.update { it.copy(tel = event.tel) }
            }

            is GenerateQrEvent.OnSmsNumberChanged -> {
                _state.update { it.copy(smsNumber = event.number) }
            }

            is GenerateQrEvent.OnSmsMessageChanged -> {
                _state.update { it.copy(smsData = event.message) }
            }

            is GenerateQrEvent.OnGeoLatitudeChanged -> {
                _state.update { it.copy(geoLatitude = event.latitude) }
            }

            is GenerateQrEvent.OnGeoLongitudeChanged -> {
                _state.update { it.copy(geoLongitude = event.longitude) }
            }

            is GenerateQrEvent.OnEventSubjectChanged -> {
                _state.update { it.copy(eventSubject = event.subject) }
            }

            is GenerateQrEvent.OnEventDTStartChanged -> {
                _state.update { it.copy(eventDTStart = event.start) }
            }

            is GenerateQrEvent.OnEventDTEndChanged -> {
                _state.update { it.copy(eventDTEnd = event.end) }
            }

            is GenerateQrEvent.OnEventLocationChanged -> {
                _state.update { it.copy(eventLocation = event.location) }
            }

            // 📦 Pick QR Data Type (e.g., URL, SMS, etc.)
            is GenerateQrEvent.OnTypePicked -> {
                _state.update {
                    it.copy(
                        pickedType = event.type,
                        qrBitmap = null,
                        formattedText = "",
                        formattedUrl = "",
                        formattedMail = "",
                        formattedTel = "",
                        formattedSMS = "",
                        formattedGeo = "",
                        formattedEvent = "",
                    )
                }
            }

            is GenerateQrEvent.ShareQRCode -> {
                val bitmap = _state.value.qrBitmap
                viewModelScope.launch {
                    if (bitmap != null) {
                        _uiEvent.send(GenerateQRCodeUiEvent.RequestShare(bitmap))
                    } else {
                        _uiEvent.send(GenerateQRCodeUiEvent.ShowToast("You have to generate a QR code first"))
                    }
                }
            }
            is GenerateQrEvent.GenerateQRCode -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true) }
                    delay(2000)

                    val isFormatted = formatData(_state.value.pickedType)

                    if (!isFormatted) {
                        _uiEvent.send(GenerateQRCodeUiEvent.ShowToast("Invalid or incomplete data"))
                        return@launch
                    }

                    val updatedState = _state.value

                    val data =
                        when (updatedState.pickedType) {
                            QrDataType.URL -> updatedState.formattedUrl
                            QrDataType.MAIL -> updatedState.formattedMail
                            QrDataType.TEL -> updatedState.formattedTel
                            QrDataType.SMS -> updatedState.formattedSMS
                            QrDataType.GEO -> updatedState.formattedGeo
                            QrDataType.EVENT -> updatedState.formattedEvent
                            QrDataType.TEXT -> updatedState.formattedText
                        }

                    _state.update {
                        it.copy(
                            qrBitmap = qrCodeGenerator.generate(data = data, 512, 512),
                            isLoading = false,
                        )
                    }
                }
            }

            //  Clear QR bitmap or output
            is GenerateQrEvent.ClearQRCode -> {
                _state.update {
                    it.copy(
                        pickedType = QrDataType.TEXT,
                        qrBitmap = null,
                        formattedText = "",
                        formattedUrl = "",
                        formattedMail = "",
                        formattedTel = "",
                        formattedSMS = "",
                        formattedGeo = "",
                        formattedEvent = "",
                    )
                }
            }
        }
    }
}
