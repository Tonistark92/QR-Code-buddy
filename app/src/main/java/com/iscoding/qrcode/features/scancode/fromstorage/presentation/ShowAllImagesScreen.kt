package com.iscoding.qrcode.features.scancode.fromstorage.presentation

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.iscoding.qrcode.graph.Screens
import com.iscoding.qrcode.features.scancode.fromstorage.domain.SharedStoragePhoto
import com.iscoding.qrcode.features.scancode.fromstorage.domain.StorageImageAnalyzer
import java.io.IOException
import java.io.InputStream

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ShowAllImagesScreen(navController: NavController) {
    val context = LocalContext.current
    var photosShared by remember { mutableStateOf<List<SharedStoragePhoto>>(emptyList()) }

    val viewModel: ScanQrCodeViewmodel = viewModel()
    val multiplePermissionsState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
    )

    if (multiplePermissionsState.allPermissionsGranted) {
        viewModel.loadShared(context)
        photosShared = viewModel.photosStateShared.value
        Log.d("TAGEff", photosShared.toString())
    }
Box(modifier = Modifier.padding(16.dp).fillMaxSize(), contentAlignment = Alignment.Center){
    if (viewModel.photosStateShared.value.isEmpty()){
        CircularProgressIndicator(
            modifier = Modifier.width(64.dp),
            color = Color(0xFF138173),
            trackColor = Color(0xFF8AE5D0),

        )
    }
    if (photosShared.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(photosShared) { photo ->
                PhotoItem(photo = photo) { selectedUri ->
                    // Handle click on the image here
                    Log.d("ClickedImage", "Clicked on image: $selectedUri")
                    analyzeImage(context.contentResolver,context, selectedUri, navController)
                }
            }
        }
    }
}
}

@Composable
private fun PhotoItem(
    photo: SharedStoragePhoto,
    onItemClick: (Uri) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onItemClick(photo.contentUri) }
    ) {
        Image(
            painter = rememberImagePainter(photo.contentUri),
            contentDescription = null,
            modifier = Modifier.size(250.dp),
            contentScale = ContentScale.Crop
        )
    }
}


private fun analyzeImage(
    contentResolver: ContentResolver,
    context: Context,
    uri: Uri,
    navController: NavController
) {
    try {
        // Open an input stream from the URI
        val inputStream: InputStream? = contentResolver.openInputStream(uri)

        // Decode the input stream into a Bitmap
        val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)

        // Close the input stream after decoding
        inputStream?.close()

        if (bitmap != null) {
            // Perform your image analysis or processing here
            Log.d("analyzeImage", "Bitmap loaded successfully from URI: $uri")

            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream?.use {
                StorageImageAnalyzer (
                    onNoQRCodeFound = {
                    Toast.makeText(context, "There is no QR Code in this image ", Toast.LENGTH_LONG).show()

                } ){ qrCodeData ->
                    // Handle QR code data here, e.g., show a dialog, navigate to a new screen
                    Log.d("ShowAllImagesScreen", "Scanned QR Code: $qrCodeData")
                    // Example: Show a Toast
                    val encodedQrCodeData = Uri.encode(qrCodeData)
                    val encodedImageUri = Uri.encode(uri.toString())
                    Toast.makeText(context, "QR Code Data: $qrCodeData", Toast.LENGTH_LONG).show()
                    navController.navigate("${Screens.ShowQRCodeDataScreen}/$encodedQrCodeData/$encodedImageUri")
                }.analyze(uri, it)
            }
        } else {
            Log.e("analyzeImage", "Failed to load bitmap from URI: $uri")
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}