package com.iscoding.qrcode.features.generatecode

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class GenerateQRCodeViewModel() : ViewModel() {


    private val _state = MutableStateFlow(GenerateQRCodeState())
    val state get() = _state.asStateFlow()


    fun updateState(newState: GenerateQRCodeState) {
        Log.d("ISLAM", newState.plainText + "View model")
        _state.value = newState
    }


    fun validateInput(chosenData: String, state: GenerateQRCodeState): Boolean {
        return when (chosenData) {
            "Mail" -> {
                val emailPattern = Regex("""[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}""")

                if (state.mail.isNotEmpty() && emailPattern.matches(state.mail)) {
                    _state.value = _state.value.copy(shouldShowErrorMail = false)
                    true
                } else {
                    _state.value = _state.value.copy(shouldShowErrorMail = true)
                    false

                }
            }

            "URL" -> {
                val urlPattern =
                    Regex("""https?://(?:www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&//=]*)""")
                if (state.url.isNotEmpty() && urlPattern.matches(state.url)) {
                    _state.value = _state.value.copy(shouldShowErrorUrl = false)
                    true
                } else {
                    _state.value = _state.value.copy(shouldShowErrorUrl = true)
                    false

                }
            }

            "Geo" -> {
                val latitudePattern = Regex("^-?([1-8]?[1-9]|[1-9]0)\\.{0,1}\\d{0,6}$")
                val longitudePattern = Regex("^-?((1?[0-7]?|[0-9]?)[0-9]|180)\\.{0,1}\\d{0,6}$")
                state.geoLatitude.isNotEmpty() && latitudePattern.matches(state.geoLatitude) &&
                        state.geoLongitude.isNotEmpty() && longitudePattern.matches(state.geoLongitude)

                if (state.geoLatitude.isNotEmpty() && latitudePattern.matches(state.geoLatitude) &&
                    state.geoLongitude.isNotEmpty() && longitudePattern.matches(state.geoLongitude)
                ) {
                    _state.value = _state.value.copy(shouldShowErrorGeoLatitude = false)
                    _state.value = _state.value.copy(shouldShowErrorGeoLongitude = false)
                    true
                } else {
                    if (state.geoLatitude.isNotEmpty() && latitudePattern.matches(state.geoLatitude) == false) {
                        _state.value = _state.value.copy(shouldShowErrorGeoLatitude = true)
                        false

                    } else {
                        _state.value = _state.value.copy(shouldShowErrorGeoLongitude = true)
                        false
                    }

                }
            }

            "Event" -> {
                val dateTimePattern = Regex("^\\d{8}T\\d{6}$")


                if (state.eventSubject.isNotEmpty() &&
                    state.eventDTStart.isNotEmpty() && dateTimePattern.matches(state.eventDTStart) &&
                    state.eventDTEnd.isNotEmpty() && dateTimePattern.matches(state.eventDTEnd) &&
                    state.eventLocation.isNotEmpty()
                ) {
                    _state.value = _state.value.copy(shouldShowErrorEventSubject = false)
                    _state.value = _state.value.copy(shouldShowErrorEventLocation = false)
                    _state.value = _state.value.copy(shouldShowErrorEventDTEnd = false)
                    _state.value = _state.value.copy(shouldShowErrorEventDTStart = false)
                    true
                } else {
                    if (state.eventSubject.isEmpty()) {
                        _state.value = _state.value.copy(shouldShowErrorEventSubject = true)
                        false

                    } else if (state.eventLocation.isEmpty()) {
                        _state.value = _state.value.copy(shouldShowErrorEventLocation = true)
                        false
                    } else if (state.eventDTStart.isNotEmpty() && dateTimePattern.matches(state.eventDTStart)) {
                        _state.value = _state.value.copy(shouldShowErrorEventDTEnd = true)
                        false

                    } else {
                        _state.value = _state.value.copy(shouldShowErrorEventDTStart = true)
                        false
                    }


                }
            }

            "SMS" -> {

                if (state.smsNumber.isNotEmpty() && state.smsData.isNotEmpty()) {
                    _state.value = _state.value.copy(shouldShowErrorSmsData = false)
                    _state.value = _state.value.copy(shouldShowErrorSmsNumber = false)
                    true
                } else {

                    if (state.tel.isEmpty()) {
                        _state.value = _state.value.copy(shouldShowErrorSmsNumber = true)
                        false
                    } else {
                        _state.value = _state.value.copy(shouldShowErrorSmsData = true)

                        false
                    }

                }
            }

            "Tel" -> {

                if (state.tel.isNotEmpty()) {
                    _state.value = _state.value.copy(shouldShowErrorTel = false)
                    true
                } else {

                    _state.value = _state.value.copy(shouldShowErrorTel = true)
                    false

                }
            }

            "Text" -> {
                if (state.plainText .isNotEmpty()) {
                    _state.value = _state.value.copy(shouldShowErrorPlainText = false)
                    true
                } else {

                    _state.value = _state.value.copy(shouldShowErrorPlainText = true)
                    false

                }

            }

            else -> false
        }
    }


}