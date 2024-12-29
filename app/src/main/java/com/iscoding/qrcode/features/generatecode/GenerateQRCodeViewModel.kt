package com.iscoding.qrcode.features.generatecode

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class GenerateQRCodeViewModel  () : ViewModel() {



    private val _state = MutableStateFlow(GenerateQRCodeState())
    val state get() = _state.asStateFlow()
}