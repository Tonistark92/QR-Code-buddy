package com.iscoding.qrcode.features.generatecode

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileOutputStream
import java.util.Hashtable


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

    fun formatData(type: String, state: GenerateQRCodeState) : Boolean{
        var isFormattedAndReady = false
        when (type) {
            "URL" -> {
                val isUrlReady = validateInput("URL", state)
                if (isUrlReady) {
//                 viewModel.updateState(state.copy(url = state.url))
                    updateState(state.copy(formattedUrl = state.url))
                    isFormattedAndReady= true
                }
            }

            "Mail" -> {
                val isEmailReady = validateInput("Mail", state)

                if (isEmailReady) {
                    if (state.mail.startsWith("mailto:")) {
                        updateState(state.copy(mail = state.mail))
                        isFormattedAndReady= true

                    } else {
                        updateState(state.copy(formattedMail = "mailto:${state.mail}"))
                        isFormattedAndReady= true


                    }
                }
            }

            "Tel" -> {
                val isPhoneReady =validateInput("Tel", state)
                if (isPhoneReady) {
                    if (state.tel.startsWith("tel:")) {
                        updateState(state.copy(formattedTel = state.tel))
                        isFormattedAndReady= true


                    } else {
                        updateState(state.copy(formattedTel = "tel:${state.tel}"))
                        isFormattedAndReady= true

                    }
                }
            }

            "SMS" -> {
                val isSmsReady = validateInput("SMS", state)
                if (isSmsReady) {
                    if (state.formattedSMS.startsWith("sms:")) {
                        updateState(state.copy(smsData = state.smsData))
                        updateState(state.copy(smsNumber = state.smsNumber))
                        val formatedSMS = """
                         Tel: ${state.smsNumber}
                         data: ${state.smsData}
                     """.trimMargin()

                        updateState(state.copy(formattedSMS = formatedSMS))
                        isFormattedAndReady= true


                    } else {
                        updateState(state.copy(smsData = "sms:${state.smsData}"))
                        updateState(state.copy(smsNumber = state.smsNumber))
                        val formatedSMS = """
                         sms:
                         Tel: ${state.smsNumber}
                         data: ${state.smsData}
                     """.trimMargin()

                        updateState(state.copy(formattedSMS = formatedSMS))
                        isFormattedAndReady= true

                    }
                }
            }

            "Geo" -> {
                val isGeoReady = validateInput("Geo", state)
                if(isGeoReady) {
                    if (state.formattedGeo.startsWith("geo:")) {
                        val formatedGeo = """
                         lat: ${state.geoLatitude}
                         long: ${state.geoLongitude}
                     """.trimMargin()

                        updateState(state.copy(formattedGeo = formatedGeo))
                        isFormattedAndReady= true

                    } else {
                        val formatedGeo = """
                         geo:
                         lat: ${state.geoLatitude}
                         long: ${state.geoLongitude}
                     """.trimMargin()

                        updateState(state.copy(formattedGeo = formatedGeo))
                        isFormattedAndReady= true

                    }
                }
            }

            "Event" -> {
                val isSummaryReady =  validateInput("Event", state)

                val formattedEvent = """
            BEGIN:EVENT
            SUMMARY:${state.eventSubject}
            DTSTART:${state.eventDTStart}
            DTEND:${state.eventDTEnd}
            LOCATION:${state.eventLocation}
            END:EVENT
            """.trimIndent()
                updateState(state.copy(formattedEvent = formattedEvent))
                isFormattedAndReady= true

            }

            "Text" -> {
                val isTextReady = validateInput("Text", state)
                if (isTextReady){

                    updateState(state.copy(formattedText = state.plainText))
                    isFormattedAndReady= true

                }

            }

            else -> {
                isFormattedAndReady= false

            }

        }
        return isFormattedAndReady
    }

    fun generateQRCode(data: String, width: Int = 512, height: Int = 512): Bitmap? {
        return try {

            val writer = QRCodeWriter()
            val hints = Hashtable<EncodeHintType, Any>()
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
            val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, width, height, hints)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(
                        x,
                        y,
                        if (bitMatrix[x, y]) Color.Black.toArgb() else Color.White.toArgb()
                    )
                }
            }
            bmp
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

}