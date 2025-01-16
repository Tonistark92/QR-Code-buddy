package com.iscoding.qrcode.features.generatecode

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class GenerateQRCodeViewModel  () : ViewModel() {



    private val _state = MutableStateFlow(GenerateQRCodeState())
    val state get() = _state.asStateFlow()


    fun updateState(newState: GenerateQRCodeState) {
        Log.d("ISLAM",newState.plainText+"View model")
        _state.value = newState
    }
}