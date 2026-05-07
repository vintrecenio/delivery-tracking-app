package com.treceniovin.deliverytracking

import android.app.Application
import com.treceniovin.deliverytracking.di.appModule
import com.treceniovin.deliverytracking.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(listOf(networkModule, appModule))
        }
    }
}
