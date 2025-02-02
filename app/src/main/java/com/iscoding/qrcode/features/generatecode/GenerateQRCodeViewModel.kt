package com.iscoding.qrcode.features.generatecode

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.Hashtable


class GenerateQRCodeViewModel() : ViewModel() {


    private val _state = MutableStateFlow(GenerateQRCodeState())
    val state get() = _state.asStateFlow()


    fun updateState(newState: GenerateQRCodeState) {
//        Log.d("DATA", newState.url + "View model")
        _state.value = newState
//        Log.d("DATA", state.value.toString() + "View model")

    }


    fun validateInput(chosenData: String, state: GenerateQRCodeState): Boolean {
        return when (chosenData) {
            "Mail" -> {
                val emailPattern = Regex("""[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}""")

                if (_state.value.mail.isNotEmpty() && emailPattern.matches(_state.value.mail)) {
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
                if (_state.value.url.isNotEmpty() && urlPattern.matches(_state.value.url)) {
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
                _state.value.geoLatitude.isNotEmpty() && latitudePattern.matches(_state.value.geoLatitude) &&
                        _state.value.geoLongitude.isNotEmpty() && longitudePattern.matches(_state.value.geoLongitude)

                if (_state.value.geoLatitude.isNotEmpty() && latitudePattern.matches(_state.value.geoLatitude) &&
                    _state.value.geoLongitude.isNotEmpty() && longitudePattern.matches(_state.value.geoLongitude)
                ) {
                    _state.value = _state.value.copy(shouldShowErrorGeoLatitude = false)
                    _state.value = _state.value.copy(shouldShowErrorGeoLongitude = false)
                    true
                } else {
                    if (_state.value.geoLatitude.isNotEmpty() && latitudePattern.matches(_state.value.geoLatitude) == false) {
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


                if (_state.value.eventSubject.isNotEmpty() &&
                    _state.value.eventDTStart.isNotEmpty() && dateTimePattern.matches(_state.value.eventDTStart) &&
                    _state.value.eventDTEnd.isNotEmpty() && dateTimePattern.matches(_state.value.eventDTEnd) &&
                    _state.value.eventLocation.isNotEmpty()
                ) {
                    _state.value = _state.value.copy(shouldShowErrorEventSubject = false)
                    _state.value = _state.value.copy(shouldShowErrorEventLocation = false)
                    _state.value = _state.value.copy(shouldShowErrorEventDTEnd = false)
                    _state.value = _state.value.copy(shouldShowErrorEventDTStart = false)
                    true
                } else {
                    if (_state.value.eventSubject.isEmpty()) {
                        _state.value = _state.value.copy(shouldShowErrorEventSubject = true)
                        false

                    } else if (_state.value.eventLocation.isEmpty()) {
                        _state.value = _state.value.copy(shouldShowErrorEventLocation = true)
                        false
                    } else if (_state.value.eventDTStart.isNotEmpty() && dateTimePattern.matches(
                            _state.value.eventDTStart
                        )
                    ) {
                        _state.value = _state.value.copy(shouldShowErrorEventDTEnd = true)
                        false

                    } else {
                        _state.value = _state.value.copy(shouldShowErrorEventDTStart = true)
                        false
                    }


                }
            }

            "SMS" -> {

                if (_state.value.smsNumber.isNotEmpty() && _state.value.smsData.isNotEmpty()) {
                    _state.value = _state.value.copy(shouldShowErrorSmsData = false)
                    _state.value = _state.value.copy(shouldShowErrorSmsNumber = false)
                    true
                } else {

                    if (_state.value.tel.isEmpty()) {
                        _state.value = _state.value.copy(shouldShowErrorSmsNumber = true)
                        false
                    } else {
                        _state.value = _state.value.copy(shouldShowErrorSmsData = true)

                        false
                    }

                }
            }

            "Tel" -> {

                if (_state.value.tel.isNotEmpty()) {
                    _state.value = _state.value.copy(shouldShowErrorTel = false)
                    true
                } else {

                    _state.value = _state.value.copy(shouldShowErrorTel = true)
                    false

                }
            }

            "Text" -> {
                if (_state.value.plainText.isNotEmpty()) {
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


    fun getImageUri(context: Context, bitmap: Bitmap): Uri? {
        val imagesFolder = File(context.cacheDir, "images")
        var uri: Uri? = null
        try {
            imagesFolder.mkdirs()
            val file = File(imagesFolder, "shared_image.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
            stream.flush()
            stream.close()
            uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return uri
    }

    fun formatData(type: String, state: GenerateQRCodeState): Boolean {
        var isFormattedAndReady = false
        when (type) {
            "URL" -> {
                val isUrlReady = validateInput("URL", _state.value)
                if (isUrlReady) {
//                 viewModel.updateState(state.copy(url = state.url))
                    updateState(_state.value.copy(formattedUrl = _state.value.url))
                    isFormattedAndReady = true
                }
            }

            "Mail" -> {
                val isEmailReady = validateInput("Mail", _state.value)

                if (isEmailReady) {
                    if (_state.value.mail.startsWith("mailto:")) {
                        updateState(_state.value.copy(mail = _state.value.mail))
                        isFormattedAndReady = true

                    } else {
                        updateState(_state.value.copy(formattedMail = "mailto:${_state.value.mail}"))
                        isFormattedAndReady = true


                    }
                }
            }

            "Tel" -> {
                val isPhoneReady = validateInput("Tel", _state.value)
                if (isPhoneReady) {
                    if (_state.value.tel.startsWith("tel:")) {
                        updateState(_state.value.copy(formattedTel = _state.value.tel))
                        isFormattedAndReady = true


                    } else {
                        updateState(_state.value.copy(formattedTel = "tel:${_state.value.tel}"))
                        isFormattedAndReady = true

                    }
                }
            }

            "SMS" -> {
                val isSmsReady = validateInput("SMS", _state.value)
                if (isSmsReady) {
                    if (_state.value.formattedSMS.startsWith("sms:")) {
                        updateState(_state.value.copy(smsData = _state.value.smsData))
                        updateState(_state.value.copy(smsNumber = _state.value.smsNumber))
                        val formatedSMS = """
                         Tel: ${_state.value.smsNumber}
                         data: ${_state.value.smsData}
                     """.trimMargin()

                        updateState(state.copy(formattedSMS = formatedSMS))
                        isFormattedAndReady = true


                    } else {
                        updateState(_state.value.copy(smsData = _state.value.smsData))
                        updateState(_state.value.copy(smsNumber = _state.value.smsNumber))
                        val formatedSMS = """
                         sms:
                         Tel: ${_state.value.smsNumber}
                         data: ${_state.value.smsData}
                     """.trimMargin()

                        updateState(_state.value.copy(formattedSMS = formatedSMS))
                        isFormattedAndReady = true

                    }
                }
            }

            "Geo" -> {
                val isGeoReady = validateInput("Geo", _state.value)
                if (isGeoReady) {
                    if (_state.value.formattedGeo.startsWith("geo:")) {
                        val formatedGeo = """
                         lat: ${_state.value.geoLatitude}
                         long: ${_state.value.geoLongitude}
                     """.trimMargin()

                        updateState(_state.value.copy(formattedGeo = formatedGeo))
                        isFormattedAndReady = true

                    } else {
                        val formatedGeo = """
                         geo:
                         lat: ${_state.value.geoLatitude}
                         long: ${_state.value.geoLongitude}
                     """.trimMargin()

                        updateState(_state.value.copy(formattedGeo = formatedGeo))
                        isFormattedAndReady = true

                    }
                }
            }

            "Event" -> {
                val isSummaryReady = validateInput("Event", state)

                val formattedEvent = """
            BEGIN:EVENT
            SUMMARY:${_state.value.eventSubject}
            DTSTART:${_state.value.eventDTStart}
            DTEND:${_state.value.eventDTEnd}
            LOCATION:${_state.value.eventLocation}
            END:EVENT
            """.trimIndent()
                updateState(state.copy(formattedEvent = formattedEvent))
                isFormattedAndReady = true

            }

            "Text" -> {
                val isTextReady = validateInput("Text", state)
                if (isTextReady) {

                    updateState(_state.value.copy(formattedText = state.plainText))
                    isFormattedAndReady = true

                }

            }

            else -> {
                isFormattedAndReady = false

            }

        }
        return isFormattedAndReady
    }

    @SuppressLint("SuspiciousIndentation")
    fun generateQRCode(
        data: String,
        coroutineScope: CoroutineScope,
        width: Int = 512,
        height: Int = 512
    ) {

//        Log.d("LOADING", "In the Generation")
        Log.d("DATA", data + "IN GENRATION")
        Log.d("DATA", data + "IN GENRATION" + "${_state.value.formattedText}")

        try {
//            Log.d("LOADING", "In the Generation  in try" )

            _state.value = _state.value.copy(
//                 qrBitmap = bmp,
                isLoading = true
            )
            val writer = QRCodeWriter()
            val hints = Hashtable<EncodeHintType, Any>()
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
            val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, width, height, hints)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            coroutineScope.launch(Dispatchers.Default) {
                delay(1000)
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        bmp.setPixel(
                            x,
                            y,
                            if (bitMatrix[x, y]) Color.Black.toArgb() else Color.White.toArgb()
                        )
                    }
                }



                _state.value = _state.value.copy(
                    qrBitmap = bmp,
                    isLoading = false
                )
//                Log.d("DATA", _state.value.qrBitmap.toString() + " ٌٌٌُIMAAGE IN GENRATION")


//                _state.value.isLoading =false
//            Log.d("LOADING", "IS Loading in viewmodel"+"${state.value.isLoading}")
//                Log.d("DATA", "the image      " + "   ${state.value.qrBitmap?.generationId}")

//                bmp
            }


        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("DATA", " ٌٌٌُERRRRORRRRR  IN GENRATION" + e.message)
            Log.d("DATA", " ٌٌٌُERRRRORRRRR  IN GENRATION" + e.printStackTrace())

            null
            //todo show error
        }

    }

}