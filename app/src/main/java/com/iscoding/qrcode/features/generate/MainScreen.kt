package com.iscoding.qrcode.features.generate

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.LocaleManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.iscoding.qrcode.R
import com.iscoding.qrcode.graph.Screens
import com.iscoding.qrcode.util.LocaleHelper
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MainScreen(navController: NavController) {
    val context = LocalContext.current
//    val mainViewModel = koinViewModel<MainViewModel>()
//    val languageCode by mainViewModel.state.collectAsState()
//    val multiplePermissionsState = rememberMultiplePermissionsState(
//        listOf(
//            android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA
//        )
//    )
//
//    var hasAllPermission by remember {
//        mutableStateOf(
//            ContextCompat.checkSelfPermission(
//                context,
//                Manifest.permission.CAMERA
//            ) == PackageManager.PERMISSION_GRANTED &&
//                    ContextCompat.checkSelfPermission(
//                        context,
//                        Manifest.permission.READ_EXTERNAL_STORAGE
//                    ) == PackageManager.PERMISSION_GRANTED
//        )
//    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxSize().padding(40.dp).verticalScroll(rememberScrollState()),

        ) {
//        val launcher = rememberLauncherForActivityResult(
//            contract = ActivityResultContracts.RequestPermission(),
//            onResult = { granted ->
//                hasAllPermission = granted
//            }
//        )
//        LaunchedEffect(key1 = true) {
////            launcher.launch(Manifest.permission.CAMERA)
//
//        multiplePermissionsState.launchMultiplePermissionRequest()
//        }

        Button(onClick = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                changeLanguage("ar",  context.applicationContext)

            } else {
                changeLanguage("ar",  context.applicationContext)
                (context as? Activity)?.recreate()
            }
        }
        ) {
            Text(text = stringResource(R.string.hello))
        }

        Button(onClick = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                changeLanguage("en",  context.applicationContext)

            } else {
                changeLanguage("en",  context.applicationContext)
                (context as? Activity)?.recreate()
            }
        }
        ) {
            Text(text = stringResource(R.string.hello))
        }
        Image(
            painter = painterResource(id = R.drawable.scanner),
            contentDescription = "Placeholder"
        )

        Button(onClick = { navController.navigate(Screens.AskFromCameraOrStorageScreen) },
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary, // Background color
                contentColor = Color.White, // Text/Icon color
                disabledContainerColor = Color.Gray, // Disabled background
                disabledContentColor = Color.Black // Disabled text color
            )) {
            Text(text = "To Scan QR Code")
        }
        Image(
            painter = painterResource(id = R.drawable.qr_code_generation),
            contentDescription = "Placeholder"
        )
        Button(onClick = { navController.navigate(Screens.GenerateCode) },
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary, // Background color
                contentColor = Color.White, // Text/Icon color
                disabledContainerColor = Color.Gray, // Disabled background
                disabledContentColor = Color.Black // Disabled text color
            )) {
            Text(text = "To Generate QR Code")
        }

    }

}
fun changeLanguage(languageTag: String, context: Context) {
    val newLocale = Locale.forLanguageTag(languageTag)
    LocaleHelper.setLocale(context, newLocale)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.getSystemService(LocaleManager::class.java)
            .applicationLocales = android.os.LocaleList.forLanguageTags(languageTag)
    } else {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageTag))
    }

}




