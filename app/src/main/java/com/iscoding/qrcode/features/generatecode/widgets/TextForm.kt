package com.iscoding.qrcode.features.generatecode.widgets

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iscoding.qrcode.features.generatecode.GenerateQRCodeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//@Composable
//fun TextForm(
//   plainText:String
//) {
//    var plainText: String = ""
//    val coroutineScope = rememberCoroutineScope()
//
//    TextField(
//        value = plainText,
//        onValueChange = { newText ->
//            plainText = newText
//
//            coroutineScope.launch(Dispatchers.Default) {
//                delay(3000)
//                state.value.shouldShowErrorPlainText = newText.length < 4
//            }
//
//        },
//        label = { Text("Type The Text") },
//    )
//    Spacer(modifier = Modifier.height(6.dp))
//    if (state.value.shouldShowErrorGeoLatitude) {
//        Text(text = state.value.errorMessagePlainText)
//        Spacer(modifier = Modifier.height(6.dp))
//    }
//
//
//}

@Composable
fun TextInput(state: GenerateQRCodeState, updateState: (GenerateQRCodeState) -> Unit) {
    ValidatedTextField(
        label = "Type The Text",
        value = state.plainText,
        errorMessage = state.errorMessagePlainText,
        shouldShowError = state.shouldShowErrorPlainText,
        onValueChange = { newText ->
            updateState(state.copy(plainText = newText))
        }
    )
}
