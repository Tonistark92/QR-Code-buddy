package com.iscoding.qrcode.util

import android.app.Application
import com.iscoding.qrcode.domain.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class MyApp  : Application() {
    override fun onCreate() {
        PreferencesManager.init(this)
        super.onCreate()
        startKoin {
            androidContext(this@MyApp)
            androidLogger()
            modules(appModule)
        }
    }
}