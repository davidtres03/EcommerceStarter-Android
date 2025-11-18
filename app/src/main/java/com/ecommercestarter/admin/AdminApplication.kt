package com.ecommercestarter.admin

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AdminApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any app-wide components here
    }
}
