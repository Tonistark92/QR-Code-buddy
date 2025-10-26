package com.iscoding.qrcode.features.mainactivity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.iscoding.qrcode.features.mainactivity.intent.MainActivityEffect
import com.iscoding.qrcode.features.mainactivity.intent.MainActivityEvent
import com.iscoding.qrcode.graph.RootNavigationGraph
import com.iscoding.qrcode.graph.Screens
import com.iscoding.qrcode.theme.QrCodeBuddyTheme
import com.iscoding.qrcode.theme.Theme
import com.iscoding.qrcode.util.LocaleHelper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import logcat.logcat
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * The main entry point of the QR Code Buddy application.
 *
 * This activity sets up the app's navigation, handles deep links and share intents,
 * and connects the UI layer with the [MainActivityViewModel] using an MVI pattern.
 */
class MainActivity : ComponentActivity() {

    /**
     * Ensures that the app applies the correct locale before attaching the base context.
     */
    private val mainViewModel: MainActivityViewModel by viewModel()
    private lateinit var navController: NavHostController

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.updateLocale(it) })
    }

    /**
     * Called when the activity is created.
     *
     * - Installs splash screen
     * - Sets up Compose UI
     * - Connects [MainActivityViewModel] state and effects to UI
     * - Handles image share intents and deep links
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            val viewModel = koinViewModel<MainActivityViewModel>()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            val navController = rememberNavController()

            LaunchedEffect(Unit) {
                navController.enableOnBackPressed(true)
            }

            // Loading state overlay
            Box(contentAlignment = Alignment.Center, modifier = Modifier.clip(Theme.shapes.medium)) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(64.dp),
                        color = Color(0xFF138173),
                        trackColor = Color(0xFF8AE5D0),
                    )
                }
                QrCodeBuddyTheme {
                    RootNavigationGraph(navController = navController)
                }
            }
            // you can use this instead of the OnNewIntent()

            // Handle "share image" intents (e.g., from Gallery)
            if (Intent.ACTION_SEND == intent.action && intent.type?.startsWith("image/") == true) {
                viewModel.onEvent(MainActivityEvent.OnNewIntentReceived(intent))
            }
//            LaunchedEffect(Unit) {
//                delay(100) // small delay ensures NavHost attached
//                intent.flags = 0 // ensure no new task flags
//                val handled = navController.handleDeepLink(intent)
//                logcat("DeepLink") { "Handled: $handled" }
//            }

            // Observe effects (side-effects outside state)
            LaunchedEffect(Unit) {
                viewModel.effect
                    .distinctUntilChanged()
                    .collectLatest { effect ->
                        when (effect) {
                            is MainActivityEffect.ShowToast -> {
                                Toast.makeText(
                                    this@MainActivity,
                                    "This is not QR Code Image",
                                    Toast.LENGTH_LONG,
                                ).show()
                            }

                            is MainActivityEffect.NavigateToQrDetailsScreen -> {
                                val encodedQrCode = Uri.encode(effect.qrCode)
                                val encodedImageUri = Uri.encode(effect.imageUri.toString())

//                                val deepLinkUri =
//                                    "qrcodebuddy://${Screens.SHOW_QR_CODE_DATA_SCREEN_DEEP_LINK}/$encodedQrCode/$encodedImageUri"
//                                        .toUri()
//                                logcat("MainActivity") { deepLinkUri.toString() }

                                val deepLinkUri = "qrcodebuddy://${Screens.SHOW_QR_CODE_DATA_SCREEN_DEEP_LINK}/$encodedQrCode/$encodedImageUri".toUri()
                                val intent = Intent(Intent.ACTION_VIEW, deepLinkUri)
                                navController.handleDeepLink(intent)
                                logcat("MainActivity") { navController.handleDeepLink(intent).toString() }
//                                navController.handleDeepLink(effect.intent)
//                                navController.navigate(deepLinkUri)
                            }

                            is MainActivityEffect.AnalyzeImage -> {
                                handleSharedImageIntent(intent, viewModel)
                            }
                        }
                    }
            }
        }
    }

    /**
     * Reads the shared image from the intent and triggers QR code analysis.
     *
     * @param intent The intent containing the shared image.
     * @param viewModel The [MainActivityViewModel] to dispatch the event to.
     */
    private fun handleSharedImageIntent(
        intent: Intent,
        viewModel: MainActivityViewModel,
    ) {
        val imageUri =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
            }

        if (imageUri != null) {
            val inputStream = contentResolver.openInputStream(imageUri)
            inputStream?.use {
                viewModel.onEvent(MainActivityEvent.OnAnalyzeImage(imageUri, it, intent))
            }
        }
    }
//
//    override fun onNewIntent(intent: Intent, caller: ComponentCaller) {
//        super.onNewIntent(intent, caller)
//        logcat("MainActivity") {  intent.data.toString() +" hello" }
//
//        mainViewModel.onEvent(MainActivityEvent.OnNewIntentReceived(intent))
//        if (::navController.isInitialized) {
//            navController.handleDeepLink(intent)
//        }
//        logcat("MainActivity") {  intent.data.toString() +" hello" }
//        logcat("MainActivity") {  " شيسشنيسكينشسكمينسشكيمنkds;laskd;asldkas;ldkas;ldkas;ldkas" }
//
//    logcat { "Parameter intent: ${intent.action}222222222" } // ACTION_SEND ✅
//
//    // But the PROPERTY 'this.intent' still has the OLD intent!
//    logcat { "Property intent: ${this.intent.action}  11111" } // Whatever started the app initially ❌
// }
//
}
