package com.iscoding.qrcode.features.scan

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.iscoding.qrcode.R
import com.iscoding.qrcode.graph.Screens

@Composable
fun AskFromCameraOrStorageScreen(navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxSize().padding(40.dp).verticalScroll(rememberScrollState()),
    ) {
        Image(
            painter = painterResource(id = R.drawable.storage),
            contentDescription = "Placeholder",
        )
        Button(
            onClick = { navController.navigate(Screens.SHOW_ALL_IMAGES_SCREEN) },
            shape = RectangleShape,
            colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary, // Background color
                contentColor = Color.White, // Text/Icon color
                disabledContainerColor = Color.Gray, // Disabled background
                disabledContentColor = Color.Black, // Disabled text color
            ),
        ) {
            Text(text = "QR From Storage")
        }
        Image(
            painter = painterResource(id = R.drawable.camera),
            contentDescription = "Placeholder",
        )
        Button(
            onClick = {
                navController.navigate(Screens.SCAN_CODE)
            },
            shape = RectangleShape,
            colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary, // Background color
                contentColor = Color.White, // Text/Icon color
                disabledContainerColor = Color.Gray, // Disabled background
                disabledContentColor = Color.Black, // Disabled text color
            ),
        ) {
            Text(text = "QR From Camera")
        }
    }
}
