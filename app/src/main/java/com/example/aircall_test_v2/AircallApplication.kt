package com.example.aircall_test_v2

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AircallApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}