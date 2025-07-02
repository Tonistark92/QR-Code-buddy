package com.iscoding.qrcode

import android.app.Application
import com.iscoding.qrcode.di.appModule
import com.iscoding.qrcode.util.PreferencesManager
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext

class MyApp  : Application() {
    override fun onCreate() {
        PreferencesManager.init(this)
        super.onCreate()
        GlobalContext.startKoin {
            androidContext(this@MyApp)
            androidLogger()
            modules(appModule)
        }
    }
}