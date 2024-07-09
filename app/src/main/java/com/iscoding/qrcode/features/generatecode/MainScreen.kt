package com.iscoding.qrcode.features.generatecode

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.ScanResult
import android.net.wifi.SoftApConfiguration
import android.net.wifi.SoftApConfiguration.SECURITY_TYPE_OPEN
import android.net.wifi.SoftApConfiguration.SECURITY_TYPE_WPA2_PSK
import android.net.wifi.SoftApConfiguration.SECURITY_TYPE_WPA3_OWE
import android.net.wifi.SoftApConfiguration.SECURITY_TYPE_WPA3_OWE_TRANSITION
import android.net.wifi.SoftApConfiguration.SECURITY_TYPE_WPA3_SAE
import android.net.wifi.SoftApConfiguration.SECURITY_TYPE_WPA3_SAE_TRANSITION
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.NavController
import com.iscoding.qrcode.graph.Screens
import kotlinx.coroutines.delay

@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MainScreen(navController: NavController) {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxSize()
    ) {


        Button(onClick = { navController.navigate(Screens.AskFromCameraOrStorageScreen) }) {
            Text(text = "To Scan QR Code")
        }
        Button(onClick = { navController.navigate(Screens.GenerateCode) }) {
            Text(text = "To Generate QR Code")
        }
    }
}




