package com.iscoding.qrcode

import android.app.Application
import com.iscoding.qrcode.di.appModule
import com.iscoding.qrcode.util.PreferencesManager
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext

/**
 * Custom [Application] class for the QR Code app.
 *
 * This class is the entry point of the app and is responsible for:
 * - Initializing global utilities such as [PreferencesManager].
 * - Starting the Koin dependency injection framework with [appModule].
 * - Setting up Logcat logging for debugging purposes.
 *
 * ### Responsibilities
 * - Provide a central place for dependency injection configuration.
 * - Install debug logging with [AndroidLogcatLogger].
 *
 * ### Usage
 * Declare this class in your **AndroidManifest.xml**:
 * ```
 * <application
 *     android:name=".MyApp"
 *     android:icon="@mipmap/ic_launcher"
 *     android:label="@string/app_name">
 *     ...
 * </application>
 * ```
 */
class MyApp : Application() {

    /**
     * Called when the application is starting, before any activity,
     * service, or receiver objects have been created.
     *
     * - Initializes [PreferencesManager].
     * - Starts Koin with [appModule].
     * - Configures Logcat logging.
     */
    override fun onCreate() {
        // Initialize SharedPreferences manager
        PreferencesManager.init(this)

        super.onCreate()

//        SentryAndroid.init(this) { options ->
//            options.isEnabled = false
//            options.dsn = BuildConfig.SENTRY_DSN
//            options.tracesSampleRate = 1.0 // enable performance tracing
//            options.isEnableAutoSessionTracking = true
//        }

        // Start Koin dependency injection
        GlobalContext.startKoin {
            androidContext(this@MyApp)
            androidLogger()
            modules(appModule)
        }

        // Enable Logcat logger for debug builds
        AndroidLogcatLogger.installOnDebuggableApp(
            this,
            minPriority = LogPriority.VERBOSE,
        )
    }
}
