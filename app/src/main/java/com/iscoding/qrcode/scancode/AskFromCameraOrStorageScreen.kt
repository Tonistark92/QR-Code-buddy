package com.iscoding.qrcode.scancode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.iscoding.qrcode.graph.Screens

@Composable
fun AskFromCameraOrStorageScreen(navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxSize()
    ) {
        Button(onClick = {navController.navigate(Screens.ShowAllImagesScreen)} ) {
            Text(text = "QR From Storage")
        }
        Button(onClick = {navController.navigate(Screens.ScanCode)}) {
            Text(text = "QR From Camera")
        }
    }
}